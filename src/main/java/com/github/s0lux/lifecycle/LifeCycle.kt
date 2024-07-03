package com.github.s0lux.lifecycle

import org.bukkit.plugin.java.JavaPlugin

class LifeCycle : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
