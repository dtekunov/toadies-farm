package com.di

import com.di.domain.{GrownToad, Tadpole, Toad}
import com.di.names._

import java.util.UUID
import scala.util.{Random, Try}

package object random {
  private val random = new Random

  def randomTadpole(farmName: String): Try[Tadpole] =
    Try(Tadpole(
      UUID.randomUUID().toString,
      farmName,
      getRandomElement(NAMES),
      getRandomElement(BREEDS),
      random.nextBoolean(),
      Random.between(0, 100),
      random.nextBoolean(),
      chooseElementByItsProbability(RARITY).get,
      getRandomElement(COLORS),
      None, // TODO
      Vector.empty[String], //TODO
      Some(Random.between(0, 100)), //TODO
    ))


  def getRandomGrownToad(farmName: String): Try[GrownToad] =
    Try(GrownToad(
      UUID.randomUUID().toString,
      farmName,
      getRandomElement(NAMES),
      getRandomElement(BREEDS),
      random.nextBoolean(),
      Random.between(0, 100),
      random.nextBoolean(),
      chooseElementByItsProbability(RARITY).get,
      getRandomElement(COLORS),
      None, // TODO
      Vector.empty[String], //TODO
      Some(Random.between(0, 100)), //TODO
      if (Random.between(0, 100) < 10) false else true
    ))

  def getRandomElement[T](collection: Seq[T]): T =
    collection(random.nextInt(collection.length))

  def chooseElementByItsProbability[T](samples: Seq[(T, Double)]): Option[T] = {
    val (strings, probs) = samples.unzip
    val cumprobs = probs.scanLeft(0.0){ _ + _ }.init

    def p2s(p: Double): T = strings(cumprobs.lastIndexWhere(_ <= p))
    Seq.fill(1)(math.random).map(p2s).headOption
  }


  def getRandomToad(farmName: String): Try[Toad] = {
    val toadTypes = Seq("tadpole", "grownToad")
    val randomizedToadType = getRandomElement(toadTypes)

    val toad =
      randomizedToadType match {
        case "tadpole" =>
          randomTadpole(farmName)
        case "grownToad" =>
         getRandomGrownToad(farmName)
      }
    toad
  }
}
