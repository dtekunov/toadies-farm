package com.di.domain

import java.util.UUID

case class Owner(id: String,
                 farmName: String,
                 balance: Long,
                 isCreative: Boolean,
                 transactionsMade: Long,
                 numberOfCycles: BigInt) {
}

object Owner {
  def createNew(farmName: String, mode: Mode): Owner =
    Owner(UUID.randomUUID().toString, farmName, mode.startBalance, mode.isCreative, 0, 0)

  final val idDb = "id"
  final val farmNameDb = "farm_name"
  final val balanceDb = "balance"
  final val isCreativeDb = "is_creative"
  final val transactionsMadeDb = "transactions_made"
  final val numberOfCyclesDb = "number_of_cycles"

}