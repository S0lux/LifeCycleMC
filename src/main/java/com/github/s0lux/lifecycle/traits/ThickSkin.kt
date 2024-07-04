package com.github.s0lux.lifecycle.traits

import com.github.s0lux.lifecycle.utils.enums.Rarity
import com.github.s0lux.lifecycle.utils.interfaces.Trait
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.plugin.java.JavaPlugin

object ThickSkin : Trait {
    override val name: String
        get() = "Thick Skin"
    override val rarity: Rarity
        get() = Rarity.COMMON
    override val isHereditary: Boolean
        get() = false

    override fun apply(player: LifeCyclePlayer, javaPlugin: JavaPlugin) {
        val bukkitPlayer = player.bukkitPlayer
        val attribute = bukkitPlayer.getAttribute(Attribute.GENERIC_ARMOR)

        if (attribute != null) {
            attribute.baseValue += 0.5
        }
    }

    override fun unApply(player: LifeCyclePlayer, javaPlugin: JavaPlugin) {
        val bukkitPlayer = player.bukkitPlayer
        val attribute = bukkitPlayer.getAttribute(Attribute.GENERIC_ARMOR)

        if (attribute != null) {
            attribute.baseValue -= 0.5
        }
    }
}