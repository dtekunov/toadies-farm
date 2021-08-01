package com.di

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.di.actors.{ContinueFarm, FarmActorActionPerformed, FarmActorCommand, StartNewFarm, StopUsingFarm}
import com.di.db.MongoFarmsConnector
import com.di.db.MongoFarmsConnector.initiateDb
import com.di.domain.{Farm, Owner, Toad}
import com.di.jsonFormatters.FormatDoc
import com.di.jsonFormatters.JsonWriter.formatFarmsSeq
import com.di.routes.mutable.Interface
import com.di.routes.{CreativeActionsRoute, DebugRoute, OtherActionsRoute}
import com.di.utils.validateMode
import com.di.utils.responses._
import com.di.utils.responses.noFarmIsInUseResp
import com.typesafe.config.Config

import java.util.UUID
import scala.util.{Failure, Success}

class Routes(context: ActorContext[_], farmRegistry: ActorRef[FarmActorCommand]) extends Interface {
  implicit val system: ActorSystem[_] = context.system

  private val config: Config = system.settings.config
  private implicit val ec: ExecutionContextExecutor = system.executionContext

  val routes: Route = {
    val db = initiateDb(config)(ec)

      pathPrefix("interface") {
        interfaceRoute(db, farmRegistry)(system, ec)
      } ~
      pathPrefix("actions") {
        (FARM_VAR, MODE_VAR) match {
          case (Some(farm), Some(mode)) if mode.isCreative =>
            CreativeActionsRoute(farm)(db, farmRegistry)(system, ec)

          case (Some(farm), Some(_)) =>
            OtherActionsRoute(farm)(db, farmRegistry)(system, ec)

          case _ => noFarmIsInUseResp()
        }
      } ~
      pathPrefix("service") {
        complete("TODO")
      } ~
      pathPrefix("debug") {
        DebugRoute(db, farmRegistry)(system, ec)
      }
  }
}
