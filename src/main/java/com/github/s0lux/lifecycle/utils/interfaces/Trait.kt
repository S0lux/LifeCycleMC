package com.github.s0lux.lifecycle.utils.interfaces

import com.github.s0lux.lifecycle.utils.enums.Rarity
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import org.bukkit.plugin.java.JavaPlugin

interface Trait {
    val name: String
    val rarity: Rarity
    val isHereditary: Boolean

    fun apply(player: LifeCyclePlayer, javaPlugin: JavaPlugin)
    fun unApply(player: LifeCyclePlayer, javaPlugin: JavaPlugin)
}