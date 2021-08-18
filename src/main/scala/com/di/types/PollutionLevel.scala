package com.di.types

trait PolLevel

case object Clear    extends PolLevel
case object VeryLow  extends PolLevel
case object Low      extends PolLevel
case object Medium   extends PolLevel
case object High     extends PolLevel
case object VeryHigh extends PolLevel
case object Critical extends PolLevel
case object Max      extends PolLevel

case class NoSuchPollutionLevelException(msg: String) extends Exception(msg)

case class PollutionLevel(level: Option[Int]) {
  val value: Option[Int] =
    if (level.getOrElse(0) > 100) Some(100)
    else level //todo: think about an exception

  override def toString: String =
    value.getOrElse(0).toString

  def combineWith(other: PollutionLevel): PollutionLevel =
    PollutionLevel(Some(((value.getOrElse(0) + other.value.getOrElse(0)) / 2 * 0.8).toInt))

  def +(toAdd: Int): PollutionLevel =
    if (value.getOrElse(0) + toAdd >= 100) new PollutionLevel(Some(100))
    else PollutionLevel(Some(value.getOrElse(0) + toAdd))

  def -(toMinus: Int): PollutionLevel =
    if (value.getOrElse(0) - toMinus <= 0) new PollutionLevel(Some(0))
    else PollutionLevel(Some(value.getOrElse(0) - toMinus))

  def description: PolLevel = {
    val comparable = PollutionLevel(value)
    if (comparable.isClear) Clear
    else if (comparable.isVeryLow) VeryLow
    else if (comparable.isLow) Low
    else if (comparable.isMedium) Medium
    else if (comparable.isMax) Max
    else if (comparable.isCritical) Critical
    else if (comparable.isVeryHigh) VeryHigh
    else if (comparable.isHigh) High
    else throw NoSuchPollutionLevelException("Invalid pollution level")

  }

  def isClear: Boolean = value.getOrElse(0) <= 5

  def isVeryLow: Boolean = value.getOrElse(0) <= 15

  def isLow: Boolean = value.getOrElse(0) <= 30

  def isMedium: Boolean = value.getOrElse(0) < 50 && value.getOrElse(0) > 30

  def isHigh: Boolean = value.getOrElse(0) >= 50

  def isVeryHigh: Boolean = value.getOrElse(0) >= 75

  def isCritical: Boolean = value.getOrElse(0) >= 90

  def isMax: Boolean = value.getOrElse(0) == 100
}
