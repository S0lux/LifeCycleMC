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
import org.bukkit.plugin.java.JavaPlugin

object Photosynthesis : Trait {
    override val name: String = "Photosynthesis"
    override val rarity: Rarity = Rarity.EPIC
    override val description: Component =
        Component.text("You can absorb energy from sunlight.")
            .appendNewline()
            .append(Component.text("Slowly regain hunger while in direct sunlight.", NamedTextColor.GREEN))
    override val isHereditary: Boolean = true

    private val playersWithTrait = mutableSetOf<LifeCyclePlayer>()
    private var overworld: World? = null

    override fun initialize(javaPlugin: JavaPlugin) {
        overworld = javaPlugin.server.worlds.find { it.environment == World.Environment.NORMAL }

        javaPlugin.launch {
            while (true) {
                playersWithTrait.forEach { player ->
                    if (isPlayerInSunlight(player)) {
                        regenerateHunger(player)
                    }
                }
                delay(100.ticks)
            }
        }
    }

    private fun isPlayerInSunlight(player: LifeCyclePlayer): Boolean {
        val location = player.bukkitPlayer.location
        return location.world.environment == World.Environment.NORMAL &&
                location.block.lightFromSky.toInt() == 15 &&
                overworld?.time?.toInt() in 0..12000
    }

    private fun regenerateHunger(player: LifeCyclePlayer) {
        val foodLevel = player.bukkitPlayer.foodLevel
        if (foodLevel < 20) {
            player.bukkitPlayer.foodLevel = minOf(foodLevel + 1, 20)
        }
    }

    override fun apply(player: LifeCyclePlayer) {
        playersWithTrait.add(player)
    }

    override fun unApply(player: LifeCyclePlayer) {
        playersWithTrait.remove(player)
    }
}