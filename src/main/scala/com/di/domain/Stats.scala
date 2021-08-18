package com.di.domain

case class Stats(farmName: String, totalToads: Long, toadsDied: Long) {
}

object Stats {
  final val farmNameDb = "farm_name"
  final val totalToadsDb = "total_toads"
  final val toadsDiedDb = "toads_died"
}