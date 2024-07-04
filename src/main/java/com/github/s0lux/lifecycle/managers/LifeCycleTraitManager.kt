package com.github.s0lux.lifecycle.managers

import com.github.s0lux.lifecycle.traits.ThickSkin
import com.github.s0lux.lifecycle.utils.interfaces.Trait
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import java.util.logging.Logger

class LifeCycleTraitManager(private val logger: Logger, private val javaPlugin: JavaPlugin) : KoinComponent {
    private val enabledTraits: Map<String, Trait> = mapOf(
        "Thick Skin" to ThickSkin
    )

    fun getTrait(name: String): Trait? {
        val trait = enabledTraits[name]

        if (trait is Trait) {
            return trait
        }
        else {
            logger.warning("There is no trait with name \"$name\"")
            return null
        }
    }
}