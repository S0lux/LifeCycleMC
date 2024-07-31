package com.github.s0lux.lifecycle.trait

import com.github.s0lux.lifecycle.trait.premades.ThickSkin
import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import java.util.logging.Logger

class TraitManager(private val logger: Logger, private val javaPlugin: JavaPlugin) : KoinComponent {
    private val traits: MutableList<Trait> = mutableListOf(
        ThickSkin
    )

    private val appliedTraits: MutableMap<LifeCyclePlayer, MutableList<Trait>> = mutableMapOf()

    fun getTraitFromName(name: String): Trait? {
        if (name.isEmpty()) return null

        val foundTrait: Trait? = traits.find { it.name == name }
        if (foundTrait != null) return foundTrait

        logger.warning("Trait with name \"$name\" not found")
        return null
    }

    private fun getRandomTrait(blacklist: List<String>? = null): Trait? {
        val obtainableTraits = traits.filter { blacklist?.contains(it.name)?.not() ?: true }
        val totalWeight = obtainableTraits.sumOf { it.rarity.weight }
        if (totalWeight <= 0) return null

        val randomNumber = (0 until totalWeight).random()
        var accumulatedWeight = 0

        for (trait in obtainableTraits) {
            accumulatedWeight += trait.rarity.weight
            if (randomNumber < accumulatedWeight) {
                return trait
            }
        }

        return null
    }

    fun addRandomTraitToPlayer(player: LifeCyclePlayer, slot: Int): Trait? {
        val trait: Trait? = getRandomTrait(player.traits.map { it.name })

        if (trait != null) {
            // This is to prevent setAge command from adding more trait than required
            if (player.traits.count() == slot) {
                player.traits.add(trait)
                return trait
            }
            return player.traits.last()
        }

        logger.warning("Unable to generate a suitable trait for player: ${player.bukkitPlayer.name}")
        return null
    }

    fun activateTraits(player: LifeCyclePlayer) {
        val activeTraits = appliedTraits[player]

        if (activeTraits == null) {
            appliedTraits[player] = mutableListOf()
            player.traits.forEach { trait ->
                trait.apply(player, javaPlugin)
                appliedTraits[player]?.add(trait)
            }
        }
        else {
            player.traits.forEach { trait ->
                if (trait !in activeTraits) {
                    trait.apply(player, javaPlugin)
                    appliedTraits[player]?.add(trait)
                }
            }
        }
    }

    fun deactivateTraits(player: LifeCyclePlayer) {
        val activeTraits = appliedTraits[player]

        if (activeTraits != null) {
            activeTraits.forEach { trait ->
                trait.unApply(player, javaPlugin)
            }

            appliedTraits.remove(player)
        }
    }
}