package com.di.actors

import akka.actor.Cancellable
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props, SpawnProtocol}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.util.Timeout
import com.di.db.MongoFarmsConnector
import com.di.domain.{Farm, Owner, Toad}
import com.di.jsonFormatters.JsonWriter
import com.di.utils.validateMode

import java.util.UUID
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{ExecutionContext, Future}

object FarmActor {

  def apply(farm: Option[Farm], owner: Option[Owner]): Behavior[FarmActorCommand] =
    Behaviors.setup { ctx =>
    registry(farm, owner, None, ctx, None)
  }

  private def registry(farm: Option[Farm],
                       owner: Option[Owner],
                       maybeToadsActor: Option[ActorRef[ToadsActorCommand]],
                       ctx: ActorContext[FarmActorCommand],
                       maybeCancellable: Option[Cancellable]): Behavior[FarmActorCommand] = {
    Behaviors.receiveMessage {
      case StartNewFarm(newFarm, owner, system, replyTo) =>
        val newToadsActor = ctx.spawn(
          ToadsActor(newFarm, system, Vector.empty[Toad], owner, 0),
          s"ToadsActor${UUID.randomUUID().toString}"
        )
        replyTo ! FarmActorActionPerformed("Farm started")
        registry(Some(newFarm), Some(owner), Some(newToadsActor), ctx, maybeCancellable)

      case ContinueFarm(farm, owner, toads, system, replyTo) =>
        val newToadsActor = ctx.spawn(
          ToadsActor(farm, system, toads, owner, owner.numberOfCycles),
          s"ToadsActor${UUID.randomUUID().toString}"
        )
        replyTo ! FarmActorActionPerformed(s"Farm ${farm.name} is used now")
        registry(Some(farm), Some(owner), Some(newToadsActor), ctx, maybeCancellable)

      case StartCycle(cycleTime, stopAt, ec, system, replyTo) =>
        implicit val executionContext: ExecutionContext = ec

        val maybeCancellable = maybeToadsActor.map { toadsActor =>
          system.scheduler.scheduleWithFixedDelay(Duration.Zero, cycleTime)(() =>
            toadsActor ! UpdateToadsState(stopAt, system)
          )
        }
        replyTo ! FarmActorActionPerformed("Cycle started")
        registry(farm, owner, maybeToadsActor, ctx, maybeCancellable)

      case StopCycle(system, replyTo) =>
        maybeCancellable.foreach(cancellable => cancellable.cancel())
        replyTo ! FarmActorActionPerformed("Cycle stopped")
        system.log.info("Cycle stopped")
        registry(farm, owner, maybeToadsActor, ctx, None)

      case GetOwnerInfo(system, replyTo) =>
        owner.foreach(elem => system.log.info(JsonWriter.format(elem)))
        replyTo ! FarmActorActionPerformed("Owner info provided", owner)
        registry(farm, owner, maybeToadsActor, ctx, maybeCancellable)

      case GetToadsActorRef(system, replyTo) =>
        replyTo ! FarmActorActionPerformed("Toads actor ref reply", None, maybeToadsActor)
        registry(farm, owner, maybeToadsActor, ctx, maybeCancellable)

      case StopUsingFarm(system, replyTo) =>
        maybeToadsActor match {
          case Some(toadsActor) =>
            ctx.stop(toadsActor)
            system.log.info("Toads actor stopped")
            replyTo ! FarmActorActionPerformed("ok")
            Behaviors.stopped
            registry(None, None, None, ctx, None)
          case None =>
            replyTo ! FarmActorActionPerformed("doesNotExist")
            Behaviors.stopped
            registry(None, None, None, ctx, None)
        }

    }
  }

}

case class FarmNotCreatedException(ex: String) extends Exception(ex)