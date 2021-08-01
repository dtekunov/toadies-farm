package com.di.utils

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

package object responses
  extends ToadBasedResponses
    with FarmBasedResponses
    with OwnerBasedResponses
    with ServiceErrors
    with InputErrors {

  def cycleStartedResp(): StandardRoute =
    complete(standardOkResponse("Cycle started"))

  def cycleStoppedResp(): StandardRoute =
    complete(standardOkResponse("Cycle stopped"))

}
