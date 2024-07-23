package com.github.s0lux.lifecycle

import com.github.s0lux.lifecycle.listeners.LifeCyclePlayerListener
import com.github.s0lux.lifecycle.managers.LifeCycleAgeManager
import com.github.s0lux.lifecycle.managers.LifeCycleDataManager
import com.github.s0lux.lifecycle.managers.LifeCycleTraitManager
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.logging.Logger

class LifeCycle : SuspendingJavaPlugin(), KoinComponent {
    private val lifeCycleAgeManager: LifeCycleAgeManager by inject()
    private val lifeCycleDataManager: LifeCycleDataManager by inject()

    override suspend fun onEnableAsync() {
        startKoin {
            modules(
                module {
                    single<Logger> { getLogger() }
                    single<JavaPlugin> { this@LifeCycle }
                    single<LifeCycleTraitManager> { LifeCycleTraitManager(get(), get()) }
                    single<LifeCycleAgeManager> { LifeCycleAgeManager(get(), get()) }
                    single<LifeCycleDataManager> { LifeCycleDataManager(get(), get(), get(), get()) }
                })
        }

        // Setup config
        saveDefaultConfig()
        saveResource("age_stages.yml", false)

        // Setup plugin
        lifeCycleDataManager.setupDatabase()
        lifeCycleDataManager.startBackupJob()
        lifeCycleAgeManager.initializeAgingCycle()

        // Register listeners
        server.pluginManager.registerSuspendingEvents(
            LifeCyclePlayerListener(
                lifeCycleDataManager,
                lifeCycleAgeManager
            ), this
        )
    }

    override suspend fun onDisableAsync() {
        lifeCycleDataManager.savePlayers(lifeCycleAgeManager.players)
        lifeCycleAgeManager.players.forEach { lifeCycleAgeManager.clearStageEffects(it) }
    }
}
