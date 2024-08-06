package com.github.s0lux.lifecycle.command

import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.trait.TraitManager
import dev.jorel.commandapi.CommandAPICommand
import org.koin.core.component.KoinComponent

class CommandManager(val agingManager: AgingManager, val traitManager: TraitManager) : KoinComponent {
    fun registerCommands() {
        val AgeCommands = CommandAPICommand("age")
            .withSubcommand(AgeCommands(agingManager).createSetAgeCommand())
            .withSubcommand(AgeCommands(agingManager).createAddAgeCommand())
            .withSubcommand(AgeCommands(agingManager).createSubtractAgeCommand())

        val TraitCommands = CommandAPICommand("trait")
            .withSubcommand(TraitCommands(agingManager, traitManager).createSetTraitCommand())

        CommandAPICommand("lifecycle")
            .withAliases("life")
            .withSubcommand(InfoCommands(agingManager).createCheckLifeSelfCommand())
            .withSubcommand(InfoCommands(agingManager).createCheckLifeOtherCommand())
            .withSubcommand(AgeCommands)
            .withSubcommand(TraitCommands)
            .register()
    }
}