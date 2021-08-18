package com.di.jsonFormatters

import com.di.domain.{DeadToad, Farm, GrownToad, Owner, PregnantToad, Tadpole, Toad}
import com.di.toadsArithmetics.{ToadsCount, ToadsGrouped}
import org.json4s.jackson.JsonMethods.{compact, pretty, render}
import org.json4s.jackson.Serialization
import org.json4s._
import org.json4s.jackson.Serialization.{read, write}



object JsonWriter {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  def format[T](result: T): String = result match {
    case result: String =>
      compact(render(JObject(
        "message" -> JString(result)
      )))

    case toadsGrouped: ToadsGrouped =>
      s"""
        "tadpoles": ${formatTadpolesSeq(toadsGrouped.tadpoles)},
        "grown_toads": ${formatGrownToadsSeq(toadsGrouped.grownToads)},
        "dead_toads": ${formatDeadToadsSeq(toadsGrouped.deadToads)},
        "pregnant_toads": ${formatPregnantToadsSeq(toadsGrouped.pregnantToads)}
      """

    case result: ToadsCount => write(result)
    case result: Owner => write(result)
    case result: GrownToad => write(result)
    case result: Tadpole => write(result)
    case result: DeadToad => write(result)
  }

  def formatFarmsSeq(toTransform: Seq[Farm]): String = {
    implicit val formats = Serialization.formats(FullTypeHints(List(classOf[Farm])))

    pretty(render(Extraction.decompose(toTransform)).removeField {
      case ("jsonClass", _) => true
      case _ => false
    })
  }

  def formatOwnersSeq(toTransform: Seq[Owner]): String = {
    implicit val formats = Serialization.formats(FullTypeHints(List(classOf[Owner])))

    pretty(render(Extraction.decompose(toTransform)).removeField {
      case ("jsonClass", _) => true
      case _ => false
    })
  }

  def formatGrownToadsSeq(toTransform: Seq[GrownToad]): String = {
    implicit val formats = Serialization.formats(FullTypeHints(List(classOf[GrownToad])))

    pretty(render(Extraction.decompose(toTransform)).removeField {
      case ("jsonClass", _) => true
      case _ => false
    })
  }

  def formatTadpolesSeq(toTransform: Seq[Tadpole]): String = {
    implicit val formats = Serialization.formats(FullTypeHints(List(classOf[Tadpole])))

    pretty(render(Extraction.decompose(toTransform)).removeField {
      case ("jsonClass", _) => true
      case _ => false
    })
  }

  def formatDeadToadsSeq(toTransform: Seq[DeadToad]): String = {
    implicit val formats = Serialization.formats(FullTypeHints(List(classOf[DeadToad])))

    pretty(render(Extraction.decompose(toTransform)).removeField {
      case ("jsonClass", _) => true
      case _ => false
    })
  }

  def formatPregnantToadsSeq(toTransform: Seq[PregnantToad]): String = {
    implicit val formats = Serialization.formats(FullTypeHints(List(classOf[PregnantToad])))

    pretty(render(Extraction.decompose(toTransform)).removeField {
      case ("jsonClass", _) => true
      case _ => false
    })
  }
}

