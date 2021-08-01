package com.di.toadsArithmetics

import com.di.domain.{DeadToad, GrownToad, Tadpole, Toad}

trait ToadsArithmetics {

  def groupToadsByType(toads: Seq[Toad]): ToadsGrouped = toads.foldLeft(
    ToadsGrouped(
      Vector.empty[Tadpole],
      Vector.empty[GrownToad],
      Vector.empty[DeadToad])) {
    (acc, elem) => elem match {
      case tadpole: Tadpole =>
        ToadsGrouped(acc.tadpoles :+ tadpole, acc.grownToads, acc.deadToads)
      case grownToad: GrownToad =>
        ToadsGrouped(acc.tadpoles, acc.grownToads :+ grownToad, acc.deadToads)
      case deadToad: DeadToad =>
        ToadsGrouped(acc.tadpoles, acc.grownToads, acc.deadToads :+ deadToad)
    }
  }

  def pairTwoToads(toad1: GrownToad, toad2: GrownToad) = ???

}

case class ToadsGrouped(tadpoles: Vector[Tadpole], grownToads: Vector[GrownToad], deadToads: Vector[DeadToad])
