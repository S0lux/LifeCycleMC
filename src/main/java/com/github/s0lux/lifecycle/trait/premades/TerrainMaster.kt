package com.github.s0lux.lifecycle.trait.premades

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Rarity
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

object TerrainMaster : Trait {
    override val name: String = "Terrain Master"
    override val rarity: Rarity = Rarity.EPIC
    override val description: Component =
        Component.text("Nothing can pose an obstacle to you (terrain wise). ")
            .appendNewline()
            .append(Component.text("You can climb walls (", NamedTextColor.GREEN))
            .append(Component.keybind("key.jump")
                .append(Component.text(" + ", NamedTextColor.GREEN))
                .append(Component.keybind("key.sneak"))
                .append(Component.text(") and take reduced fall damage!", NamedTextColor.GREEN)))

    override val isHereditary: Boolean = false

    private val playersWithTrait = mutableSetOf<LifeCyclePlayer>()
    private const val CLIMB_SPEED = 0.2
    private const val FALL_DAMAGE_REDUCTION = 0.5

    override fun initialize(javaPlugin: JavaPlugin) {
        javaPlugin.server.pluginManager.registerEvents(TerrainMasterListener(), javaPlugin)
    }

    override fun apply(player: LifeCyclePlayer) {
        playersWithTrait.add(player)
    }

    override fun unApply(player: LifeCyclePlayer) {
        playersWithTrait.remove(player)
    }

    private fun canClimbWall(player: Player): Boolean {
        val block = player.location.block
        return block.getRelative(BlockFace.NORTH).type.isSolid ||
                block.getRelative(BlockFace.SOUTH).type.isSolid ||
                block.getRelative(BlockFace.EAST).type.isSolid ||
                block.getRelative(BlockFace.WEST).type.isSolid
    }

    class TerrainMasterListener : Listener {
        @EventHandler
        fun onPlayerMove(event: PlayerMoveEvent) {
            val player = event.player
            val lifeCyclePlayer = playersWithTrait.find { it.bukkitPlayer == player } ?: return

            if (canClimbWall(player) && !player.isOnGround && player.isSneaking) {
                player.velocity = Vector(0.0, CLIMB_SPEED, 0.0)
                player.fallDistance = 0f
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 20, 0, false, false))
            }
        }

        @EventHandler
        fun onEntityDamage(event: EntityDamageEvent) {
            if (event.entity !is Player) return
            val player = event.entity as Player
            val lifeCyclePlayer = playersWithTrait.find { it.bukkitPlayer == player } ?: return

            if (event.cause == EntityDamageEvent.DamageCause.FALL) {
                event.damage *= (1 - FALL_DAMAGE_REDUCTION)
            }
        }
    }
}