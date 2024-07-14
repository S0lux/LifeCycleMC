package com.github.s0lux.lifecycle.managers

import com.github.s0lux.lifecycle.events.AgingEvent
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.koin.core.component.KoinComponent
import java.util.logging.Logger

class LifeCycleAgeManager(private val logger: Logger, private val javaPlugin: JavaPlugin) : KoinComponent {
    private var ageCycleJob: Job? = null
    private val updateInterval: Int = javaPlugin.config.getInt("lifecycle.update-interval")
    private val ageInterval: Int = javaPlugin.config.getInt("lifecycle.age-interval")

    var players: MutableList<LifeCyclePlayer> = mutableListOf()
        private set

    var activeAgeEffects: MutableMap<String, MutableList<Any>> = mutableMapOf()
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
                    // logger.info("Aging player ${player.bukkitPlayer.name} (${player.currentAge}, ${player.currentTicks})")
                    if (player.currentTicks + updateInterval >= ageInterval) {
                        player.currentAge++
                        player.currentTicks = 0

                        val agingEvent = AgingEvent(player)
                        agingEvent.callEvent()
                    } else {
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

    private fun addAgeEffect(player: LifeCyclePlayer, effect: Any) {
        val uuid = player.bukkitPlayer.uniqueId.toString()

        activeAgeEffects.getOrPut(uuid) { mutableListOf() }.add(effect)

        when (effect) {
            is PotionEffect -> player.bukkitPlayer.addPotionEffect(effect)
            is Pair<*, *> -> {
                if (effect.first is Attribute && effect.second is Double) {
                    player.bukkitPlayer.getAttribute(effect.first as Attribute)?.baseValue =
                        player.bukkitPlayer.getAttribute(effect.first as Attribute)?.baseValue?.plus(
                            effect.second as Double
                        )!!
                }
            }
        }
    }

    fun removeAgeEffects(player: LifeCyclePlayer) {
        val uuid = player.bukkitPlayer.uniqueId.toString()
        val effects = activeAgeEffects.get(uuid)

        if (!effects.isNullOrEmpty()) {
            effects.forEach { effect ->
                when (effect) {
                    is PotionEffect -> player.bukkitPlayer.removePotionEffect(effect.type)
                    is Pair<*, *> -> {
                        if (effect.first is Attribute && effect.second is Double) {
                            player.bukkitPlayer.getAttribute(effect.first as Attribute)?.baseValue =
                                player.bukkitPlayer.getAttribute(effect.first as Attribute)?.baseValue?.minus(
                                    effect.second as Double
                                )!!
                        }
                    }
                }
            }
        }

        activeAgeEffects.remove(uuid)
    }

    fun resetAge(player: LifeCyclePlayer) {
        player.currentAge = 0
        player.currentTicks = 0
    }

    fun applyAge(player: LifeCyclePlayer) {
        if (player.currentAge > player.lifespan) {
            if (player.deathJob == null) {
                player.deathJob = javaPlugin.launch {
                    val bukkitPlayer = player.bukkitPlayer

                    while (isActive) {
                        if (bukkitPlayer.health <= 2) {
                            bukkitPlayer.health = 0.0
                        } else {
                            bukkitPlayer.health -= 2
                        }

                        if (bukkitPlayer.isDead || bukkitPlayer.isConnected.not()) {
                            cancel()
                        }

                        delay(12.ticks)
                    }
                }
            }
            return
        } else {
            removeAgeEffects(player)

            player.currentStage.potionEffects.forEach { effect ->
                addAgeEffect(player, effect)
            }
            player.currentStage.attributeModifiers.forEach { effect ->
                addAgeEffect(player, effect)
            }
        }
    }
}