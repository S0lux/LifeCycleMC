package com.github.s0lux.lifecycle.notification

import com.github.s0lux.lifecycle.aging.AgeStageResult
import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.koin.core.component.KoinComponent

class NotificationManager() : KoinComponent {
    fun notifyTraitObtained(player: LifeCyclePlayer, trait: Trait) {
        val bukkitPlayer = player.bukkitPlayer
        val message: Component = Component.text("You have obtained the trait: ")
            .append(
                Component.text(trait.name, trait.rarity.color)
                    .decoration(TextDecoration.UNDERLINED, true)
            )
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

    fun notifyAge(player: LifeCyclePlayer, agingInfo: AgeStageResult) {
        val bukkitPlayer = player.bukkitPlayer

        val message: Component = if (player.currentAge == player.lifespan) {
            Component.text(
                "You have reached the end of your lifespan, you will not survive next year.",
                NamedTextColor.GOLD
            )
        } else if (player.currentAge > player.lifespan) {
            Component.text("Your time has come...", NamedTextColor.DARK_RED)
        } else if (agingInfo.isNewStage) {
            Component.text("You are now at the stage: ${agingInfo.stage.name}")
        } else Component.text("You are now ${player.currentAge} years old.")

        val notificationSound: Sound =
            Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1f, 1f)

        bukkitPlayer.playSound(notificationSound)
        bukkitPlayer.sendMessage(message)
    }

    fun notifyEndOfLife(player: LifeCyclePlayer) {
        val bukkitPlayer = player.bukkitPlayer
        val message: Component = Component.text("You are now dying from old age.").color(NamedTextColor.GOLD)

        bukkitPlayer.sendMessage(message)
    }
}