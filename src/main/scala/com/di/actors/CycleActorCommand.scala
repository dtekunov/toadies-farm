//package com.di.actors
//
//import akka.actor.typed.{ActorRef, ActorSystem}
//
//import scala.concurrent.duration.Duration
//
//sealed trait CycleActorCommand
//
//final case class StartNewCycle(cycleTime: Duration,
//                            stopAt: BigInt,
//                            system: ActorSystem[_],
//                            replyTo: ActorRef[CycleActorActionPerformed.type]) extends CycleActorCommand
//
//case object CycleActorActionPerformed