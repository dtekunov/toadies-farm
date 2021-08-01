package com.di.jsonFormatters

import com.di.jsonFormatters.rawdataFormats.{RawGrownToad, RawTadpole}
import org.json4s.jackson.JsonMethods.{compact, parse, pretty, render}
import org.json4s.jackson.Serialization
import org.json4s._
import org.json4s.jackson.Serialization.{read, write}

import scala.util.Try



object JsonReader {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  def rawStringToRawGrownToad(raw: String): Try[RawGrownToad] =
    Try(parse(raw).extract[RawGrownToad])

  def rawStringToRawTadpole(raw: String): Try[RawTadpole] =
    Try(parse(raw).extract[RawTadpole])
}
