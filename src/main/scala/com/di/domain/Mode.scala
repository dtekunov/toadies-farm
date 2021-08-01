package com.di.domain

trait Mode {
  val name: String
  val startBalance: Long
  val isCreative: Boolean
}

case object Creative extends Mode {
  final val name: String = "creative"
  final val startBalance: Long = -1
  final val isCreative: Boolean = true
}

case object Survival extends Mode {
  final val name: String = "survival"
  final val startBalance: Long = 1000
  final val isCreative: Boolean = false
}

case object Hardcore extends Mode {
  final val name: String = "hardcore"
  final val startBalance: Long = 100
  final val isCreative: Boolean = false
}
