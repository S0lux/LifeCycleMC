package com.github.s0lux.lifecycle.trait

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import com.github.s0lux.lifecycle.trait.premades.*
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import java.util.logging.Logger

class TraitManager(private val logger: Logger, private val javaPlugin: JavaPlugin) : KoinComponent {
    val traits: MutableList<Trait> = mutableListOf(
        ThickSkin,
        NightOwl,
        Photosynthesis,
        Chameleon,
        GreenThumb,
        TerrainMaster,
        AquaticAdept,
        LongLife,
        Cancer
    )

    private val appliedTraits: MutableMap<LifeCyclePlayer, MutableList<Trait>> = mutableMapOf()

    fun initializeTraits() {
        traits.forEach { trait ->
            trait.initialize(javaPlugin)
        }
    }

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

    fun addTraitToPlayer(player: LifeCyclePlayer, slot: Int, overrideTrait: Trait? = null): Trait? {
        var trait: Trait? = null

        if (overrideTrait == null) {
            trait = getRandomTrait(player.traits.map { it.name })
        }
        else trait = overrideTrait

        if (trait != null) {
            if (player.traits.size <= slot) {
                player.traits.add(trait)
                return trait
            } else {
                player.traits[slot] = trait
                return trait
            }
        }

        logger.warning("Unable to generate a suitable trait for player: ${player.bukkitPlayer.name}")
        return null
    }

    fun activateTraits(player: LifeCyclePlayer) {
        val activeTraits = appliedTraits[player]

        if (activeTraits == null) {
            appliedTraits[player] = mutableListOf()
            player.traits.forEach { trait ->
                trait.apply(player)
                appliedTraits[player]?.add(trait)
            }
        }
        else {
            player.traits.forEach { trait ->
                if (trait !in activeTraits) {
                    trait.apply(player)
                    appliedTraits[player]?.add(trait)
                }
            }
        }
    }

    fun deactivateTraits(player: LifeCyclePlayer) {
        val activeTraits = appliedTraits[player]

        if (activeTraits != null) {
            activeTraits.forEach { trait ->
                trait.unApply(player)
            }

            appliedTraits.remove(player)
        }
    }
}