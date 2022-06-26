package com.di.actors.logic

import akka.actor.typed.ActorSystem
import com.di.domain.{DeadToad, Farm, GrownToad, Mode, PregnantToad, Tadpole, Toad}

//todo: mb merge with toadsArithmetics
trait ToadsActorLogic {

  /**
   * Calculates age and hunger of toads
   *
   * Also calculates the result of pregnancy
   *
   * TODO: move pregnancy logic to another fucntion
   */
  def updateToadsAgeAndHunger(toads: Vector[Toad]): Vector[Toad] =
    toads.foldLeft(Vector.empty[Toad]) { (acc, elem) =>
      elem match {
        case pregnantToad: PregnantToad =>
          val pregnantToadWithUpdatedAge = pregnantToad.updateAge
          val pregnantToadWithUpdatedAgeAndHunger = pregnantToadWithUpdatedAge.increaseHunger
          val (maybeTadpoles, mother) = pregnantToadWithUpdatedAgeAndHunger match {
            case pregnant: PregnantToad =>
              val pregnancyResult = pregnant.pregnancyResultRND
                (pregnancyResult.tadpoles.getOrElse(Vector.empty[Tadpole]), pregnancyResult.mother)

            case _ => (Vector.empty[Tadpole], pregnantToadWithUpdatedAge)
          }
          (acc ++ maybeTadpoles) :+ mother

        case deadToad: DeadToad => acc :+ deadToad
        case otherToad => acc :+ otherToad.updateAge.increaseHunger
      }
    }

  /**
   * Calculates the number of dead bodies on a farm
   */
  def countDeadBodies(toads: Vector[Toad]): Long =
    toads.foldLeft(0L) { (acc, elem) =>
      elem match {
        case _: DeadToad => acc + 1
        case _ => acc
      }
    }

  /**
   * Returns pollution level based on the number of dead bodies on the farm.
   *
   * Max pollution level cannot be greater then 50 due to the limit of total dead bodies.
   */
  def countPollutionLevelBasedOnDeadBodies(deadBodiesNum: Long): Int = {
    val maxBodies = if (deadBodiesNum > 500) 500 else deadBodiesNum

    (maxBodies * 0.1).toInt
  }

  /**
   * Returns hungry toads based on type of farm (peaceful or not)
   */
  def calculateHungryToadsByFarmType(toads: Vector[Toad], farm: Farm): HungryToads = {
    if (farm.isCannibal) cannibalHungerCycle(toads)
    else peacefulHungerCycle(toads)
  }

  /**
   * Returns hungry toads via peaceful mode
   */
  private def peacefulHungerCycle(toads: Vector[Toad]): HungryToads =
    toads.foldLeft(HungryToads(
      hungryNonCannibals = Vector.empty[Toad],
      hungryCannibals = None)) { (acc, toad) =>
      if (toad.hungerLevel.isNonCannibalHungry)
        HungryToads(acc.hungryNonCannibals :+ toad, acc.hungryCannibals)
      else acc
    }

  /**
   * Returns hungry toads via cannibal mode
   */
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

  /**
   * Updates pollution level for each toad
   */
  def updateToadsPollutionLevel(toads: Vector[Toad], pollutionLevelToAdd: Int): Vector[Toad] =
    toads.map(_.updatePollutionLevel(pollutionLevelToAdd))

  /**
   * Returns toads after being eaten by cannibals
   */
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

  /**
   * Returns toads-cannibals after being eaten by other cannibals
   *
   * Should only be applied if no non-cannibal toads left
   */
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