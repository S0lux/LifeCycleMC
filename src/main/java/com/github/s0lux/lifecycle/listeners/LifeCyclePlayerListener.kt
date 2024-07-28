package com.github.s0lux.lifecycle.listeners

import com.github.s0lux.lifecycle.events.AgingEvent
import com.github.s0lux.lifecycle.managers.LifeCycleAgeManager
import com.github.s0lux.lifecycle.managers.LifeCycleDataManager
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class LifeCyclePlayerListener(
    private val lifeCycleDataManager: LifeCycleDataManager,
    private val lifeCycleAgeManager: LifeCycleAgeManager,
) : Listener {

    @EventHandler
    suspend fun onPlayerJoin(event: PlayerJoinEvent) {
        val bukkitPlayer = event.player
        val lifeCyclePlayer = lifeCycleDataManager.getPlayer(bukkitPlayer.uniqueId.toString())

        lifeCycleAgeManager.enrollPlayerToAgingCycle(lifeCyclePlayer)
        lifeCycleAgeManager.updatePlayerStageEffects(lifeCyclePlayer)
    }

    @EventHandler
    suspend fun onPlayerQuit(event: PlayerQuitEvent) {
        val bukkitPlayer = event.player
        val lifeCyclePlayer = lifeCycleAgeManager.players.find { it.bukkitPlayer == bukkitPlayer }

        if (lifeCyclePlayer is LifeCyclePlayer) {
            lifeCycleAgeManager.unenrollPlayerFromAgingCycle(bukkitPlayer.uniqueId.toString())
            lifeCycleAgeManager.clearPlayerStageEffects(lifeCyclePlayer)
            lifeCycleDataManager.savePlayers(listOf(lifeCyclePlayer))
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val bukkitPlayer = event.player
        val lifeCyclePlayer = lifeCycleAgeManager.players.find { it.bukkitPlayer == bukkitPlayer }

        if (lifeCyclePlayer is LifeCyclePlayer) {
            lifeCycleAgeManager.clearPlayerStageEffects(lifeCyclePlayer)
            lifeCycleAgeManager.resetPlayerAge(lifeCyclePlayer)
        }
    }

    @EventHandler
    suspend fun onPlayerRespawn(event: PlayerRespawnEvent) {
        delay(1.ticks)

        val bukkitPlayer = event.player
        val lifeCyclePlayer = lifeCycleAgeManager.players.find { it.bukkitPlayer == bukkitPlayer }

        if (lifeCyclePlayer is LifeCyclePlayer) {
            lifeCycleAgeManager.updatePlayerStageEffects(lifeCyclePlayer)
        }
    }
}