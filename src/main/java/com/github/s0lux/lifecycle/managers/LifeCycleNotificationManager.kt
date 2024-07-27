package com.github.s0lux.lifecycle.managers

import com.github.s0lux.lifecycle.utils.interfaces.Trait
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.koin.core.component.KoinComponent

class LifeCycleNotificationManager() : KoinComponent {
    fun notifyTraitObtained(player: LifeCyclePlayer, trait: Trait) {
        val bukkitPlayer = player.bukkitPlayer
        val message: Component =
            Component.text("You have obtained the trait: ")
                .append(
                    Component.text(trait.name, trait.rarity.color)
                        .decoration(TextDecoration.UNDERLINED, true))
                .hoverEvent(HoverEvent.showText(trait.description))

        bukkitPlayer.sendMessage(message)
    }

    fun notifyUnableToObtainTrait(player: LifeCyclePlayer) {
        val bukkitPlayer = player.bukkitPlayer
        val message: Component =
            Component.text("You are unable to manifest a trait.", NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, true)

        bukkitPlayer.sendMessage(message)
    }
}