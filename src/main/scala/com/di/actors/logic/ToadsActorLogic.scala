package com.di.actors.logic

import akka.actor.typed.ActorSystem
import com.di.domain.{DeadToad, Farm, GrownToad, Mode, PregnantToad, Toad}

//todo: mb merge with toadsArithmetics
trait ToadsActorLogic {

  def updateToadsAgeAndHunger(toads: Vector[Toad]): Vector[Toad] =
    toads.foldLeft(Vector.empty[Toad]) { (acc, elem) =>
      elem match {
        case pregnantToad: PregnantToad =>
          val pregnantToadWithUpdatedAge = pregnantToad.updateAge
          val pregnantToadWithUpdatedAgeAndHunger = pregnantToadWithUpdatedAge.increaseHunger
          val (maybeTadpoles, mother) = pregnantToadWithUpdatedAgeAndHunger match {
            case pregnant: PregnantToad =>
              val pregnancyResult = pregnant.pregnancyResultRND
              if (pregnancyResult.tadpoles.nonEmpty)
                (pregnancyResult.tadpoles.get, pregnancyResult.mother)
              else
                (Vector(), pregnancyResult.mother)

            case _ => (Vector(), pregnantToadWithUpdatedAge)
          }
          (acc ++ maybeTadpoles) :+ mother

        case deadToad: DeadToad => acc :+ deadToad
        case otherToad => acc :+ otherToad.updateAge.increaseHunger
      }
    }

  def countDeadBodies(toads: Vector[Toad]): Long =
    toads.foldLeft(0L) { (acc, elem) =>
      elem match {
        case _: DeadToad => acc + 1
        case _ => acc
      }
    }

  def countPollutionLevelBasedOnDeadBodies(deadBodiesNum: Long): Int = {
    val maxBodies = if (deadBodiesNum > 500) 500 else deadBodiesNum

    (maxBodies * 0.1).toInt
  }

  def hungerCycle(toads: Vector[Toad], farm: Farm): HungryToads = {
    if (farm.isCannibal) cannibalHungerCycle(toads)
    else peacefulHungerCycle(toads)
  }

  private def peacefulHungerCycle(toads: Vector[Toad]): HungryToads =
    toads.foldLeft(HungryToads(
      hungryNonCannibals = Vector.empty[Toad],
      hungryCannibals = None)) { (acc, toad) =>
      if (toad.hungerLevel.isNonCannibalHungry)
        HungryToads(acc.hungryNonCannibals :+ toad, acc.hungryCannibals)
      else acc
    }

  private def cannibalHungerCycle(toads: Vector[Toad]): HungryToads =
    toads.foldLeft(HungryToads(
      hungryNonCannibals = Vector.empty[Toad],
      hungryCannibals = Some(Vector.empty[Toad]))) { (acc, toad) =>
      if (toad.isCannibal && toad.hungerLevel.isCannibalHungry)
        HungryToads(acc.hungryNonCannibals, Some(acc.hungryCannibals.getOrElse(Vector.empty[Toad]) :+ toad))
      else if (!toad.isCannibal && toad.hungerLevel.isNonCannibalHungry)
        HungryToads(acc.hungryNonCannibals :+ toad, acc.hungryCannibals)
      else acc
    }

  def updateToadsPollutionLevel(toads: Vector[Toad], pollutionLevelToAdd: Int): Vector[Toad] =
    toads.map(_.updatePollutionLevel(pollutionLevelToAdd))

  def feastToads(toads: Vector[Toad], cannibals: Vector[Toad]): Vector[Toad] = {

    val noCannibals = toads.diff(cannibals)
    if (noCannibals.length >= cannibals.length) {
      val noCannibalsBuffer = collection.mutable.ArrayBuffer(noCannibals: _*)

      val fedCannibals: Vector[Toad] = cannibals.map {
        case grownToad: GrownToad =>
//          val toadToEat: Toad = noCannibalsBuffer(0) //todo: random?
          noCannibalsBuffer.remove(0)
          grownToad.feedCannibal
        case pregnantToad: PregnantToad =>
//          val toadToEat: Toad = noCannibalsBuffer(0)
          noCannibalsBuffer.remove(0)
          pregnantToad.feedCannibal
        case other => other
      }
      fedCannibals ++ noCannibalsBuffer
    } else letCannibalsEatThemselves(cannibals) ++ noCannibals
  }

  private def letCannibalsEatThemselves(cannibals: Vector[Toad]): Vector[Toad] = {
    if (cannibals.length > 1) {
      cannibals.take(cannibals.length / 2).map {
        case grownToad: GrownToad =>
          grownToad.feedCannibal
        case pregnantToad: PregnantToad =>
          pregnantToad.feedCannibal
        case other => other
      }
    } else cannibals
  }
}

case class HungryToads(hungryNonCannibals: Vector[Toad],
                       hungryCannibals: Option[Vector[Toad]])