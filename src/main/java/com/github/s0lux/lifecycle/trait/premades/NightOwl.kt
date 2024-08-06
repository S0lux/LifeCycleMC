package com.github.s0lux.lifecycle.trait.premades

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Rarity
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.plugin.java.JavaPlugin

object NightOwl : Trait {
    override val name: String = "Night Owl"
    override val rarity: Rarity = Rarity.COMMON
    override val description: Component =
        Component.text("You're more active at night. ")
            .append(Component.text("+10% speed at night, -5% speed during day", NamedTextColor.AQUA))
    override val isHereditary: Boolean = false

    private val playerModifiers = mutableMapOf<LifeCyclePlayer, Boolean>()
    var overworld: World? = null

    override fun initialize(javaPlugin: JavaPlugin) {
        overworld = javaPlugin.server.worlds.first()

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
            val modifier = if (isNight) 1.1 else 0.95
            if (playerModifiers[player] != isNight) {
                attribute.baseValue *= modifier
                playerModifiers[player] = isNight
            }
        }
    }

    override fun apply(player: LifeCyclePlayer) {
        playerModifiers[player] = false
        applySpeedModifier(player, overworld?.time?.toInt() !in 13000..23000)
    }

    override fun unApply(player: LifeCyclePlayer) {
        playerModifiers.remove(player)

        val attribute = player.bukkitPlayer.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
        if (attribute != null) {
            attribute.baseValue = attribute.defaultValue
        }
    }
}