package com.di.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.di.actors.FarmActorCommand
import com.di.db.MongoFarmsConnector
import com.di.domain.Farm

import scala.concurrent.ExecutionContext

object OtherActionsRoute {

  def apply(farm: Farm)
           (db: MongoFarmsConnector, farmRegistry: ActorRef[FarmActorCommand])
           (implicit system: ActorSystem[_], ec: ExecutionContext): Route =
    get {
      pathPrefix("") {
        complete("TODO")
      }

    } ~
      post {
        pathPrefix("") {
          complete("TODO")
        }
      }

}