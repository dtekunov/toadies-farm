package com.di.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.di.actors.logic.ToadsActorLogic
import com.di.domain.{DeadToad, Farm, Owner, PregnantToad, Toad}
import com.di.names.DEATHS
import com.di.random.{bornRandomTadpoleRND, getRandomElement, getRandomToad}

import scala.util.{Failure, Success}

object ToadsActor extends ToadsActorLogic {

  def apply(farm: Farm,
            system: ActorSystem[_],
            toads: Vector[Toad],
            owner: Owner,
            numberOfCycles: BigInt): Behavior[ToadsActorCommand] = {
    registry(farm, toads, owner, numberOfCycles)
  }

  // TODO: mb toads to arraybuffer ???
  private def registry(farm: Farm, toads: Vector[Toad], owner: Owner, numberOfCycles: BigInt): Behavior[ToadsActorCommand] = {
    Behaviors.receiveMessage {
      case AddGrownToad(rawGrownToad, system, replyTo) =>
        val toad = rawGrownToad.toGrownToad(farm.name)
        system.log.info(s"Toad ${toad.name} added")
        replyTo ! DefaultToadsActorResponse(s"Toad ${toad.name} added", None)
        registry(farm, toads :+ toad, owner, numberOfCycles)

      case KillToadById(id, system, replyTo) =>
        toads.find(toad => toad.id == id) match {
          case Some(toad) =>
            val updatedToads = toads.map(t => if (t.id == id) t.killed else t)
            system.log.info(s"Toad ${toad.name} ${getRandomElement(DEATHS)}")
            replyTo ! DefaultToadsActorResponse("Successfully killed", None)
            registry(farm, updatedToads, owner, numberOfCycles)
          case None =>
            replyTo ! DefaultToadsActorResponse("No such toad", None)
            system.log.info(s"Toad $id cannot be killed, cause it does not exist")
            registry(farm, toads, owner, numberOfCycles)
        }

//      case RemoveToadById(id, system, replyTo) =>
//        toads.find(toad => toad.id == id) match {
//          case Some(toad) =>
//            system.log.info(s"Toad ${toad.name} ${getRandomElement(DEATHS)}")
//            replyTo ! DefaultToadsActorResponse("Successfully deleted", None)
//            registry(farm, toads.filter(t => t.id != id), owner, numberOfCycles)
//          case None =>
//            replyTo ! DefaultToadsActorResponse("Successfully deleted", None)
//            system.log.info(s"Toad $id cannot be killed, cause it does not exist")
//            registry(farm, toads, owner, numberOfCycles)
//        }

      case GetToadById(id, _, replyTo) =>
        val maybeToad = toads.find(toad => toad.id == id)
        replyTo ! SingleToadResponse(maybeToad)
        registry(farm, toads, owner, numberOfCycles)

      case UpdateToad(oldToad, newToad, _, replyTo) =>
        val filteredToads = toads.filter(toad => toad.id != oldToad.id) //todo: test
        replyTo ! DefaultToadsActorResponse("Filtering performed", None)
        registry(farm, filteredToads :+ newToad, owner, numberOfCycles)

      /**
       *
       */
      case BornRandom(system, replyTo) =>
        bornRandomTadpoleRND(farm.name) match {
          case Success(tadpole) =>
            val responseText = s"Tadpole ${tadpole.name} was born"
            system.log.info(responseText)
            replyTo ! DefaultToadsActorResponse(responseText, Some(responseText))
            registry(farm, toads :+ tadpole, owner, numberOfCycles)
          case Failure(ex) =>
            val cause = s"Random toad cannot be created due to ${ex.getLocalizedMessage}"
            system.log.error(cause)
            replyTo ! DefaultToadsActorResponse(cause, None)
            registry(farm, toads, owner, numberOfCycles)
        }

      /**
       * Get all available toads
       */
      case GetAllToads(_, replyTo) =>
        replyTo ! MaybeToadsResponse(Some(toads))
        registry(farm, toads, owner, numberOfCycles)

      /**
       * Removes all <DeadToad> entries
       */
      case RemoveDeadBodies(system, replyTo) =>
        val filteredToads = toads.filter(_.isAlive)
        replyTo ! DefaultToadsActorResponse("Dead bodies removed", None)
        system.log.info("Dead bodies were removed")
        registry(farm, filteredToads, owner, numberOfCycles)

      /**
       *
       */
      case UpdateToadsState(stopAt, ctx) =>
        if (numberOfCycles < stopAt) {
          val hungryToadsWithUpdatedAge = updateToadsAgeAndHunger(toads)
          val hungryToads = hungerCycle(hungryToadsWithUpdatedAge, farm)

          hungryToads.hungryNonCannibals.foreach(toad => ctx.log.info(s" Toad ${toad.name} is hungry"))

          val feastedToads =
            if (farm.isCannibal && hungryToads.hungryCannibals.isDefined) {
              feastToads(hungryToadsWithUpdatedAge, hungryToads.hungryCannibals.get)
            } else hungryToadsWithUpdatedAge

          val numOfDeadBodies = countDeadBodies(feastedToads)
          val polLevelToAdd = countPollutionLevelBasedOnDeadBodies(numOfDeadBodies)
          val toadsWithUpdatedPollutionLevel = updateToadsPollutionLevel(feastedToads, polLevelToAdd)

          val updatedOwner = owner.copy(numberOfCycles = numberOfCycles + 1)
          ctx.log.info(s"Cycle #$numberOfCycles")
          registry(farm, feastedToads, updatedOwner, numberOfCycles + 1)
        } else {
          Behaviors.stopped
          registry(farm, toads, owner, numberOfCycles)
        }
    }
  }

}
