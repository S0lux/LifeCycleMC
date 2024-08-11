package com.github.s0lux.lifecycle.trait.premades

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.s0lux.lifecycle.trait.interfaces.Rarity
import com.github.s0lux.lifecycle.trait.interfaces.Trait
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object AquaticAdept : Trait {
    override val name: String = "Aquatic Adept"
    override val rarity: Rarity = Rarity.RARE
    override val description: Component =
        Component.text("The ocean calls for you. ")
            .appendNewline()
            .append(Component.text("Swim faster, hold your breath longer and see better underwater.", NamedTextColor.GREEN))
    override val isHereditary: Boolean = false

    private val playersWithTrait = mutableSetOf<LifeCyclePlayer>()
    lateinit var oxygenModifier: AttributeModifier
    private const val SWIM_SPEED_BOOST = 0
    private const val OXYGEN_BOOST = 2.0

    override fun initialize(javaPlugin: JavaPlugin) {
        oxygenModifier = AttributeModifier(
            NamespacedKey(javaPlugin, "aquatic-adept-oxygen"), OXYGEN_BOOST, AttributeModifier.Operation.ADD_NUMBER
        )

        javaPlugin.server.pluginManager.registerEvents(AquaticAdeptListener(), javaPlugin)
    }

    override fun apply(player: LifeCyclePlayer) {
        playersWithTrait.add(player)
        applyOxygenBonus(player.bukkitPlayer)
        updatePlayerEffects(player.bukkitPlayer)
    }

    override fun unApply(player: LifeCyclePlayer) {
        playersWithTrait.remove(player)
        removeOxygenBonus(player.bukkitPlayer)
        removePlayerEffects(player.bukkitPlayer)
    }

    private fun updatePlayerEffects(player: Player) {
        if (player.isInWater) {
            player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, SWIM_SPEED_BOOST, false, false))
        }
    }

    private fun removePlayerEffects(player: Player) {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION)
        player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE)
    }

    private fun applyOxygenBonus(bukkitPlayer: Player) {
        bukkitPlayer.getAttribute(Attribute.GENERIC_OXYGEN_BONUS)?.addModifier(oxygenModifier)
    }

    private fun removeOxygenBonus(bukkitPlayer: Player) {
        bukkitPlayer.getAttribute(Attribute.GENERIC_OXYGEN_BONUS)?.removeModifier(oxygenModifier)
    }

    class AquaticAdeptListener : Listener {
        @EventHandler
        fun onPlayerMove(event: PlayerMoveEvent) {
            val player = event.player
            val lifeCyclePlayer = playersWithTrait.find { it.bukkitPlayer == player } ?: return

            if (player.isInWater) {
                if (player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE)?.amplifier != SWIM_SPEED_BOOST) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, SWIM_SPEED_BOOST, false, false))
                }
                if (player.getPotionEffect(PotionEffectType.NIGHT_VISION) == null) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false))
                }
            } else {
                player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE)
                player.removePotionEffect(PotionEffectType.NIGHT_VISION)
            }
        }
    }
}