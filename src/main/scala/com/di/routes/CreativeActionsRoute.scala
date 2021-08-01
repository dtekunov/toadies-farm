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
import com.di.utils.{extractRequestEntityAsString, validateId, validateMode}
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

    //todo: optimize to calculate it once
    lazy val maybeToadsActor: Future[Option[ActorRef[ToadsActorCommand]]] =
      farmRegistry.ask(GetToadsActorRef(system, _)).map(answer => answer.toadsActor)

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
          onComplete(maybeToadsActor) {
            case Success(Some(toadsActor)) =>
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
            case Success(None) => internalServerError("No toads actor provided")
            case Failure(ex) => internalServerError(ex.getMessage)
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
          } ~ pathPrefix("stop-cycle") {
            onComplete(stopCycle(farmRegistry)) {
              case Success(_) =>
                cycleStoppedResp()

              case Failure(ex) => internalServerError(ex.getMessage)
            }
          } ~ pathPrefix("add-grown-toad") {
            onComplete(extractRequestEntityAsString(req)(system)) {
              case Success(rawToad) =>
                rawStringToRawGrownToad(rawToad) match {
                  case Success(rawGrownToad) =>
                    onComplete(maybeToadsActor) {
                      case Success(Some(toadsActor)) =>
                        onComplete(addGrownToad(toadsActor, rawGrownToad)) {
                          case Success(_) => toadAddedResp
                          case Failure(ex) => internalServerError(ex.getMessage)
                        }
                      case Success(None) => toadsActorNotInitializedError
                      case Failure(ex) => internalServerError(ex.getMessage)
                    }
                  case Failure(_) => invalidToadStructureResp()
                }
              case Failure(ex) => internalServerError(ex.getMessage)
            }
          } ~ pathPrefix("born-random-toad") {
            onComplete(maybeToadsActor) {
              case Success(maybeToadsActorRef) => maybeToadsActorRef match {
                case Some(toadsActor) =>
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
                case None => internalServerError("No toads actor provided")
              }
              case Failure(ex) => internalServerError(ex.getMessage)
            }
          }
        }
      } ~
      delete {
        pathPrefix("kill-toad") {
          parameter("id".as[String]) { maybeId =>
            validateId(maybeId) match {
              case Some(uuid) =>
                onComplete(maybeToadsActor) {
                  case Success(Some(toadsActor)) =>
                    onComplete(killToadById(toadsActor, uuid.toString)) {
                      case Success(_) => toadKilleddResp
                      case Failure(ex) => internalServerError(ex.getMessage)
                    }
                  case Success(None) => toadsActorNotInitializedError
                  case Failure(ex) => internalServerError(ex.getMessage)
                }
              case None => invalidIdProvidedResponse(maybeId)
            }
          }
        }
      }
  }
}
