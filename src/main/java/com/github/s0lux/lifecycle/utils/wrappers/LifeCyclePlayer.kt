package com.github.s0lux.lifecycle.utils.wrappers

import com.github.s0lux.lifecycle.utils.interfaces.Trait
import org.bukkit.GameMode
import org.bukkit.entity.Player

class LifeCyclePlayer(
    var currentAge: Int, var currentTicks: Int, var traits: List<Trait>, val bukkitPlayer: Player
) {
    val isAgingEnabled: Boolean
        get() = bukkitPlayer.gameMode == GameMode.SURVIVAL || bukkitPlayer.gameMode == GameMode.ADVENTURE
}