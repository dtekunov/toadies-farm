package com.di.domain

import com.di.random.{generateTadPoleByParent, numberOfTadpolesBorn}
import com.di.types.{Hunger, PollutionLevel, Rarity}

class DeadToadAgeUpdateException(ex: String) extends Exception(ex)
class NonCannibalException(ex: String) extends Exception(ex)

/**
 * Basic toads trait. Any toad provided should implement this behavior
 */
trait Toad {
  val id: String
  val farmName: String
  val name: String
  val breed: String
  val isMale: Boolean
  val age: Int
  val isCannibal: Boolean
  val rarity: Rarity
  val color: String
  val diseaseStatus: Option[String]
  val mutations: Vector[String]
  val pollutionLevel: PollutionLevel
  val hungerLevel: Hunger

  def updateAge: Toad

  def updatePollutionLevel(toAdd: Int): Toad

  def increaseHunger: Toad

  def decreaseHunger(toDecrease: Int): Toad

  def toadStatus: String

  def isAlive: Boolean

  def killed: DeadToad

  def isRadioactive: Boolean = mutations.contains("radioactive")

  def isCursedBreed: Boolean = mutations.contains("cursed")

  def isExtraHunger: Boolean = mutations.contains("hunger")

  def absoluteCannibal: Boolean = mutations.contains("cannibal")

}

case class GrownToad(id: String,
                     farmName: String,
                     name: String,
                     breed: String,
                     isMale: Boolean,
                     age: Int,
                     isCannibal: Boolean,
                     rarity: Rarity,
                     color: String,
                     diseaseStatus: Option[String],
                     mutations: Vector[String],
                     pollutionLevel: PollutionLevel,
                     fertility: Boolean,
                     hungerLevel: Hunger) extends Toad {
  def updateAge: Toad =
    if (age + 1 > 100)
      this.killed
    else
      this.copy(age = age + 1)

  def updatePollutionLevel(toAdd: Int): Toad =
    if ((pollutionLevel + toAdd).isMax) this.killed
    else this.copy(pollutionLevel = pollutionLevel + toAdd)

  def increaseHunger: Toad =
    if (hungerLevel.increase.value == 30)
      this.killed
    else this.copy(hungerLevel = hungerLevel.increase)

  def decreaseHunger(toDecrease: Int): Toad =
    this.copy(hungerLevel = hungerLevel.feed(toDecrease))


  def toadStatus = "grownToad"
  def isAlive = true

  def makePregnant(fatherBreed: String): Option[PregnantToad] = {
    if (fertility && !isMale)
      Some(PregnantToad(
        id = id,
        farmName = farmName,
        name = name,
        breed = breed,
        age = age,
        isCannibal = isCannibal,
        rarity = rarity,
        color = color,
        diseaseStatus = diseaseStatus,
        mutations = mutations,
        pollutionLevel = pollutionLevel,
        pregnancyTime = 0,
        fatherBreed = fatherBreed,
        hungerLevel = hungerLevel
      ))
    else None
  }

  def feedCannibal: GrownToad =
    if (isCannibal)
      this.copy(hungerLevel = hungerLevel.feedCannibal)
    else throw new NonCannibalException("About to feed non-cannibal toad with another toad")

  def killed: DeadToad =
    DeadToad(
      id = id,
      farmName = farmName,
      name = name,
      breed = breed,
      isMale = isMale,
      isCannibal = isCannibal,
      rarity = rarity,
      color = color
    )
}

//todo: new fields
object GrownToad {
  final val idDb = "id"
  final val farmNameDb = "farm_name"
  final val nameDb = "name"
  final val breedDb = "breed"
  final val isMaleDb = "is_male"
  final val ageDb = "age"
  final val isCannibalDb = "is_cannibal"
  final val rarityDb = "rarity"
  final val colorDb = "color"
  final val diseaseStatusDb = "disease_status"
  final val mutationsDb = "mutations"
  final val pollutionLevelDb = "pollution_level"
  final val fertilityDb = "fertility"
  final val hungerLevelDb = "hunger_level"
}

case class Tadpole(id: String,
                   farmName: String,
                   name: String,
                   breed: String,
                   isMale: Boolean,
                   age: Int,
                   isCannibal: Boolean,
                   rarity: Rarity,
                   color: String,
                   diseaseStatus: Option[String],
                   mutations: Vector[String],
                   pollutionLevel: PollutionLevel,
                   hungerLevel: Hunger) extends Toad {
  def updateAge: Toad = //todo think about logic
    if (age + 1 > 100)
      GrownToad(id, farmName, name, breed, isMale, age = 0, isCannibal, rarity,
        color, diseaseStatus, mutations, pollutionLevel, fertility = diseaseStatus.isEmpty, //todo: rewrite
        hungerLevel = hungerLevel
      )
    else
      this.copy(age = age + 1)

  def updatePollutionLevel(toAdd: Int): Toad =
    if ((pollutionLevel + toAdd).isMax) this.killed
    else this.copy(pollutionLevel = pollutionLevel + toAdd)

  def increaseHunger: Toad =
    if (hungerLevel.increase.value == 30)
      this.killed
    else
      this.copy(hungerLevel = hungerLevel.increase)

  def decreaseHunger(toDecrease: Int): Toad =
    this.copy(hungerLevel = hungerLevel.feed(toDecrease))

  def toadStatus = "tadpole"
  def isAlive = true

  def killed: DeadToad =
    DeadToad(
      id = id,
      farmName = farmName,
      name = name,
      breed = breed,
      isMale = isMale,
      isCannibal = isCannibal,
      rarity = rarity,
      color = color
    )
}

object Tadpole {
  final val idDb = "id"
  final val farmNameDb = "farm_name"
  final val nameDb = "name"
  final val breedDb = "breed"
  final val isMaleDb = "is_male"
  final val ageDb = "age"
  final val isCannibalDb = "is_cannibal"
  final val rarityDb = "rarity"
  final val colorDb = "color"
  final val diseaseStatusDb = "disease_status"
  final val mutationsDb = "mutations"
  final val pollutionLevelDb = "pollution_level"
  final val hungerLevelDb = "hunger_level"
}

case class DeadToad(id: String,
                    farmName: String,
                    name: String,
                    breed: String,
                    isMale: Boolean,
                    isCannibal: Boolean,
                    rarity: Rarity,
                    color: String) extends Toad {

  final val diseaseStatus = None
  final val mutations = Vector.empty[String]
  final val pollutionLevel = PollutionLevel(None)
  final val age = -1
  final val hungerLevel = Hunger(0)

  def updateAge = throw new DeadToadAgeUpdateException("Dead toad's age cannot be updated")

  def increaseHunger: Toad = this

  def decreaseHunger(toDecrease: Int): Toad = this

  def updatePollutionLevel(toAdd: Int): Toad = this

  def toadStatus = "deadToad"
  def isAlive = false

  def killed: DeadToad = this
}

object DeadToad {
  final val idDb = "id"
  final val farmNameDb = "farm_name"
  final val nameDb = "name"
  final val breedDb = "breed"
  final val isMaleDb = "is_male"
  final val isCannibalDb = "is_cannibal"
  final val rarityDb = "rarity"
  final val colorDb = "color"
  final val diseaseStatusDb = "disease_status"
  final val mutationsDb = "mutations"
  final val pollutionLevelDb = "pollution_level"
  final val ageDb = "age"
}

case class PregnantAndTadpoles(mother: Toad, tadpoles: Option[Vector[Tadpole]])

case class PregnantToad(id: String,
                        farmName: String,
                        name: String,
                        breed: String,
                        age: Int,
                        isCannibal: Boolean,
                        rarity: Rarity,
                        color: String,
                        diseaseStatus: Option[String],
                        mutations: Vector[String],
                        pollutionLevel: PollutionLevel,
                        pregnancyTime: Int,
                        fatherBreed: String,
                        hungerLevel: Hunger) extends Toad {
  final val isMale = false
  final val fertility = true

  def updateAge: Toad =
    if (age + 1 > 100)
      DeadToad(id, farmName, name, breed, isMale, isCannibal, rarity, color)
    else this.copy(age = this.age + 1, pregnancyTime = this.pregnancyTime + 1)

  def updatePollutionLevel(toAdd: Int): Toad =
    if ((pollutionLevel + toAdd).isMax) this.killed
    else this.copy(pollutionLevel = pollutionLevel + toAdd)

  def feedCannibal: PregnantToad =
    if (isCannibal)
      this.copy(hungerLevel = hungerLevel.feedCannibal)
    else throw new NonCannibalException("About to feed non-cannibal toad with another toad")

  def increaseHunger: Toad =
    if (hungerLevel.increase.value == 30)
      this.killed
    else
      this.copy(hungerLevel = hungerLevel.increaseForPregnant)

  def decreaseHunger(toDecrease: Int): Toad =
    this.copy(hungerLevel = hungerLevel.feed(toDecrease))

  def toGrownToad: GrownToad =
    GrownToad(id, farmName, name, breed, isMale  = false, age, isCannibal, rarity,
      color, diseaseStatus, mutations, pollutionLevel, fertility = true, hungerLevel = hungerLevel.decrease
    )

  def pregnancyResultRND: PregnantAndTadpoles = {
    if (pregnancyTime < 10) PregnantAndTadpoles(this, None)
    else {
      val numberOfTadpoles = numberOfTadpolesBorn(this)
      val tadpoles = Vector.fill(numberOfTadpoles)(generateTadPoleByParent(this))
      val updatedMother = this.toGrownToad

      PregnantAndTadpoles(updatedMother, Some(tadpoles))
    }
  }

  def toadStatus = "pregnantToad"
  def isAlive = true

  def killed: DeadToad =
    DeadToad(id = id, farmName = farmName, name = name, breed = breed,
      isMale = isMale, isCannibal = isCannibal, rarity = rarity, color = color
    )
}

object PregnantToad {
  final val idDb = "id"
  final val farmNameDb = "farm_name"
  final val nameDb = "name"
  final val breedDb = "breed"
  final val isMaleDb = "is_male"
  final val ageDb = "age"
  final val isCannibalDb = "is_cannibal"
  final val rarityDb = "rarity"
  final val colorDb = "color"
  final val diseaseStatusDb = "disease_status"
  final val mutationsDb = "mutations"
  final val pollutionLevelDb = "pollution_level"
  final val fertilityDb = "fertility"
}