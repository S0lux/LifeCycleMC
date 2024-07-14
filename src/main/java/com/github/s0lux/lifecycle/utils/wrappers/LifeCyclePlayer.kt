package com.github.s0lux.lifecycle.utils.wrappers

import com.github.s0lux.lifecycle.stages.*
import com.github.s0lux.lifecycle.utils.interfaces.AgeStage
import com.github.s0lux.lifecycle.utils.interfaces.Trait
import kotlinx.coroutines.Job
import org.bukkit.GameMode
import org.bukkit.entity.Player

class LifeCyclePlayer(
    var currentAge: Int,
    var currentTicks: Int,
    var traits: List<Trait>,
    val bukkitPlayer: Player,
    var lifespan: Int,
    var deathJob: Job? = null
) {
    val isAgingEnabled: Boolean
        get() = (bukkitPlayer.gameMode == GameMode.SURVIVAL || bukkitPlayer.gameMode == GameMode.ADVENTURE) && bukkitPlayer.isDead.not()

    val currentStage: AgeStage
        get() = when (currentAge) {
            in infancy -> Infancy
            in earlyChildhood -> EarlyChildhood
            in middleChildhood -> MiddleChildhood
            in adolescence -> Adolescence
            in earlyAdulthood -> EarlyAdulthood
            in adulthood -> Adulthood
            else -> OldAge
        }

    val infancy = 0..2
    val earlyChildhood = 3..6
    val middleChildhood = 7..12
    val adolescence = 13..18
    val earlyAdulthood = 19..30
    val adulthood = 31..50
    val oldAge = 51..100
}