package com.github.s0lux.lifecycle.trait.premades

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Rarity
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Chameleon : Trait {
    override val name: String = "Chameleon"
    override val rarity: Rarity = Rarity.LEGENDARY
    override val description: Component =
        Component.text("You can blend into your surroundings. ")
            .appendNewline()
            .append(Component.text("Complete invisibility when standing still.", NamedTextColor.GREEN))
    override val isHereditary: Boolean = false

    private val playersWithTrait = mutableMapOf<LifeCyclePlayer, PlayerData>()
    private const val INVISIBILITY_DELAY = 30

    data class PlayerData(
        var lastMoveTime: Long = 0L, var isInvisible: Boolean = false
    )

    override fun initialize(javaPlugin: JavaPlugin) {
        javaPlugin.server.pluginManager.registerEvents(ChameleonListener(), javaPlugin)

        javaPlugin.launch {
            while (true) {
                val currentTime = javaPlugin.server.currentTick
                playersWithTrait.forEach { (player, data) ->
                    if (currentTime - data.lastMoveTime > INVISIBILITY_DELAY) {
                        if (!data.isInvisible) {
                            applyInvisibility(player)
                        }
                    }
                }
                delay(5.ticks)
            }
        }
    }

    private fun applyInvisibility(player: LifeCyclePlayer) {
        player.bukkitPlayer.addPotionEffect(
            PotionEffect(
                PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false
            )
        )
        playersWithTrait[player]?.isInvisible = true
    }

    private fun removeInvisibility(player: LifeCyclePlayer) {
        player.bukkitPlayer.removePotionEffect(PotionEffectType.INVISIBILITY)
        playersWithTrait[player]?.isInvisible = false
    }

    override fun apply(player: LifeCyclePlayer) {
        playersWithTrait[player] = PlayerData()
    }

    override fun unApply(player: LifeCyclePlayer) {
        playersWithTrait.remove(player)
        removeInvisibility(player)
    }

    class ChameleonListener : Listener {
        @EventHandler
        fun onPlayerMove(event: PlayerMoveEvent) {
            val lifeCyclePlayer = playersWithTrait.keys.find { it.bukkitPlayer == event.player } ?: return
            playersWithTrait[lifeCyclePlayer]?.lastMoveTime = Bukkit.getCurrentTick().toLong()
            if (playersWithTrait[lifeCyclePlayer]?.isInvisible == true) {
                removeInvisibility(lifeCyclePlayer)
            }
        }
    }
}