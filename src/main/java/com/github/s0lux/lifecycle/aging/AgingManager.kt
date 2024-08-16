package com.github.s0lux.lifecycle.aging

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.util.loadAgeStagesFromYaml
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import java.io.File
import java.util.logging.Logger

class AgingManager(private val logger: Logger, private val javaPlugin: JavaPlugin) : KoinComponent {
    private var ageCycleJob: Job? = null
    private val updateInterval: Int = javaPlugin.config.getInt("lifecycle.update-interval")
    private val ageInterval: Int = javaPlugin.config.getInt("lifecycle.age-interval")

    var players: MutableList<LifeCyclePlayer> = mutableListOf()
        private set

    var activeAgeEffects: MutableMap<String, MutableList<StageEffect>> = mutableMapOf()
        private set

    val ageStagesFile = File(javaPlugin.dataFolder, "age_stages.yml")
    val ageStages: AgeStages = loadAgeStagesFromYaml(ageStagesFile, logger)

    fun initializeAgingCycle() {
        if (ageCycleJob?.isActive == true) {
            logger.warning("Attempted to begin a new age cycle while there is already an active one.")
            return;
        }

        ageCycleJob = javaPlugin.launch {
            logger.info("Starting age cycle")
            while (isActive) {
                players.filter { it.isAgingEnabled }.forEach { player ->
                    if (player.currentTicks + updateInterval >= ageInterval) {
                        player.currentAge++
                        player.currentTicks = 0

                        val agingEvent = AgingEvent(player, ageStages.getStageForAge(player.currentAge))
                        agingEvent.callEvent()
                    } else {
                        player.currentTicks += updateInterval
                    }
                }

                delay(updateInterval.ticks)
            }
        }
    }

    fun enrollPlayerToAgingCycle(player: LifeCyclePlayer) {
        if (players.contains(player)) {
            logger.warning("Attempted to register an already registered player.")
            return;
        }
        players.add(player)
    }

    fun unenrollPlayerFromAgingCycle(uuid: String) {
        players.removeIf { it.bukkitPlayer.uniqueId.toString() == uuid }
    }

    private fun addStageEffect(player: LifeCyclePlayer, ageStageEffect: StageEffect) {
        val uuid = player.bukkitPlayer.uniqueId.toString()

        activeAgeEffects.getOrPut(uuid) { mutableListOf() }.add(ageStageEffect)

        when (ageStageEffect) {
            is StageEffect.Potion -> player.bukkitPlayer.addPotionEffect(ageStageEffect.get())
            is StageEffect.Attribute -> {
                player.bukkitPlayer.getAttribute(ageStageEffect.get().attribute)?.baseValue =
                    player.bukkitPlayer.getAttribute(ageStageEffect.get().attribute)?.baseValue?.plus(
                        ageStageEffect.get().value
                    )!!
            }
        }
    }

    fun clearPlayerStageEffects(player: LifeCyclePlayer) {
        val uuid = player.bukkitPlayer.uniqueId.toString()
        val effects = activeAgeEffects[uuid]

        if (!effects.isNullOrEmpty()) {
            effects.forEach { effect ->
                when (effect) {
                    is StageEffect.Potion -> player.bukkitPlayer.removePotionEffect(effect.get().type)
                    is StageEffect.Attribute -> {
                        player.bukkitPlayer.getAttribute(effect.get().attribute)?.baseValue =
                            player.bukkitPlayer.getAttribute(effect.get().attribute)?.baseValue?.minus(
                                effect.get().value
                            )!!
                    }
                }
            }
        }

        activeAgeEffects.remove(uuid)
    }

    fun resetPlayerAge(player: LifeCyclePlayer) {
        player.currentAge = 0
        player.currentTicks = 0
    }

    fun updatePlayerStageEffects(player: LifeCyclePlayer) {
        if (player.currentAge > player.lifespan) {
            if (player.deathJob == null) {
                initiateEndOfLifeProcess(player)
            }
            return
        } else {
            val stage = ageStages.getStageForAge(player.currentAge).stage
            clearPlayerStageEffects(player)
            stage.effects.forEach { effect -> addStageEffect(player, effect) }
        }
    }

    private fun initiateEndOfLifeProcess(player: LifeCyclePlayer) {
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
                    player.deathJob = null
                }

                delay(12.ticks)
            }
        }
    }

    fun findLifeCyclePlayer(uuid: String): LifeCyclePlayer? =
        players.find { it.bukkitPlayer.uniqueId.toString() == uuid }
}