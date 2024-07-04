package com.github.s0lux.lifecycle

import com.github.s0lux.lifecycle.managers.LifeCycleDataManager
import com.github.s0lux.lifecycle.managers.LifeCycleTraitManager
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.logging.Logger

class LifeCycle : JavaPlugin() {
    override fun onEnable() {
        startKoin {
            modules(
                module {
                    single<Logger> { getLogger() }
                    single<JavaPlugin> { this@LifeCycle }
                    single<LifeCycleTraitManager> { LifeCycleTraitManager(get(), get()) }
                    single<LifeCycleDataManager> { LifeCycleDataManager(get(), get(), get()) }
            })
        }

        saveDefaultConfig()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
