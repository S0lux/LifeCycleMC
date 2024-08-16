package com.github.s0lux.lifecycle.command

import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.trait.TraitManager
import dev.jorel.commandapi.CommandAPICommand
import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent

class CommandManager(val agingManager: AgingManager, val traitManager: TraitManager) : KoinComponent {
    private fun hasAgeCommandPermission(sender: CommandSender): Boolean {
        return sender.hasPermission("lifecycle.age.set") ||
                sender.hasPermission("lifecycle.age.add") ||
                sender.hasPermission("lifecycle.age.subtract")
    }

    private fun hasTraitCommandPermission(sender: CommandSender): Boolean {
        return sender.hasPermission("lifecycle.trait.set") ||
                sender.hasPermission("lifecycle.trait.list")
    }

    private fun hasInfoCommandPermission(sender: CommandSender): Boolean {
        return sender.hasPermission("lifecycle.check.self") ||
                sender.hasPermission("lifecycle.check.other")
    }

    fun registerCommands() {
        val ageCommands = CommandAPICommand("age")
            .withRequirement { sender -> hasAgeCommandPermission(sender) }
            .withSubcommand(AgeCommands(agingManager).createSetAgeCommand())
            .withSubcommand(AgeCommands(agingManager).createAddAgeCommand())
            .withSubcommand(AgeCommands(agingManager).createSubtractAgeCommand())

        val traitCommands = CommandAPICommand("trait")
            .withRequirement { sender -> hasTraitCommandPermission(sender) }
            .withSubcommand(TraitCommands(agingManager, traitManager).createSetTraitCommand())
            .withSubcommand(TraitCommands(agingManager, traitManager).createListTraitCommand())

        CommandAPICommand("lifecycle")
            .withAliases("life")
            .withRequirement { sender ->
                hasInfoCommandPermission(sender) ||
                        hasTraitCommandPermission(sender) ||
                        hasAgeCommandPermission(sender)
            }
            .withSubcommand(InfoCommands(agingManager).createCheckLifeSelfCommand())
            .withSubcommand(InfoCommands(agingManager).createCheckLifeOtherCommand())
            .withSubcommand(ageCommands)
            .withSubcommand(traitCommands)
            .register()
    }
}