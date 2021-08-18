package com.di.types

case class Hunger(level: Int) {
  val value: Int = {
    if (level > 30) 30
    else if (level < 0) 0
    else level
  }

  def increase: Hunger = Hunger(value + 1)

  def increaseForPregnant: Hunger = Hunger(value + 2)

  def decrease: Hunger = Hunger(value - 1)

  def feed(amount: Int): Hunger = Hunger(value - amount)

  def feedCannibal: Hunger = Hunger(0)

  def isNonCannibalHungry: Boolean = value > 15

  def isCannibalHungry: Boolean = value > 25
}
