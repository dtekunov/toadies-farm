package com.di.utils.responses

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

trait InputErrors extends BaseHttp {

  def invalidModeProvidedResponse(mode: String): StandardRoute =
    complete(badRequestResponse(s"Invalid mode parameter provided: $mode"))

  def invalidIdProvidedResponse(id: String): StandardRoute =
    complete(badRequestResponse(s"Invalid id parameter provided: $id"))

  def authFailedResp: StandardRoute =
    complete(expectationFailed("Auth failed"))

  def invalidToadStructureResp(): StandardRoute =
    complete(badRequestResponse("Invalid json provided"))

}
