package com.di

import com.di.domain.{GrownToad, PregnantToad, Tadpole, Toad}
import com.di.names._
import com.di.types._

import java.util.UUID
import scala.util.{Random, Try}

/**
 * Holds all random-based operations
 */
package object random {
  private val random = new Random

  def bornRandomTadpoleRND(farmName: String): Try[Tadpole] = {
    val isMale = random.nextBoolean()
    val randomName =
      if (isMale) getRandomElement(MALE_NAMES)
      else getRandomElement(FEMALE_NAMES)

    Try(Tadpole(
      id = UUID.randomUUID().toString,
      farmName = farmName,
      name = randomName,
      breed = getRandomElement(BREEDS),
      isMale = isMale,
      age = 0,
      isCannibal = random.nextBoolean(),
      rarity = Rarity(chooseElementByItsProbability(RARITY).get),
      color = getRandomElement(COLORS),
      diseaseStatus = None, // TODO
      mutations = Vector.empty[String], //TODO
      pollutionLevel = PollutionLevel(Some(Random.between(0, 100))), //TODO
      Hunger(0)
    ))
  }


  def getRandomGrownToad(farmName: String): Try[GrownToad] = {
    val isMale = random.nextBoolean()
    val randomName =
      if (isMale) getRandomElement(MALE_NAMES)
      else getRandomElement(FEMALE_NAMES)

    Try(GrownToad(
      id = UUID.randomUUID().toString,
      farmName = farmName,
      name = randomName,
      breed = getRandomElement(BREEDS),
      isMale = isMale,
      age = Random.between(0, 100),
      isCannibal = random.nextBoolean(),
      Rarity(chooseElementByItsProbability(RARITY).get),
      getRandomElement(COLORS),
      None, // TODO
      Vector.empty[String], //TODO
      PollutionLevel(Some(Random.between(0, 100))), //TODO
      if (Random.between(0, 100) < 10) false else true,
      Hunger(0)
    ))
  }

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
          bornRandomTadpoleRND(farmName)
        case "grownToad" =>
         getRandomGrownToad(farmName)
      }
    toad
  }

  /**
   * Calculates the number of tadpoles that will be born from a given pregnant mother
   */
  def numberOfTadpolesBorn(pregnantToad: PregnantToad): Int = {
    val BASE = 10

    val RND_MULTIPLIER: Double = Random.between(1, 15) * 0.1

    val DISEASE_MULTIPLIER: Int = 1 //todo

    val POLLUTION_MULTIPLIER: Double =
      pregnantToad.pollutionLevel.description match {
        case `Clear` => 2.5
        case `VeryLow` => 1.3
        case `Low` => 0.7
        case `Medium` => 0.3
        case `High` => 0.1
        case `VeryHigh` => 0.05
        case `Max` => 0
    }

    val MUTATIONS_MULTIPLIER: Double =
      pregnantToad.mutations.length match {
        case 0 => 1
        case 1 => 0.8
        case len if len >= 2 && len <= 4 => 0.5
        case len if len >= 5 && len <= 10 => 0.25
        case len if len > 10 => 0.1
      }

    val RARITY_MULTIPLIER: Double =
      pregnantToad.rarity.description match {
        case `Common` => 2
        case `Rare` => 1
        case `Epic` => 0.5
        case `Legendary` => 0.2
      }

    val result: Int = (
      BASE *
        POLLUTION_MULTIPLIER *
        MUTATIONS_MULTIPLIER *
        RARITY_MULTIPLIER *
        DISEASE_MULTIPLIER *
        RND_MULTIPLIER
      ).toInt

    result
  }

  /**
   * Generates new tadpole based on its parents characteristics
   */
  def generateTadPoleByParent(pregnantToad: PregnantToad): Tadpole = {
    val tadpoleBreed = if (Random.nextBoolean()) pregnantToad.breed else pregnantToad.fatherBreed

    val tadpoleIsCannibal =
      if (pregnantToad.isCannibal && Random.between(0, 100) <= 75) true
      else if (Random.between(0, 100) <= 15) true
      else false

    val tadpoleRarity =
      if (Random.between(0, 100) <= 85) pregnantToad.rarity
      else pregnantToad.rarity.lessRarity

    val tadpolePollutionLevel =
      pregnantToad.pollutionLevel.combineWith(new PollutionLevel(None))

    val result = Tadpole(
      id = UUID.randomUUID().toString,
      farmName = pregnantToad.farmName,
      name = getRandomElement(MALE_NAMES),
      breed = tadpoleBreed,
      isMale = Random.nextBoolean(),
      age = 0,
      isCannibal = tadpoleIsCannibal,
      rarity = tadpoleRarity,
      color = getRandomElement(COLORS),
      diseaseStatus = None,
      mutations = Vector(), // todo
      pollutionLevel = tadpolePollutionLevel,
      hungerLevel = Hunger(0)
    )

    result
  }

}
