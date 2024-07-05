package com.github.s0lux.lifecycle.managers

import com.github.s0lux.lifecycle.events.AgingEvent
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import java.util.logging.Logger

class LifeCycleAgeManager(private val logger: Logger, private val javaPlugin: JavaPlugin): KoinComponent {
    private var ageCycleJob: Job? = null
    private val updateInterval: Int = javaPlugin.config.getInt("lifecycle.update-interval")
    private val ageInterval: Int = javaPlugin.config.getInt("lifecycle.age-interval")

    var players: MutableList<LifeCyclePlayer> = mutableListOf()
        private set

    fun beginAgeCycle() {
        if (ageCycleJob?.isActive == true) {
            logger.warning("Attempted to begin a new age cycle while there is already an active one.")
            return;
        }

        ageCycleJob = javaPlugin.launch {
            logger.info("Starting age cycle")
            while (isActive) {
                players.filter { it.isAgingEnabled }.forEach { player ->
                    logger.info("Aging player ${player.bukkitPlayer.name} (${player.currentAge}, ${player.currentTicks})")
                    if (player.currentTicks + updateInterval >= ageInterval) {
                        player.currentAge++
                        player.currentTicks = 0

                        val agingEvent = AgingEvent(player)
                        agingEvent.callEvent()
                    }
                    else {
                        player.currentTicks += updateInterval
                    }
                }

                delay(updateInterval.ticks)
            }
        }
    }

    fun registerPlayer(player: LifeCyclePlayer) {
        if (players.contains(player)) {
            logger.warning("Attempted to register an already registered player.")
            return;
        }

        players.add(player)
    }

    fun unregisterPlayer(uuid: String) {
        players.removeIf { it.bukkitPlayer.uniqueId.toString() == uuid }
    }
}