package com.github.s0lux.lifecycle.player

import com.github.s0lux.lifecycle.trait.interfaces.Trait
import kotlinx.coroutines.Job
import org.bukkit.GameMode
import org.bukkit.entity.Player

class BukkitPlayerWrapper(
    var currentAge: Int,
    var currentTicks: Int,
    var traits: MutableList<Trait>,
    val bukkitPlayer: Player,
    var lifespan: Int,
    var deathJob: Job? = null
) {
    val isAgingEnabled: Boolean
        get() = (bukkitPlayer.gameMode == GameMode.SURVIVAL || bukkitPlayer.gameMode == GameMode.ADVENTURE) && bukkitPlayer.isDead.not()
}