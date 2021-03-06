package com.di.domain

case class Farm(id: String,
                name: String,
                mode: String,
                isCannibal: Boolean,
                mutationsModifier: Long)

object Farm {
  final val idDbName = "id"
  final val nameDbName = "name"
  final val modeDbName = "mode"
  final val isCannibalDbName = "is_cannibal"
  final val mutationsModifierDbName = "mutations_modifier"
}
