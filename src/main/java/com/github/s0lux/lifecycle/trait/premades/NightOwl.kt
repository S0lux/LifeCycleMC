package com.github.s0lux.lifecycle.trait.premades

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Rarity
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Namespaced
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.plugin.java.JavaPlugin

object NightOwl : Trait {
    override val name: String = "Night Owl"
    override val rarity: Rarity = Rarity.COMMON
    override val description: Component = Component.text("You're more active at night. ").appendNewline()
        .append(Component.text("+10% speed at night, -5% speed during day", NamedTextColor.AQUA))
    override val isHereditary: Boolean = false

    var overworld: World? = null
    private val playerModifiers = mutableMapOf<LifeCyclePlayer, Boolean>()

    lateinit var dayModifier: AttributeModifier
    lateinit var nightModifier: AttributeModifier

    override fun initialize(javaPlugin: JavaPlugin) {
        this.dayModifier = AttributeModifier(
            NamespacedKey(javaPlugin, "night-owl-speed-day"), -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )
        this.nightModifier = AttributeModifier(
            NamespacedKey(javaPlugin, "night-owl-speed-night"), 1.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )

        overworld = javaPlugin.server.worlds.find { it.environment == World.Environment.NORMAL }

        javaPlugin.launch {
            var wasNight = false
            while (true) {
                val isNight = overworld?.time?.toInt() in 13000..23000
                if (isNight != wasNight) {
                    // Time transition occurred
                    playerModifiers.keys.forEach { player ->
                        applySpeedModifier(player, isNight)
                    }
                    wasNight = isNight
                }
                delay(5.ticks)
            }
        }
    }

    private fun applySpeedModifier(player: LifeCyclePlayer, isNight: Boolean) {
        val attribute = player.bukkitPlayer.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
        if (attribute != null) {
            if (playerModifiers[player] != isNight) {
                attribute.removeModifier(if (isNight) dayModifier else nightModifier)
                attribute.addModifier(if (isNight) nightModifier else dayModifier)
                playerModifiers[player] = isNight
            }
        }
    }

    override fun apply(player: LifeCyclePlayer) {
        playerModifiers[player] = overworld?.time?.toInt() !in 13000..23000
        applySpeedModifier(player, overworld?.time?.toInt() in 13000..23000)
    }

    override fun unApply(player: LifeCyclePlayer) {
        playerModifiers.remove(player)

        val attribute = player.bukkitPlayer.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
        if (attribute != null) {
            attribute.removeModifier(dayModifier)
            attribute.removeModifier(nightModifier)
        }
    }
}