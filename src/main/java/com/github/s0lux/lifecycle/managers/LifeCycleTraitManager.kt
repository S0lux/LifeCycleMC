package com.github.s0lux.lifecycle.managers

import com.github.s0lux.lifecycle.traits.ThickSkin
import com.github.s0lux.lifecycle.utils.interfaces.Trait
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import java.util.logging.Logger

class LifeCycleTraitManager(private val logger: Logger, private val javaPlugin: JavaPlugin) : KoinComponent {
    private val traits: MutableList<Trait> = mutableListOf(
        ThickSkin
    )

    fun getTraitFromName(name: String): Trait? {
        if (name.isEmpty()) return null

        val foundTrait: Trait? = traits.find { it.name == name }
        if (foundTrait != null) return foundTrait

        logger.warning("Trait with name \"$name\" not found")
        return null
    }

    fun getRandomTrait(): Trait? {
        val totalWeight = traits.sumOf { it.rarity.weight }
        if (totalWeight <= 0) return null

        val randomNumber = (0 until totalWeight).random()
        var accumulatedWeight = 0

        for (trait in traits) {
            accumulatedWeight += trait.rarity.weight
            if (randomNumber < accumulatedWeight) {
                return trait
            }
        }

        return null
    }
}