package com.di

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContextExecutor
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.ActorContext
import com.di.actors.FarmActorCommand
import com.di.db.MongoFarmsConnector.initiateDb
import com.di.routes.mutable.Interface
import com.di.routes.{CreativeActionsRoute, DebugRoute, OtherActionsRoute}
import com.di.utils.responses.noFarmIsInUseResp
import com.typesafe.config.Config

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
      pathPrefix("help") {
        complete("TODO")
      } ~
      pathPrefix("debug") {
        DebugRoute(db, farmRegistry)(system, ec)
      } ~
      pathEndOrSingleSlash {
        complete("You can use .../help for help")
      }
  }
}
