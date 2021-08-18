package com.di.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.di.db.MongoFarmsConnector
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.di.domain.{Farm, Owner, Stats, Toad}

import scala.collection.immutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object StatsActor {

  def apply(farmStats: Vector[Stats]): Behavior[StatsActorCommand] =
    registry(farmStats)


  private def registry(farmStats: Vector[Stats]): Behavior[StatsActorCommand] = {
    Behaviors.receiveMessage {
      case RecordNewToad(farmName, _, replyTo) =>
        val newStats = farmStats.map {
          case stat if stat.farmName == farmName =>
            Stats(farmName, totalToads = stat.totalToads + 1, toadsDied = stat.toadsDied)
          case stat => stat
        }
        replyTo ! StatsActorActionPerformed
        registry(newStats)

      case RecordNewDeath(farmName, _, replyTo) =>
        val newStats = farmStats.map {
          case stat if stat.farmName == farmName =>
            Stats(farmName, totalToads = stat.totalToads, toadsDied = stat.toadsDied + 1)
          case stat => stat
        }
        replyTo ! StatsActorActionPerformed
        registry(newStats)

      case SaveState(db, system, replyTo) =>
        val timeout = system.settings.config.getInt("main.routes.ask-timeout").seconds
        farmStats.foreach { farmStat =>
          Await.result(db.updateStats(farmStat), timeout) match {
            case Some(_) =>
              system.log.debug(s"Farm ${farmStat.farmName} stats updated")
            case None =>
              system.log.error(s"Farm ${farmStat.farmName} stats cannot be updated")
          }
        }
        replyTo ! StatsActorActionPerformed
        registry(farmStats)
    }
  }

}
