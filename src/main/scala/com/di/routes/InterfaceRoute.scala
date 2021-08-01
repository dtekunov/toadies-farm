//package com.di.routes
//
//import akka.actor.typed.ActorSystem
//import akka.http.scaladsl.server.Route
//import akka.actor.typed.{ActorRef, ActorSystem}
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.server.Route
//import akka.util.Timeout
//import com.di.actors.{ContinueFarm, FarmActorActionPerformed, FarmActorCommand, StartNewFarm, StopUsingFarm}
//import com.di.db.MongoFarmsConnector
//import akka.actor.typed.scaladsl
//import akka.actor.typed.scaladsl.ActorContext
//import akka.actor.typed.scaladsl.AskPattern._
//import com.di.domain.{Farm, Owner, Toad}
//import com.di.jsonFormatters.FormatDoc
//import com.di.jsonFormatters.JsonWriter.formatFarmsSeq
//import com.di.utils
//import com.di.utils.Responses._
//import com.di.utils.{GlobalScope, validateMode}
//import org.mongodb.scala.Document
//
//import java.util.UUID
//import scala.concurrent.{ExecutionContext, Future}
//import scala.concurrent.ExecutionContext
//import scala.util.{Failure, Success}
//
//object InterfaceRoute extends GlobalScope {
//
//  def interfaceRoute(db: MongoFarmsConnector,
//            farmRegistry: ActorRef[FarmActorCommand],
//            context: ActorContext[_])
//           (implicit system: ActorSystem[_], ec: ExecutionContext): Route = {
//
//    implicit val timeout: Timeout =
//      Timeout.create(system.settings.config.getDuration("main.routes.ask-timeout"))
//
//    def useFarm(farm: Farm, owner: Owner, toads: Vector[Toad]): Future[FarmActorActionPerformed] =
//      farmRegistry.ask(ContinueFarm(farm, owner, toads, system, _))
//
//    def useNewFarm(farm: Farm, owner: Owner): Future[FarmActorActionPerformed] =
//      farmRegistry.ask(StartNewFarm(farm, owner, system, _))
//
//    def stopUsingFarmActor(): Future[FarmActorActionPerformed] =
//      farmRegistry.ask(StopUsingFarm(system, _))
//
//    get {
//      pathPrefix("available-farms") {
//        onComplete(db.getAllFarms) {
//          case Success(farms) =>
//            availableFarmsResponse(formatFarmsSeq(farms.map(farm => FormatDoc.toFarm(farm))))
//          case Failure(ex) =>
//            internalServerError(ex.getMessage)
//        }
//      } ~
//        pathPrefix("use-farm") {
//          parameter("name") { name => //TODO: validate name
//            onComplete(db.getFarmByName(name)) {
//              case Success(Some(rawFarm)) =>
//                val farm = FormatDoc.toFarm(rawFarm)
//                onComplete(db.getAllToadsByFarm(farm.name)) {
//                  case Success(toads) =>
//                    onComplete(db.getOwnerByFarmName(farm.name)) {
//                      case Success(Some(ownerDoc)) =>
//                        val owner = FormatDoc.toOwner(ownerDoc)
//                        onComplete(useFarm(farm, owner, toads)) {
//                          case Success(_) =>
//                            FARM_VAR = Some(farm)
//                            MODE_VAR = utils.validateMode(farm.mode)
//                            farmAvailableResponse(name)
//                          case Failure(ex) =>
//                            internalServerError(ex.getMessage)
//                        }
//                      case Success(None) => noSuchOwnerError(farm.name)
//                      case Failure(ex) => internalServerError(ex.getMessage)
//                    }
//                  case Failure(ex) => internalServerError(ex.getMessage)
//                }
//              case Success(None) => noSuchFarm(name)
//              case Failure(ex) => internalServerError(ex.getMessage)
//            }
//          }
//        }
//    } ~
//      post {
//        pathPrefix("new-farm") {
//          parameters("name".as[String], "mode".as[String], "is_cannibal".as[Boolean]) { //TODO: validate mode and name
//            (name, rawMode, isCannibal) =>
//              validateMode(rawMode) match {
//                case Some(mode) =>
//                  val farm = Farm(UUID.randomUUID().toString, name, mode.name, isCannibal)
//                  onComplete(db.getFarmByName(farm.name)) {
//                    case Success(None) =>
//                      onComplete(db.insertSingleFarm(farm)) {
//                        case Success(Some(_)) =>
//                          val owner = Owner.createNew(farm.name, mode)
//                          onComplete(db.insertSingleOwner(owner)) {
//                            case Success(Some(_)) =>
//                              onComplete(useNewFarm(farm, owner)) {
//                                case Success(_) =>
//                                  FARM_VAR = Some(farm)
//                                  MODE_VAR = utils.validateMode(farm.mode)
//                                  farmAddedResponse(farm.name)
//                                case Failure(ex) => internalServerError(ex.getMessage)
//                              }
//                            case Failure(ex) => internalServerError(ex.getMessage)
//                          }
//                        case Failure(ex) => internalServerError(ex.getMessage)
//                      }
//                    case Success(Some(_)) => farmAlreadyExistsResponse(farm.name)
//                    case Failure(ex) => internalServerError(ex.getMessage)
//                  }
//                case None => invalidModeProvidedResponse(rawMode)
//              }
//          }
//        } ~ pathPrefix("stop-using-farm") {
//          parameter("name".as[String]) { name =>
//            onComplete(db.getFarmByName(name)) {
//              case Success(Some(_)) =>
//                FARM_VAR match {
//                  case Some(currentFarmUsed) if currentFarmUsed.name == name =>
//                    onComplete(stopUsingFarmActor()) {
//                      case Success(res) if res.description == "ok" =>
//                        FARM_VAR = None
//                        MODE_VAR = None
//                        farmIsNowNotInUse(name)
//                      case Success(_) =>
//                        noFarmIsInUse()
//                      case Failure(ex) => internalServerError(ex.getMessage)
//                    }
//                  case Some(currentFarmUsed) => anotherFarmIsInUse(currentFarmUsed.name)
//                  case None => noFarmIsInUse()
//                }
//              case Success(None) => noSuchFarm(name)
//              case Failure(ex) => internalServerError(ex.getMessage)
//            }
//          }
//        }
//        //        ~
//        //          pathPrefix("rename-farm") {
//        //            parameters("old-name".as[String], "new-name".as[String]) { (oldName, newName) => //TODO: validate name
//        //              onComplete(db.getFarmByName(oldName)) {
//        //                case Success(Some(_)) =>
//        //                  onComplete(db.updateFarmName(oldName, newName)) {
//        //                    case Success(Some(_)) => farmUpdated(oldName, newName)
//        //                    case Failure(ex) => internalServerError(ex.getMessage)
//        //                  }
//        //                case Success(None) => noSuchFarm(oldName)
//        //                case Failure(ex) => internalServerError(ex.getMessage)
//        //              }
//        //            }
//        //          }
//      } ~
//      delete {
//        pathPrefix("delete-farm") {
//          parameter("name".as[String]) { name => //TODO: validate name
//            onComplete(db.deleteFarmByName(name)) { // remove from db
//              case Success(deletionResult) =>
//                system.log.info(s"${deletionResult.toadsDeleted} toads were deleted")
//                onComplete(stopUsingFarmActor()) {
//                  case Success(_) =>
//                    context.stop(farmRegistry)
//                    farmDeletedResponse(name)
//                  case Failure(ex) =>
//                    internalServerError(ex.getMessage)
//                }
//              case Failure(ex) => internalServerError(ex.getMessage)
//            }
//          }
//        }
//      }
//  }
//}
