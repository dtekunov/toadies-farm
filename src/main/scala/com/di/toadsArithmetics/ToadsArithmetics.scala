package com.di.toadsArithmetics

import com.di.domain.{DeadToad, GrownToad, PregnantToad, Tadpole, Toad}

case class ToadsCannotHaveSameGenderException(msg: String) extends Exception(msg)

trait ToadsArithmetics {

  def groupToadsByType(toads: Seq[Toad]): ToadsGrouped = toads.foldLeft(
    ToadsGrouped(
      Vector.empty[Tadpole],
      Vector.empty[GrownToad],
      Vector.empty[DeadToad],
      Vector.empty[PregnantToad])) {
    (acc, elem) => elem match {
      case tadpole: Tadpole =>
        ToadsGrouped(acc.tadpoles :+ tadpole, acc.grownToads, acc.deadToads, acc.pregnantToads)
      case grownToad: GrownToad =>
        ToadsGrouped(acc.tadpoles, acc.grownToads :+ grownToad, acc.deadToads, acc.pregnantToads)
      case deadToad: DeadToad =>
        ToadsGrouped(acc.tadpoles, acc.grownToads, acc.deadToads :+ deadToad, acc.pregnantToads)
      case pregnantToad: PregnantToad =>
        ToadsGrouped(acc.tadpoles, acc.grownToads, acc.deadToads, acc.pregnantToads :+ pregnantToad)
    }
  }

  def countToadsByType(toads: Seq[Toad]): ToadsCount = toads.foldLeft(
    ToadsCount(0L, 0L, 0L, 0L, 0L)) { (acc, elem) =>
    elem match {
      case _: Tadpole =>
        ToadsCount(acc.total + 1, acc.tadpoles + 1, acc.grownToads, acc.deadToads, acc.pregnantToads)
      case _: GrownToad =>
        ToadsCount(acc.total + 1, acc.tadpoles, acc.grownToads + 1, acc.deadToads, acc.pregnantToads)
      case _: DeadToad =>
        ToadsCount(acc.total + 1, acc.tadpoles, acc.grownToads, acc.deadToads + 1, acc.pregnantToads)
      case _: PregnantToad =>
        ToadsCount(acc.total + 1, acc.tadpoles, acc.grownToads, acc.deadToads, acc.pregnantToads + 1)
    }
  }

  def pairTwoToads(toad1: GrownToad, toad2: GrownToad): ToadsPairingResult = {
    if (toad1.isMale && toad1.fertility && !toad2.isMale && toad2.fertility) {
      val mother = toad2.makePregnant(toad1.breed).getOrElse(toad2)
      ToadsPairingResult(toad1, mother, arePaired = true)

    } else if (!toad1.isMale && toad1.fertility && toad2.isMale && toad2.fertility) {
      val mother = toad1.makePregnant(toad2.breed).getOrElse(toad1)
      ToadsPairingResult(mother, toad2, arePaired = true)

    } else ToadsPairingResult(toad1, toad2, arePaired = false)
  }
}

case class ToadsPairingResult(toad1: Toad, toad2: Toad, arePaired: Boolean)

case class ToadsGrouped(tadpoles: Vector[Tadpole],
                        grownToads: Vector[GrownToad],
                        deadToads: Vector[DeadToad],
                        pregnantToads: Vector[PregnantToad])

case class ToadsCount(total: Long,
                      tadpoles: Long,
                      grownToads: Long,
                      deadToads: Long,
                      pregnantToads: Long)
