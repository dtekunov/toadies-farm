package com.di.jsonFormatters.rawdataFormats

import com.di.domain.GrownToad
import com.di.types.{Hunger, PollutionLevel, Rarity}

import java.util.UUID

/**
 * Raw toad info to convert from json to actual data format
 */
trait RawToad {
  val name: String
  val breed: String
  val is_male: Boolean
  val age: Int
  val is_cannibal: Boolean
  val rarity: String
  val color: String
  val disease_status: Option[String]
  val mutations: Vector[String]
  val pollution_level: Option[Int]
}

case class RawGrownToad(name: String,
                        breed: String,
                        is_male: Boolean,
                        age: Int,
                        is_cannibal: Boolean,
                        rarity: String,
                        color: String,
                        disease_status: Option[String],
                        mutations: Vector[String],
                        pollution_level: Option[Int],
                        fertility: Boolean) extends RawToad {
  def toGrownToad(farmName: String): GrownToad = {
    GrownToad(
      UUID.randomUUID().toString,
      farmName,
      name,
      breed,
      is_male,
      age,
      is_cannibal,
      Rarity(rarity),
      color,
      disease_status,
      mutations,
      PollutionLevel(pollution_level),
      fertility,
      Hunger(0)
  )
  }
}

case class RawTadpole(name: String,
                      breed: String,
                      is_male: Boolean,
                      age: Int,
                      is_cannibal: Boolean,
                      rarity: String,
                      color: String,
                      disease_status: Option[String],
                      mutations: Vector[String],
                      pollution_level: Option[Int]) extends RawToad



