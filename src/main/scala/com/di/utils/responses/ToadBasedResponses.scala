package com.di.utils.responses

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

trait ToadBasedResponses extends BaseHttp {

  private def toadSuccessfullyAction(actionDone: String) =
    complete(standardOkResponse(s"Toad successfully $actionDone"))

  val toadBornResponse: StandardRoute = toadSuccessfullyAction("born")

  val toadAddedResp: StandardRoute = toadSuccessfullyAction("added")

  val toadKilleddResp: StandardRoute = toadSuccessfullyAction("killed")

  def allToadsResponse(toads: String): StandardRoute =
    complete(okResponseAsIs(toads))

}
