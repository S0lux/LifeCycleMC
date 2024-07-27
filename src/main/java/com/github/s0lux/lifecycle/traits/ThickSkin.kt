package com.github.s0lux.lifecycle.traits

import com.github.s0lux.lifecycle.utils.enums.Rarity
import com.github.s0lux.lifecycle.utils.interfaces.Trait
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.attribute.Attribute
import org.bukkit.plugin.java.JavaPlugin

object ThickSkin : Trait {
    override val name: String = "Thick Skin"
    override val rarity: Rarity = Rarity.COMMON
    override val description: Component =
        Component.text("Your skin is thicker than normal people. ")
            .append(Component.text("+0.5HP", NamedTextColor.RED))
    override val isHereditary: Boolean = false

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