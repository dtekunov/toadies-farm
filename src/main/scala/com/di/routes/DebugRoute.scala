package com.di.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.model.HttpMethods.{GET, POST}
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, Uri}
import akka.http.scaladsl.server.Route
import com.di.actors.FarmActorCommand
import com.di.db.MongoFarmsConnector
import akka.http.scaladsl.server.Directives._
import com.di.jsonFormatters.FormatDoc.toOwner
import com.di.jsonFormatters.JsonWriter.formatOwnersSeq
import com.di.utils.responses._
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object DebugRoute {

  def apply(db: MongoFarmsConnector, farmRegistry: ActorRef[FarmActorCommand])
           (implicit system: ActorSystem[_], ec: ExecutionContext): Route = {
      extractRequest { request =>
        if (checkAuthHeader(request.headers, system.settings.config)) {
          request match {
            case HttpRequest(GET, Uri.Path("/debug/ping"), _, _, _) => pongResp

            case HttpRequest(GET, Uri.Path("/debug/get-owners"), _, _, _) =>
              onComplete(db.getAllOwners) {
                case Success(owners) =>
                  val responseBody = formatOwnersSeq(owners.map(doc => toOwner(doc)))
                  allOwnersResponse(responseBody)
                case Failure(ex) => internalServerError(ex.getMessage)
              }

            case HttpRequest(GET, Uri.Path("/debug/get-all-toads"), _, _, _) =>
              onComplete(db.getAllToadsFromDb) {
                case Success(toads) =>
                  val responseBody = toads.map(toad => toad.toJson()).toString() //todo: rewrite
                  allToadsResponse(responseBody)
              }
            case _ => notAllowedResp
          }
        } else authFailedResp
      }
  }

  private def checkAuthHeader(headers: Seq[HttpHeader], config: Config) = { //todo: improve auth logic
    val salt = config.getString("main.debug.salt")
    headers.exists(header => header.is("authorization") && header.value() == salt)
  }
}
