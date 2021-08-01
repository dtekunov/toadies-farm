package com.di.domain

import java.util.UUID

class DeadToadAgeUpdateException(ex: String) extends Exception(ex)

trait Toad {
  val id: String
  val farmName: String
  val name: String
  val breed: String
  val isMale: Boolean
  val age: Int
  val isCannibal: Boolean
  val rarity: String
  val color: String
  val diseaseStatus: Option[String]
  val mutations: Vector[String]
  val pollutionLevel: Option[Int]

  def updateAge: Toad
  def toadStatus: String
  def isAlive: Boolean
}

case class GrownToad(id: String,
                     farmName: String,
                     name: String,
                     breed: String,
                     isMale: Boolean,
                     age: Int,
                     isCannibal: Boolean,
                     rarity: String,
                     color: String,
                     diseaseStatus: Option[String],
                     mutations: Vector[String],
                     pollutionLevel: Option[Int],
                     fertility: Boolean) extends Toad {
  def updateAge: Toad =
    if (age + 1 > 100)
      DeadToad(id, farmName, name, breed, isMale, isCannibal, rarity, color)
    else
      GrownToad(
        id,
        farmName,
        name,
        breed,
        isMale,
        age + 1,
        isCannibal,
        rarity,
        color,
        diseaseStatus,
        mutations,
        pollutionLevel,
        fertility
      )
  def toadStatus = "grownToad"
  def isAlive = true
}

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
}

case class Tadpole(id: String,
                   farmName: String,
                   name: String,
                   breed: String,
                   isMale: Boolean,
                   age: Int,
                   isCannibal: Boolean,
                   rarity: String,
                   color: String,
                   diseaseStatus: Option[String],
                   mutations: Vector[String],
                   pollutionLevel: Option[Int]) extends Toad {
  def updateAge: Toad = //todo think about logic
    if (age + 1 > 100)
      GrownToad(
        id,
        farmName,
        name,
        breed,
        isMale,
        age = 0,
        isCannibal,
        rarity,
        color,
        diseaseStatus,
        mutations,
        pollutionLevel,
        fertility =
          if (diseaseStatus.isDefined) false //todo: rewrite
          else true
      )
    else
      Tadpole(
        id,
        farmName,
        name,
        breed,
        isMale,
        age + 1,
        isCannibal,
        rarity,
        color,
        diseaseStatus,
        mutations,
        pollutionLevel)

  def toadStatus = "tadpole"
  def isAlive = true
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
}

case class DeadToad(id: String,
                    farmName: String,
                    name: String,
                    breed: String,
                    isMale: Boolean,
                    isCannibal: Boolean,
                    rarity: String,
                    color: String) extends Toad {

  final val diseaseStatus = None
  final val mutations = Vector.empty[String]
  final val pollutionLevel = None
  final val age = -1

  def updateAge = throw new DeadToadAgeUpdateException("Dead toad's age cannot be updated")
  def toadStatus = "deadToad"
  def isAlive = false
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