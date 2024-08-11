package com.github.s0lux.lifecycle.command

import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

class InfoCommands(val agingManager: AgingManager) {

    fun createCheckLifeSelfCommand() =
        CommandAPICommand("check").executesPlayer(PlayerCommandExecutor { player, _ ->
            val lifeCyclePlayer = agingManager.findLifeCyclePlayer(player.uniqueId.toString())
            player.sendMessage(createLifeInfoComponent(lifeCyclePlayer))
        })

    fun createCheckLifeOtherCommand() =
        CommandAPICommand("check")
            .withArguments(PlayerArgument("player").withPermission("lifecycle.check.others"))
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val targetPlayer = args.get("player") as Player
                val lifeCyclePlayer = agingManager.findLifeCyclePlayer(targetPlayer.uniqueId.toString())
                player.sendMessage(createLifeInfoComponent(lifeCyclePlayer, isOtherPlayer = true))
            })

    private fun createLifeInfoComponent(lifeCyclePlayer: LifeCyclePlayer?, isOtherPlayer: Boolean = false): Component {
        val headerText =
            if (isOtherPlayer) "${lifeCyclePlayer?.bukkitPlayer?.name}'s current life:" else "Current life:"
        return Component.text().content(headerText).appendNewline().appendSpace()
            .append(Component.text("Age: ${lifeCyclePlayer?.currentAge ?: 0}")).appendNewline().appendSpace()
            .append(Component.text("Traits: ")).append(createTraitsComponent(lifeCyclePlayer?.traits)).build()
    }

    // Move this to a util class later
    private fun createTraitsComponent(traits: List<Trait>?): TextComponent.Builder {
        if (traits.isNullOrEmpty()) return Component.text().content("None")
        val traitDisplay = Component.text()

        traits.forEachIndexed { index, trait ->
            traitDisplay
                .append(
                    Component.text()
                    .append(
                        Component
                            .text(trait.name)
                            .color(trait.rarity.color)
                            .decoration(TextDecoration.UNDERLINED, true)
                            .hoverEvent(HoverEvent.showText(createTraitHoverComponent(trait))))
                )

            if (index < traits.size - 1) {
                traitDisplay.append(Component.text(", "))
            }
        }

        return traitDisplay
    }

    private fun createTraitHoverComponent(trait: Trait): Component {
        return Component
            .text(trait.name + " (${trait.rarity.name})")
            .color(trait.rarity.color)
            .decoration(TextDecoration.BOLD, true)
            .appendNewline()
            .append(trait.description.decoration(TextDecoration.BOLD, false))
    }
}