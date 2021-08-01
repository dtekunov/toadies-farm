package com.di.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.di.domain.{Farm, Owner, Toad}
import com.di.names.DEATHS
import com.di.random.{getRandomElement, getRandomToad, randomTadpole}

import scala.util.{Failure, Success}

object ToadsActor {

  def apply(farm: Farm,
            system: ActorSystem[_],
            toads: Vector[Toad],
            owner: Owner,
            numberOfCycles: BigInt): Behavior[ToadsActorCommand] = {
    registry(farm, toads, owner, numberOfCycles)
  }

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
            system.log.info(s"Toad ${toad.name} ${getRandomElement(DEATHS)}")
            replyTo ! DefaultToadsActorResponse("Successfully deleted", None)
            registry(farm, toads.filter(t => t.id != id), owner, numberOfCycles)
          case None =>
            replyTo ! DefaultToadsActorResponse("Successfully deleted", None)
            system.log.info(s"Toad $id cannot be killed, cause it does not exist")
            registry(farm, toads, owner, numberOfCycles)
        }

      case BornRandom(system, replyTo) =>
        randomTadpole(farm.name) match {
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

      case GetAllToads(_, replyTo) =>
        replyTo ! MaybeToadsResponse(Some(toads))
        registry(farm, toads, owner, numberOfCycles)


      case UpdateToadsState(stopAt, ctx) =>
        if (numberOfCycles < stopAt) {
          val updatedToads = toads.map { toad =>
            if (toad.isAlive) toad.updateAge
            else toad
          }
          val updatedOwner = owner.copy(numberOfCycles = numberOfCycles + 1)
          ctx.log.info(s"Cycle #$numberOfCycles")
          registry(farm, updatedToads, updatedOwner, numberOfCycles + 1)
        } else {
          Behaviors.stopped
          registry(farm, toads, owner, numberOfCycles)
        }
    }
  }

}
