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
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.plugin.java.JavaPlugin

object GreenThumb : Trait {
    override val name: String = "Green Thumb"
    override val rarity: Rarity = Rarity.RARE
    override val description: Component =
        Component.text("You like green. ")
            .appendNewline()
            .append(Component.text("Crops within 5-block radius of you grow faster.", NamedTextColor.GREEN))
    override val isHereditary: Boolean = true

    private val playersWithTrait = mutableSetOf<LifeCyclePlayer>()
    private const val GROWTH_RATE_MULTIPLIER = 2
    private const val SEARCH_RADIUS = 5 // blocks

    override fun initialize(javaPlugin: JavaPlugin) {
        javaPlugin.launch {
            while (true) {
                playersWithTrait.forEach { player ->
                    val playerLocation = player.bukkitPlayer.location
                    for (x in -SEARCH_RADIUS..SEARCH_RADIUS) {
                        for (y in -SEARCH_RADIUS..SEARCH_RADIUS) {
                            for (z in -SEARCH_RADIUS..SEARCH_RADIUS) {
                                val block: Block = player.bukkitPlayer.world.getBlockAt(
                                    playerLocation.x.toInt() + x,
                                    playerLocation.y.toInt() + y,
                                    playerLocation.z.toInt() + z
                                )

                                if (block.blockData is Ageable) {
                                    val data = block.blockData as Ageable
                                    if (data.age < data.maximumAge) {
                                        data.age = (data.age + GROWTH_RATE_MULTIPLIER).coerceAtMost(data.maximumAge)
                                        block.blockData = data
                                    }
                                }
                            }
                        }
                    }
                }
                delay(400.ticks)
            }
        }
    }

    override fun apply(player: LifeCyclePlayer) {
        playersWithTrait.add(player)
    }

    override fun unApply(player: LifeCyclePlayer) {
        playersWithTrait.remove(player)
    }
}