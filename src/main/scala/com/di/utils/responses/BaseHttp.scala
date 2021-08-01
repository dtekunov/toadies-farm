package com.di.utils.responses

import akka.http.scaladsl.model._
import com.di.jsonFormatters.JsonWriter

trait BaseHttp {

  private final val baseHeaders = Vector.empty[HttpHeader]

  protected def standardOkResponse[T](output: T): HttpResponse =
    HttpResponse(
      status = StatusCodes.OK,
      headers = baseHeaders,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        string = JsonWriter.format(output)
      ))

  protected def okResponseAsIs(output: String): HttpResponse =
    HttpResponse(
      status = StatusCodes.OK,
      headers = baseHeaders,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        string = output
      ))

  protected def alreadyExistsResponse(output: String): HttpResponse =
    HttpResponse(
      status = StatusCodes.AlreadyReported,
      headers = baseHeaders,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        string = JsonWriter.format(output)
      ))

  protected def notFoundResponse(output: String): HttpResponse =
    HttpResponse(
      status = StatusCodes.AlreadyReported,
      headers = baseHeaders,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        string = JsonWriter.format(output)
      ))

  protected def internalServerErrorResponse(msg: String): HttpResponse =
    HttpResponse(
      status = StatusCodes.InternalServerError,
      headers = baseHeaders,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        string = JsonWriter.format(msg)
      ))

  protected def badRequestResponse(msg: String): HttpResponse =
    HttpResponse(
      status = StatusCodes.BadRequest,
      headers = baseHeaders,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        string = JsonWriter.format(msg)
      ))

  protected def expectationFailed(msg: String): HttpResponse =
    HttpResponse(
      status = StatusCodes.ExpectationFailed,
      headers = baseHeaders,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        string = JsonWriter.format(msg)
      ))

  protected def methodNotAllowedResponse: HttpResponse =
    HttpResponse(
      status = StatusCodes.MethodNotAllowed,
      headers = baseHeaders,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        string = "Not allowed"
      ))
}
