package com.github.s0lux.lifecycle.command

import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.trait.TraitManager
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class TraitCommands(val agingManager: AgingManager, val traitManager: TraitManager) {
    val traitNames = traitManager.traits.map { it.name.replace(" ", "_") }
    val numberOfSlots = agingManager.ageStages.stages.count { it.traitSlot > -1 }

    fun createSetTraitCommand() =
        CommandAPICommand("set")
            .withPermission("lifecycle.trait.set")
            .withArguments(
                PlayerArgument("player"),
                StringArgument("trait").replaceSuggestions(ArgumentSuggestions.strings(traitNames)),
                IntegerArgument("slot").replaceSuggestions(
                    ArgumentSuggestions.strings(List(numberOfSlots) { it.toString() })
                )
            )
            .executes(CommandExecutor { commandSender, commandArguments ->
                val targetPlayer = commandArguments.get("player") as Player
                val lifeCyclePlayer = agingManager.findLifeCyclePlayer(targetPlayer.uniqueId.toString())
                val trait: Trait? =
                    traitManager.getTraitFromName(commandArguments.get("trait").toString().replace("_", " "))
                val slot: Int? = commandArguments.get("slot").toString().toIntOrNull()

                if (lifeCyclePlayer == null) {
                    commandSender.sendMessage(
                        Component.text("Unable to find player in age loop!").color(NamedTextColor.RED)
                    )
                    return@CommandExecutor
                }

                if (trait == null) {
                    commandSender.sendMessage(
                        Component.text("Cannot find trait: ${commandArguments.get("trait")}").color(NamedTextColor.RED)
                    )
                    return@CommandExecutor
                }

                if (slot == null || slot < 0) {
                    commandSender.sendMessage(
                        Component.text("Invalid slot number.").color(NamedTextColor.RED)
                    )
                    return@CommandExecutor
                }

                // Sorry for the spaghetti ;(

                if (lifeCyclePlayer.traits.count() <= slot) {
                    lifeCyclePlayer.traits.last().unApply(lifeCyclePlayer)
                }
                else lifeCyclePlayer.traits[slot].unApply(lifeCyclePlayer)

                traitManager.addTraitToPlayer(lifeCyclePlayer, slot, trait)
                trait.apply(lifeCyclePlayer)

                commandSender.sendMessage(
                    Component.text("Trait added to player").color(NamedTextColor.GREEN)
                )
            })
}