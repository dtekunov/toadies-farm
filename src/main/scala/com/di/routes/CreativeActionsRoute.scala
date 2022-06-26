package com.di.routes

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Route
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.di.actors._
import com.di.db.MongoFarmsConnector
import akka.actor.typed.scaladsl
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AskPattern._
import com.di.domain.{DeadToad, Farm, GrownToad, Mode, Owner, Tadpole, Toad}
import com.di.jsonFormatters.JsonReader.rawStringToRawGrownToad
import com.di.jsonFormatters.{FormatDoc, JsonWriter}
import com.di.jsonFormatters.JsonWriter.{formatDeadToadsSeq, formatFarmsSeq, formatGrownToadsSeq, formatTadpolesSeq}
import com.di.jsonFormatters.rawdataFormats.RawGrownToad
import com.di.toadsArithmetics.ToadsArithmetics
import com.di.utils
import com.di.utils.responses._
import com.di.utils.{extractRequestEntityAsString, getFoodValue, validateId, validateMode}
import org.json4s.{JObject, JString}
import org.mongodb.scala.Document

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

//todo: move to another package
object CreativeActionsRoute extends ToadsArithmetics with CreativeActionsActorCommands {

  def apply(farm: Farm)
           (db: MongoFarmsConnector, farmRegistry: ActorRef[FarmActorCommand])
           (implicit system: ActorSystem[_], ec: ExecutionContext): Route = {

    implicit val timeout: Timeout =
      Timeout.create(system.settings.config.getDuration("main.routes.ask-timeout"))

    lazy val maybeToadsActor: Future[Option[ActorRef[ToadsActorCommand]]] =
      farmRegistry.ask(GetToadsActorRef(system, _)).map(answer => answer.toadsActor)

    onComplete(maybeToadsActor) {
      case Success(Some(toadsActor)) =>
        get {
          pathPrefix("owner-info") {
            onComplete(getOwnerInfo(farmRegistry)) {
              case Success(answer) =>
                val responseBody = answer.owner.map(owner => JsonWriter.format(owner)).getOrElse("")
                ownerResponse(responseBody)

              case Failure(ex) => internalServerError(ex.getMessage)
            }
          } ~
            pathPrefix("get-all-toads") {
              onComplete(getAllToads(toadsActor)) {
                case Success(res) =>
                  res match {
                    case maybeToadsResponse: MaybeToadsResponse =>
                      val toads = maybeToadsResponse.maybeToads.getOrElse(Vector.empty[Toad])
                      val toadsGrouped = groupToadsByType(toads)
                      val responseBody = JsonWriter.format(toadsGrouped)
                      allToadsResponse(responseBody)
                    case _ => invalidActorMessageError
                  }
                case Failure(ex) => internalServerError(ex.getMessage)
              }
            } ~
            pathPrefix("total-toads") {
              onComplete(getAllToads(toadsActor)) {
                case Success(response) => response match {
                  case MaybeToadsResponse(Some(toads)) =>
                    val res = JsonWriter.format(countToadsByType(toads))
                    totalToadsResponse(res)
                  case _ => invalidActorMessageError
                }
                case Failure(ex) => internalServerError(ex.getMessage)
              }
            } ~
            pathPrefix("pair-toads") {
              parameters("id1".as[String], "id2".as[String]) { (maybeId1, maybeId2) =>
                (validateId(maybeId1), validateId(maybeId2)) match {
                  case (Some(id1), Some(id2)) =>
                    onComplete(
                      for {
                        toadFut1 <- getToadById(toadsActor, id1.toString)
                        toadFut2 <- getToadById(toadsActor, id2.toString)
                      } yield (toadFut1, toadFut2)
                    ) {
                      case Success(res) => res match {
                        case (resp1: SingleToadResponse, resp2: SingleToadResponse) =>
                          (resp1.maybeToad, resp2.maybeToad) match {
                            case (Some(toad1: GrownToad), Some(toad2: GrownToad)) =>
                              val pairingResult = pairTwoToads(toad1, toad2)
                              if (pairingResult.arePaired) {
                                onComplete(
                                  for {
                                    updateF1 <- updateToad(toadsActor, toad1, pairingResult.toad1)
                                    updateF2 <- updateToad(toadsActor, toad2, pairingResult.toad2)
                                  } yield (updateF1, updateF2)
                                ) {
                                  case Success(_) => toadsPairedResp
                                  case Failure(ex) => internalServerError(ex.getMessage)
                                }
                              } else toadsNotPairedResp
                            case (Some(_), Some(_)) => toadIsNotGrownResp
                            case _ => toadNotFoundResponse(id1 + " | " + id2)
                          }
                        case _ => internalServerError("Invalid message type acquired")
                      }
                    }
                  case (_, _) => invalidParameterProvidedResponse(maybeId1 + " : " + maybeId2)
                }
              }
            } ~
            pathPrefix("feed-toad") {
              parameters("id".as[String], "food".as[String]) { (maybeId, maybeFood) =>
                (getFoodValue(maybeFood), validateId(maybeId)) match {
                  case (Some(foodValue), Some(id)) =>
                    onComplete(getToadById(toadsActor, id.toString)) {
                      case Success(singleToadResponse: SingleToadResponse) =>
                        singleToadResponse.maybeToad match {
                          case Some(toad) =>
                            val newToad = toad.decreaseHunger(foodValue)
                            onComplete(updateToad(toadsActor, toad, newToad)) {
                              case Success(_) => toadFedResp
                              case Failure(ex) => internalServerError(ex.getMessage)
                            }
                          case None => toadNotFoundResponse(id.toString)
                        }
                    }
                  case (_, _) => invalidParameterProvidedResponse(maybeId + " : " + maybeFood)
                }
              }
            }
        } ~
          post {
            extractRequest { req =>
              pathPrefix("start-cycle") {
                onComplete(startCycle(farmRegistry)) {
                  case Success(_) =>
                    cycleStartedResp()

                  case Failure(ex) => internalServerError(ex.getMessage)
                }
              } ~
                pathPrefix("stop-cycle") {
                onComplete(stopCycle(farmRegistry)) {
                  case Success(_) =>
                    cycleStoppedResp()

                  case Failure(ex) => internalServerError(ex.getMessage)
                }
              } ~
                pathPrefix("add-grown-toad") {
                onComplete(extractRequestEntityAsString(req)(system)) {
                  case Success(rawToad) =>
                    rawStringToRawGrownToad(rawToad) match {
                      case Success(rawGrownToad) =>
                        onComplete(addGrownToad(toadsActor, rawGrownToad)) {
                          case Success(_) => toadAddedResp
                          case Failure(ex) => internalServerError(ex.getMessage)
                        }
                      case Failure(_) => invalidToadStructureResp()
                    }
                  case Failure(ex) => internalServerError(ex.getMessage)
                }
              } ~
                pathPrefix("born-random-toad") {
                onComplete(bornRandom(toadsActor)) {
                  case Success(res) => res match {
                    case response: DefaultToadsActorResponse if response.result.nonEmpty =>
                      toadBornResponse
                    case response: DefaultToadsActorResponse =>
                      internalServerError(s"Cannot born random toad due to ${response.description}")
                    case _ => invalidActorMessageError
                  }
                  case Failure(ex) => internalServerError(ex.getMessage)
                }
              } ~
                pathPrefix("save") {
                complete("TODO")
              }
            }
          } ~
          delete {
            pathPrefix("kill-toad") {
              parameter("id".as[String]) { maybeId =>
                validateId(maybeId) match {
                  case Some(uuid) =>
                    onComplete(killToadById(toadsActor, uuid.toString)) {
                      case Success(_) => toadKilledResp
                      case Failure(ex) => internalServerError(ex.getMessage)
                    }
                  case None => invalidParameterProvidedResponse(maybeId)
                }
              }
            } ~
              pathPrefix("remove-dead-bodies") {
              onComplete(removeDeadBodies(toadsActor)) {
                case Success(_) => deadBodiesRemoved
                case Failure(ex) => internalServerError(ex.getMessage)
              }
            }
          }
      case Success(None) => toadsActorNotInitializedError
      case Failure(ex) => internalServerError(ex.getMessage)
    }
  }
}
