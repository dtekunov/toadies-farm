package com.di.routes

import akka.actor.typed.scaladsl.AskPattern.Askable
import com.di.actors._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern._
import com.di.jsonFormatters.rawdataFormats.RawGrownToad

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

trait CreativeActionsActorCommands {

  def killToadById(toadsActor: ActorRef[ToadsActorCommand], id: String)
                  (implicit system: ActorSystem[_],
                   ec: ExecutionContext,
                   timeout: Timeout): Future[ToadsActorResponse] =
    toadsActor.ask(KillToadById(id, system, _))

  def getToadById(toadsActor: ActorRef[ToadsActorCommand], id: String)
                 (implicit system: ActorSystem[_],
                  ec: ExecutionContext,
                  timeout: Timeout): Future[ToadsActorResponse] =
    toadsActor.ask(GetToadById(id, system, _))

  def getOwnerInfo(farmActor: ActorRef[FarmActorCommand])
                  (implicit system: ActorSystem[_],
                   ec: ExecutionContext,
                   timeout: Timeout): Future[FarmActorActionPerformed] =
    farmActor.ask(GetOwnerInfo(system, _))

  def startCycle(farmActor: ActorRef[FarmActorCommand])
                (implicit system: ActorSystem[_],
                 ec: ExecutionContext,
                 timeout: Timeout): Future[FarmActorActionPerformed] =
    farmActor.ask(StartCycle(1.second, 1000, ec, system, _)) //todo: params up

  def stopCycle(farmActor: ActorRef[FarmActorCommand])
               (implicit system: ActorSystem[_],
                ec: ExecutionContext,
                timeout: Timeout): Future[FarmActorActionPerformed] =
    farmActor.ask(StopCycle(system, _))

  def addGrownToad(toadsActor: ActorRef[ToadsActorCommand], rawGrownToad: RawGrownToad)
                  (implicit system: ActorSystem[_],
                   ec: ExecutionContext,
                   timeout: Timeout): Future[ToadsActorResponse] =
    toadsActor.ask(AddGrownToad(rawGrownToad, system, _))

  def bornRandom(toadsActor: ActorRef[ToadsActorCommand])
                (implicit system: ActorSystem[_],
                 ec: ExecutionContext,
                 timeout: Timeout): Future[ToadsActorResponse] =
    toadsActor.ask(BornRandom(system, _))

  def getAllToads(toadsActor: ActorRef[ToadsActorCommand])
                 (implicit system: ActorSystem[_],
                  ec: ExecutionContext,
                  timeout: Timeout): Future[ToadsActorResponse] =
    toadsActor.ask(GetAllToads(system, _))
}
