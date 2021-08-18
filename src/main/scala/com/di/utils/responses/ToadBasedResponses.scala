package com.di.utils.responses

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

trait ToadBasedResponses extends BaseHttp {

  private def toadSuccessfullyAction(actionDone: String) =
    complete(standardOkResponse(s"Toad successfully $actionDone"))

  val toadBornResponse: StandardRoute = toadSuccessfullyAction("born")

  val toadAddedResp: StandardRoute = toadSuccessfullyAction("added")

  val toadKilledResp: StandardRoute = toadSuccessfullyAction("killed")

  val toadFedResp: StandardRoute = toadSuccessfullyAction("fed")

  val toadIsNotGrownResp: StandardRoute =
    complete(badRequestResponse("Toad is not grown"))

  val toadsPairedResp: StandardRoute =
    complete(standardOkResponse(s"Toads were successfully paired"))

  val deadBodiesRemoved: StandardRoute =
    complete(standardOkResponse(s"Dead bodies were successfully removed"))

  val toadsNotPairedResp: StandardRoute =
    complete(standardOkResponse(s"Toads are ok, but were not paired"))

  def allToadsResponse(toads: String): StandardRoute =
    complete(okResponseAsIs(toads))

  def totalToadsResponse(total: String): StandardRoute =
    complete(okResponseAsIs(total))

  def toadNotFoundResponse(id: String): StandardRoute =
    complete(notFoundResponse(id))

}
