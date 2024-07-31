package com.github.s0lux.lifecycle.player

import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.data.DataManager
import com.github.s0lux.lifecycle.trait.TraitManager
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerListener(
    private val dataManager: DataManager,
    private val agingManager: AgingManager,
    private val traitManager: TraitManager
) : Listener {

    @EventHandler
    suspend fun onPlayerJoin(event: PlayerJoinEvent) {
        val bukkitPlayer = event.player
        val lifeCyclePlayer = dataManager.getPlayer(bukkitPlayer.uniqueId.toString())

        traitManager.activateTraits(lifeCyclePlayer)
        agingManager.enrollPlayerToAgingCycle(lifeCyclePlayer)
        agingManager.updatePlayerStageEffects(lifeCyclePlayer)
    }

    @EventHandler
    suspend fun onPlayerQuit(event: PlayerQuitEvent) {
        val bukkitPlayer = event.player
        val lifeCyclePlayer = agingManager.players.find { it.bukkitPlayer == bukkitPlayer }

        if (lifeCyclePlayer is LifeCyclePlayer) {
            agingManager.unenrollPlayerFromAgingCycle(bukkitPlayer.uniqueId.toString())
            traitManager.deactivateTraits(lifeCyclePlayer)
            agingManager.clearPlayerStageEffects(lifeCyclePlayer)
            dataManager.savePlayers(listOf(lifeCyclePlayer))
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val bukkitPlayer = event.player
        val lifeCyclePlayer = agingManager.players.find { it.bukkitPlayer == bukkitPlayer }

        if (lifeCyclePlayer is LifeCyclePlayer) {
            agingManager.clearPlayerStageEffects(lifeCyclePlayer)
            traitManager.activateTraits(lifeCyclePlayer)
            agingManager.resetPlayerAge(lifeCyclePlayer)
        }
    }

    @EventHandler
    suspend fun onPlayerRespawn(event: PlayerRespawnEvent) {
        delay(1.ticks)

        val bukkitPlayer = event.player
        val lifeCyclePlayer = agingManager.players.find { it.bukkitPlayer == bukkitPlayer }

        if (lifeCyclePlayer is LifeCyclePlayer) {
            traitManager.activateTraits(lifeCyclePlayer)
            agingManager.updatePlayerStageEffects(lifeCyclePlayer)
        }
    }
}