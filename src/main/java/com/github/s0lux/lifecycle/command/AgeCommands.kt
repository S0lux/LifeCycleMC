package com.github.s0lux.lifecycle.command

import com.github.s0lux.lifecycle.aging.AgingEvent
import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AgeCommands(private val agingManager: AgingManager) {
    fun createSetAgeCommand() =
        CommandAPICommand("set")
            .withPermission("lifecycle.age.set")
            .withArguments(PlayerArgument("player"), IntegerArgument("ageValue"))
            .executes(CommandExecutor { sender, args ->
                val targetPlayer = args.get("player") as Player
                val targetAgeValue = args.get("ageValue") as Int
                executeAgeChange(sender, targetPlayer, targetAgeValue) { _, newAge -> newAge }
            })

    fun createSubtractAgeCommand() =
        CommandAPICommand("subtract")
            .withPermission("lifecycle.age.subtract")
            .withArguments(PlayerArgument("player"), IntegerArgument("ageValue"))
            .executes(CommandExecutor { sender, args ->
                val targetPlayer = args.get("player") as Player
                val subtractValue = args.get("ageValue") as Int
                executeAgeChange(sender, targetPlayer, subtractValue) { currentAge, value -> currentAge - value }
            })

    fun createAddAgeCommand() =
        CommandAPICommand("add")
            .withPermission("lifecycle.age.add")
            .withArguments(PlayerArgument("player"), IntegerArgument("ageValue"))
            .executes(CommandExecutor { sender, args ->
                val targetPlayer = args.get("player") as Player
                val addValue = args.get("ageValue") as Int
                executeAgeChange(sender, targetPlayer, addValue) { currentAge, value -> currentAge + value }
            })

    private fun executeAgeChange(
        sender: CommandSender,
        targetPlayer: Player,
        ageValue: Int,
        calculateNewAge: (Int, Int) -> Int
    ) {
        val lifeCyclePlayer = agingManager.findLifeCyclePlayer(targetPlayer.uniqueId.toString())

        if (lifeCyclePlayer == null) {
            sender.sendMessage(
                Component.text("Unable to find player in age loop!").color(NamedTextColor.RED)
            )
            return
        }

        val newAge = calculateNewAge(lifeCyclePlayer.currentAge, ageValue)

        if (newAge < 0) {
            sender.sendMessage(
                Component.text("Age value cannot be negative!").color(NamedTextColor.RED)
            )
            return
        }

        setPlayerAge(lifeCyclePlayer, newAge)
        sender.sendMessage(
            Component.text("Player ${targetPlayer.name}'s age has been set to $newAge").color(NamedTextColor.GREEN)
        )
    }

    private fun setPlayerAge(player: LifeCyclePlayer, age: Int) {
        player.currentAge = age
        player.currentTicks = 0

        val agingEvent = AgingEvent(player, agingManager.ageStages.getStageForAge(player.currentAge))
        agingEvent.callEvent()
    }
}