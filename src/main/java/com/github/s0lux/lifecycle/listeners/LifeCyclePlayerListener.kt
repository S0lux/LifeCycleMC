package com.github.s0lux.lifecycle.listeners

import com.github.s0lux.lifecycle.managers.LifeCycleAgeManager
import com.github.s0lux.lifecycle.managers.LifeCycleDataManager
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class LifeCyclePlayerListener(
    private val lifeCycleDataManager: LifeCycleDataManager,
    private val lifeCycleAgeManager: LifeCycleAgeManager,
) : Listener {

    @EventHandler
    suspend fun onPlayerJoin(event: PlayerJoinEvent) {
        val bukkitPlayer = event.player
        val lifeCyclePlayer = lifeCycleDataManager.getPlayer(bukkitPlayer.uniqueId.toString())

        lifeCycleAgeManager.registerPlayer(lifeCyclePlayer)
    }

    @EventHandler
    suspend fun onPlayerQuit(event: PlayerQuitEvent) {
        val bukkitPlayer = event.player
        val lifeCyclePlayer = lifeCycleAgeManager.players.find { it.bukkitPlayer == bukkitPlayer }

        lifeCycleAgeManager.unregisterPlayer(bukkitPlayer.uniqueId.toString())

        if (lifeCyclePlayer is LifeCyclePlayer) {
            lifeCycleDataManager.savePlayers(listOf(lifeCyclePlayer))
        }
    }
}