//package com.di.actors
//
//import akka.actor.typed.scaladsl.Behaviors
//import akka.actor.typed.{ActorSystem, Behavior}
//import com.di.domain.{Farm, Owner, Toad}
//
//object CycleActor {
//
//  def apply(numberOfCycles: BigInt): Behavior[CycleActorCommand] = {
//    registry(numberOfCycles)
//  }
//
//  private def registry(numberOfCycles: BigInt): Behavior[CycleActorCommand] = {
//    Behaviors.receiveMessage {
//      case StartNewCycle(cycleTime, stopAt, system, replyTo) =>
//
//    }
//  }
//
//}
