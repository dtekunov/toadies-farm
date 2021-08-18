package com.di.types

trait RarityDesc
case object Common    extends RarityDesc
case object Rare      extends RarityDesc
case object Epic      extends RarityDesc
case object Legendary extends RarityDesc

case class NoSuchRarityException(msg: String) extends Exception(msg)

case class Rarity(value: String) {
  val description: RarityDesc = value.toLowerCase match {
    case "common" => Common
    case "rare" => Rare
    case "epic" => Epic
    case "legendary" => Legendary
    case _ => throw NoSuchRarityException("Invalid rarity provided")
  }

  def compareWith(another: Rarity): Int =
    (description, another.description) match {
      case (`Common`, `Common`) => 0
      case (`Common`, _) => -1
      case (`Rare`, `Common`) => 1
      case (`Rare`, `Rare`) => 0
      case (`Rare`, _) => -1
      case (`Epic`, `Legendary`) => -1
      case (`Epic`, `Epic`) => 0
      case (`Epic`, _) => 1
      case (`Legendary`, `Legendary`) => 0
      case (`Legendary`, _) => 1
  }

  def lessRarity: Rarity = description match {
    case `Legendary` => Rarity("epic")
    case `Epic` => Rarity("rare")
    case `Rare` => Rarity("common")
    case `Common` => Rarity("common")
  }
}
