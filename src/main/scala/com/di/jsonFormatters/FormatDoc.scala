package com.di.jsonFormatters

import com.di.domain.{DeadToad, Farm, GrownToad, Owner, Tadpole}
import org.mongodb.scala.Document

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object FormatDoc {
  def toFarm(doc: Document): Farm =
    Farm(
      doc(Farm.idDbName).asString().getValue,
      doc(Farm.nameDbName).asString().getValue,
      doc(Farm.modeDbName).asString().getValue,
      doc(Farm.isCannibalDbName).asBoolean().getValue
    )

  def toGrownToad(doc: Document): GrownToad =
    GrownToad(
      doc(GrownToad.idDb).asString().getValue,
      doc(GrownToad.farmNameDb).asString().getValue,
      doc(GrownToad.nameDb).asString().getValue,
      doc(GrownToad.breedDb).asString().getValue,
      doc(GrownToad.isMaleDb).asBoolean().getValue,
      doc(GrownToad.ageDb).asInt32().getValue,
      doc(GrownToad.isCannibalDb).asBoolean().getValue,
      doc(GrownToad.rarityDb).asString().getValue,
      doc(GrownToad.colorDb).asString().getValue,
      if (doc(GrownToad.diseaseStatusDb).isNull) None else Some(doc(GrownToad.diseaseStatusDb).asString().getValue),
      doc(GrownToad.mutationsDb).asArray().getValues.toVector.map(elem => elem.asString().getValue),
      if (doc(GrownToad.pollutionLevelDb).isNull) None else Some(doc(GrownToad.pollutionLevelDb).asInt32().getValue),
      doc(GrownToad.fertilityDb).asBoolean().getValue
    )

  def toTadpole(doc: Document): Tadpole =
    Tadpole(
      doc(GrownToad.idDb).asString().getValue,
      doc(GrownToad.farmNameDb).asString().getValue,
      doc(GrownToad.nameDb).asString().getValue,
      doc(GrownToad.breedDb).asString().getValue,
      doc(GrownToad.isMaleDb).asBoolean().getValue,
      doc(GrownToad.ageDb).asInt32().getValue,
      doc(GrownToad.isCannibalDb).asBoolean().getValue,
      doc(GrownToad.rarityDb).asString().getValue,
      doc(GrownToad.colorDb).asString().getValue,
      if (doc(GrownToad.diseaseStatusDb).isNull) None else Some(doc(GrownToad.diseaseStatusDb).asString().getValue),
      doc(GrownToad.mutationsDb).asArray().getValues.toVector.map(elem => elem.asString().getValue),
      if (doc(GrownToad.pollutionLevelDb).isNull) None else Some(doc(GrownToad.pollutionLevelDb).asInt32().getValue),
    )

  def toDeadToad(doc: Document): DeadToad =
    DeadToad(
      doc(DeadToad.idDb).asString().getValue,
      doc(DeadToad.farmNameDb).asString().getValue,
      doc(DeadToad.nameDb).asString().getValue,
      doc(DeadToad.breedDb).asString().getValue,
      doc(DeadToad.isMaleDb).asBoolean().getValue,
      doc(DeadToad.isCannibalDb).asBoolean().getValue,
      doc(DeadToad.rarityDb).asString().getValue,
      doc(DeadToad.colorDb).asString().getValue
    )

  implicit def toOwner(doc: Document): Owner = {
    Owner(
      doc(Owner.idDb).asString().getValue,
      doc(Owner.farmNameDb).asString().getValue,
      doc(Owner.balanceDb).asInt64().getValue,
      doc(Owner.isCreativeDb).asBoolean().getValue,
      doc(Owner.transactionsMadeDb).asInt64().getValue,
      BigInt(doc(Owner.numberOfCyclesDb).asString().getValue)
    )
  }
}
