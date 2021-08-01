package com.di

package object names {

  //todo: finalize
  lazy final val BREEDS = Vector(
    "Lepidobatrachus laevis"
  )

  lazy final val NAMES = Vector(
    "Adam",
    "Abraham",
    "James",
    "John"
  )

  lazy final val RARITY: Vector[(String, Double)] = Vector(
    "common" -> 0.73,
    "rare" -> 0.2,
    "epic" -> 0.06,
    "legendary" -> 0.01
  )

  lazy final val COLORS = Vector(
    "grey",
    "yellow",
    "green"
  )

  lazy final val MUTATIONS = Map(
    "three-legged" -> 0.1
  )

  lazy final val DEATHS = Vector(
    "was smashed by a rock ",
    "*accidentally* fell off the roof",
    "was thunderstruck"
  )

}
