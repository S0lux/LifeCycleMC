package com.github.s0lux.lifecycle.trait.premades

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Rarity
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.plugin.java.JavaPlugin

object Cancer : Trait {
    override val name: String = "Cancer"
    override val rarity: Rarity = Rarity.COMMON
    override val description: Component =
        Component.text("You have cancer... ")
            .appendNewline()
            .append(Component.text("-15 years to your lifespan", NamedTextColor.RED))
    override val isHereditary: Boolean = false

    override fun initialize(javaPlugin: JavaPlugin) {
        // Nothing!
    }

    override fun apply(player: LifeCyclePlayer) {
        player.lifespan -= 15
    }

    override fun unApply(player: LifeCyclePlayer) {
        player.lifespan += 15
    }
}