package com.di.utils.responses

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

trait FarmBasedResponses extends BaseHttp {

  def farmAddedResponse(farm: String): StandardRoute =
    complete(standardOkResponse(s"Farm $farm added"))

  def farmDeletedResponse(farm: String): StandardRoute =
    complete(standardOkResponse(s"Farm $farm deleted"))

  def farmUpdated(oldName: String, newName: String): StandardRoute =
    complete(standardOkResponse(s"Farm $oldName is now $newName"))

  def availableFarmsResponse(farms: String): StandardRoute =
    complete(okResponseAsIs(farms))

  def noSuchFarm(farm: String): StandardRoute =
    complete(notFoundResponse(s"Farm $farm not found"))

  def farmAvailableResponse(farmName: String): StandardRoute =
    complete(standardOkResponse(s"Farm $farmName is now available"))

  def anotherFarmIsInUse(farmName: String): StandardRoute =
    complete(expectationFailed(s"Farm $farmName is in use now"))

  def noFarmIsInUseResp(): StandardRoute =
    complete(expectationFailed(s"No farm is in use now"))

  def farmIsNowNotInUseResp(farmName: String): StandardRoute =
    complete(standardOkResponse(s"Farm $farmName is now not in use"))

  def farmAlreadyExistsResponse(farmName: String): StandardRoute =
    complete(standardOkResponse(s"Farm with name $farmName already exists"))

  def farmAlreadyUsed(farmName: String): StandardRoute =
    complete(standardOkResponse(s"Farm with name $farmName already in use"))

}
