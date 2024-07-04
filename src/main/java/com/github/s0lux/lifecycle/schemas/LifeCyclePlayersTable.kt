package com.github.s0lux.lifecycle.schemas

import org.jetbrains.exposed.sql.Table

object LifeCyclePlayersTable: Table() {
    val uuid = varchar("uuid", length = 36).uniqueIndex()
    val currentAge = integer("current_age")
    val currentTicks = integer("current_ticks")
    val traits = varchar("traits", length = 64).nullable()
}