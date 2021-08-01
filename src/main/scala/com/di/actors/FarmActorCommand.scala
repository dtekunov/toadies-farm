package com.di.actors

import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.{ActorRef, ActorSystem}
import com.di.db.MongoFarmsConnector
import com.di.domain.{Farm, GrownToad, Owner, Tadpole, Toad}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

sealed trait FarmActorCommand

final case class StartNewFarm(farm: Farm,
                              owner: Owner,
                              system: ActorSystem[_],
                              replyTo: ActorRef[FarmActorActionPerformed]) extends FarmActorCommand

final case class ContinueFarm(farm: Farm,
                              owner: Owner,
                              toads: Vector[Toad],
                              system: ActorSystem[_],
                              replyTo: ActorRef[FarmActorActionPerformed]) extends FarmActorCommand

final case class StopUsingFarm(system: ActorSystem[_],
                               replyTo: ActorRef[FarmActorActionPerformed]) extends FarmActorCommand

final case class GetOwnerInfo(system: ActorSystem[_],
                              replyTo: ActorRef[FarmActorActionPerformed]) extends FarmActorCommand

final case class StartCycle(cycleTime: FiniteDuration,
                            stopAt: BigInt,
                            ec: ExecutionContext,
                            system: ActorSystem[_],
                            replyTo: ActorRef[FarmActorActionPerformed]) extends FarmActorCommand

final case class StopCycle(system: ActorSystem[_],
                           replyTo: ActorRef[FarmActorActionPerformed]) extends FarmActorCommand

final case class GetToadsActorRef(system: ActorSystem[_],
                                  replyTo: ActorRef[FarmActorActionPerformed]) extends FarmActorCommand

final case class FarmActorActionPerformed(description: String,
                                          owner: Option[Owner] = None,
                                          toadsActor: Option[ActorRef[ToadsActorCommand]] = None) //todo: different msgs
