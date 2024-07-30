package com.github.s0lux.lifecycle.trait.premades

import com.github.s0lux.lifecycle.player.BukkitPlayerWrapper
import com.github.s0lux.lifecycle.trait.interfaces.Rarity
import com.github.s0lux.lifecycle.trait.interfaces.Trait
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

    override fun apply(player: BukkitPlayerWrapper, javaPlugin: JavaPlugin) {
        val bukkitPlayer = player.bukkitPlayer
        val attribute = bukkitPlayer.getAttribute(Attribute.GENERIC_ARMOR)

        if (attribute != null) {
            attribute.baseValue += 0.5
        }
    }

    override fun unApply(player: BukkitPlayerWrapper, javaPlugin: JavaPlugin) {
        val bukkitPlayer = player.bukkitPlayer
        val attribute = bukkitPlayer.getAttribute(Attribute.GENERIC_ARMOR)

        if (attribute != null) {
            attribute.baseValue -= 0.5
        }
    }
}