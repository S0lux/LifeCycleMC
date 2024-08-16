package com.github.s0lux.lifecycle.placeholderAPI

import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.trait.TraitManager
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin

class LifeCycleExpansion(
    private val javaPlugin: JavaPlugin,
    private val agingManager: AgingManager,
    private val traitManager: TraitManager,
): PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "lifecycle"
    }

    override fun getAuthor(): String {
        return "solux"
    }

    override fun getVersion(): String {
        return javaPlugin.pluginMeta.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        val lifeCyclePlayer = agingManager.findLifeCyclePlayer(player?.uniqueId.toString()) ?: return null

        if (params.equals("player_age", true)) {
            return lifeCyclePlayer.currentAge.toString()
        }

        val traitPattern = "player_trait_(\\d+)".toRegex()
        val matchResult = traitPattern.matchEntire(params)

        if (matchResult != null) {
            val traitIndex = matchResult.groupValues[1].toInt()
            if (traitIndex >= lifeCyclePlayer.traits.count()) {
                return "None"
            }
            else return lifeCyclePlayer.traits[traitIndex].name
        }

        return null
    }
}