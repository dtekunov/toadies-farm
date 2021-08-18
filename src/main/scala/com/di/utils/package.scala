package com.di

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.{HttpCharsets, HttpRequest}
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.{Keep, Sink}
import com.di.domain.{Creative, Hardcore, Mode, Survival}
import com.di.names.FOOD_LIST

import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

package object utils {

  def validateMode(rawMode: String): Option[Mode] = rawMode.toLowerCase match {
    case "creative" => Some(Creative)
    case "survival" => Some(Survival)
    case "hardcore" => Some(Hardcore)
    case _ => None
  }

  def validateId(rawId: String): Option[UUID] = Try(UUID.fromString(rawId)) match {
    case Success(uuid) => Some(uuid)
    case Failure(_) => None
  }

  def getFoodValue(rawFood: String): Option[Int] = FOOD_LIST.get(rawFood)

  def extractRequestEntityAsString(request: HttpRequest)(implicit system: ActorSystem[_]): Future[String] = {
    val collectBodySink = Sink.fold[String, String]("")(_ + _)

    val source: Source[String, AnyRef] = request.entity.getDataBytes().map {
      chunk => chunk.decodeString(HttpCharsets.`UTF-8`.value)
    }.asScala

    val runnable = source.toMat(collectBodySink)(Keep.right)

    runnable.run()
  }
}
