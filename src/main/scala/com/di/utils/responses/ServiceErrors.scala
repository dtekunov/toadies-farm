package com.di.utils.responses

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

trait ServiceErrors extends BaseHttp {

  def internalServerError(msg: String): StandardRoute =
    complete(internalServerErrorResponse(msg))

  val invalidActorMessageError: StandardRoute =
    complete(internalServerErrorResponse("Invalid actor message was provided"))

  val toadsActorNotInitializedError: StandardRoute =
    complete(internalServerErrorResponse("Toads actor not initialized"))

  def mongoError(msg: String): StandardRoute =
    complete(internalServerErrorResponse(msg))

  val notAllowedResp: StandardRoute =
    complete(methodNotAllowedResponse)

  val pongResp: StandardRoute =
    complete(standardOkResponse("pong"))

}
