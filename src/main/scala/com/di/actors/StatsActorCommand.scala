package com.di.actors

import akka.actor.typed.{ActorRef, ActorSystem}
import com.di.db.MongoFarmsConnector

import scala.concurrent.duration.Duration

sealed trait StatsActorCommand

//final case class StartNewCycle(cycleTime: Duration,
//                            stopAt: BigInt,
//                            system: ActorSystem[_],
//                            replyTo: ActorRef[CycleActorActionPerformed.type]) extends StatsActorCommand

final case class RecordNewToad(farmName: String,
                               system: ActorSystem[_],
                               replyTo: ActorRef[StatsActorActionPerformed.type]) extends StatsActorCommand

final case class RecordNewDeath(farmName: String,
                                system: ActorSystem[_],
                                replyTo: ActorRef[StatsActorActionPerformed.type]) extends StatsActorCommand

final case class SaveState(db: MongoFarmsConnector,
                           system: ActorSystem[_],
                           replyTo: ActorRef[StatsActorActionPerformed.type]) extends StatsActorCommand

case object StatsActorActionPerformed