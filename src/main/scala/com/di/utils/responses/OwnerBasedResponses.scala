package com.di.utils.responses

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

trait OwnerBasedResponses extends BaseHttp {

  def ownerResponse(owner: String): StandardRoute =
    complete(okResponseAsIs(owner))

  def allOwnersResponse(owners: String): StandardRoute =
    complete(okResponseAsIs(owners))

  def noSuchOwnerError(farmName: String): StandardRoute =
    complete(notFoundResponse(s"Owner with farm name $farmName not found"))

}
