package com.github.s0lux.lifecycle.trait.interfaces

import com.github.s0lux.lifecycle.player.BukkitPlayerWrapper
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin

interface Trait {
    val name: String
    val rarity: Rarity
    val description: Component

    // Doesn't do anything yet, maybe in the future it'll do something
    val isHereditary: Boolean

    fun apply(player: BukkitPlayerWrapper, javaPlugin: JavaPlugin)
    fun unApply(player: BukkitPlayerWrapper, javaPlugin: JavaPlugin)
}