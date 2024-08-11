package com.github.s0lux.lifecycle.trait.premades

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Rarity
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.plugin.java.JavaPlugin

object ThickSkin : Trait {
    override val name: String = "Thick Skin"
    override val rarity: Rarity = Rarity.COMMON
    override val description: Component =
        Component.text("Your skin is thicker than normal people. ")
            .appendNewline()
            .append(Component.text("+2 hearts", NamedTextColor.RED))
    override val isHereditary: Boolean = false

    lateinit var healthBonus: AttributeModifier
    private const val HEALTH_BONUS_AMOUNT = 4.0

    override fun initialize(javaPlugin: JavaPlugin) {
        healthBonus = AttributeModifier(
            NamespacedKey(javaPlugin, "thick-skin-health"), HEALTH_BONUS_AMOUNT, AttributeModifier.Operation.ADD_NUMBER
        )
    }

    override fun apply(player: LifeCyclePlayer) {
        val bukkitPlayer = player.bukkitPlayer
        bukkitPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.addModifier(healthBonus)
    }

    override fun unApply(player: LifeCyclePlayer) {
        val bukkitPlayer = player.bukkitPlayer
        bukkitPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.removeModifier(healthBonus)
    }
}