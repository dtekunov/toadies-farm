package com.di.jsonFormatters

import com.di.domain.{DeadToad, Farm, GrownToad, Owner, Tadpole}
import com.di.types.{Hunger, PollutionLevel, Rarity}
import org.mongodb.scala.Document

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object FormatDoc {
  def toFarm(doc: Document): Farm =
    Farm(
      doc(Farm.idDbName).asString().getValue,
      doc(Farm.nameDbName).asString().getValue,
      doc(Farm.modeDbName).asString().getValue,
      doc(Farm.isCannibalDbName).asBoolean().getValue,
      doc(Farm.mutationsModifierDbName).asInt64().getValue
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
      Rarity(doc(GrownToad.rarityDb).asString().getValue),
      doc(GrownToad.colorDb).asString().getValue,
      if (doc(GrownToad.diseaseStatusDb).isNull) None else Some(doc(GrownToad.diseaseStatusDb).asString().getValue),
      doc(GrownToad.mutationsDb).asArray().getValues.toVector.map(elem => elem.asString().getValue),
      if (doc(GrownToad.pollutionLevelDb).isNull) PollutionLevel(None)
      else PollutionLevel(Some(doc(GrownToad.pollutionLevelDb).asInt32().getValue)),
      doc(GrownToad.fertilityDb).asBoolean().getValue,
      Hunger(doc(GrownToad.hungerLevelDb).asInt32().getValue)
    )

  def toTadpole(doc: Document): Tadpole =
    Tadpole(
      doc(Tadpole.idDb).asString().getValue,
      doc(Tadpole.farmNameDb).asString().getValue,
      doc(Tadpole.nameDb).asString().getValue,
      doc(Tadpole.breedDb).asString().getValue,
      doc(Tadpole.isMaleDb).asBoolean().getValue,
      doc(Tadpole.ageDb).asInt32().getValue,
      doc(Tadpole.isCannibalDb).asBoolean().getValue,
      Rarity(doc(Tadpole.rarityDb).asString().getValue),
      doc(Tadpole.colorDb).asString().getValue,
      if (doc(Tadpole.diseaseStatusDb).isNull) None else Some(doc(Tadpole.diseaseStatusDb).asString().getValue),
      doc(Tadpole.mutationsDb).asArray().getValues.toVector.map(elem => elem.asString().getValue),
      if (doc(Tadpole.pollutionLevelDb).isNull) PollutionLevel(None)
      else PollutionLevel(Some(doc(Tadpole.pollutionLevelDb).asInt32().getValue)),
      Hunger(doc(Tadpole.hungerLevelDb).asInt32().getValue)
    )

  def toDeadToad(doc: Document): DeadToad =
    DeadToad(
      doc(DeadToad.idDb).asString().getValue,
      doc(DeadToad.farmNameDb).asString().getValue,
      doc(DeadToad.nameDb).asString().getValue,
      doc(DeadToad.breedDb).asString().getValue,
      doc(DeadToad.isMaleDb).asBoolean().getValue,
      doc(DeadToad.isCannibalDb).asBoolean().getValue,
      Rarity(doc(DeadToad.rarityDb).asString().getValue),
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
