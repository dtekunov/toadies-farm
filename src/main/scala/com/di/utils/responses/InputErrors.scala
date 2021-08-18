package com.di.utils.responses

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

trait InputErrors extends BaseHttp {

  def invalidParameterProvidedResponse(parameter: String): StandardRoute =
    complete(badRequestResponse(s"Invalid parameter provided: $parameter"))

  def authFailedResp: StandardRoute =
    complete(expectationFailed("Auth failed"))

  def invalidToadStructureResp(): StandardRoute =
    complete(badRequestResponse("Invalid json provided"))

}
