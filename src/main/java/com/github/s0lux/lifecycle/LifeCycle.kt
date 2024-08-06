package com.github.s0lux.lifecycle

import com.github.s0lux.lifecycle.aging.AgingListener
import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.command.CommandManager
import com.github.s0lux.lifecycle.data.DataManager
import com.github.s0lux.lifecycle.notification.NotificationManager
import com.github.s0lux.lifecycle.player.PlayerListener
import com.github.s0lux.lifecycle.trait.TraitManager
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.logging.Logger

class LifeCycle : SuspendingJavaPlugin(), KoinComponent {
    private val agingManager: AgingManager by inject()
    private val dataManager: DataManager by inject()
    private val traitManager: TraitManager by inject()
    private val notificationManager: NotificationManager by inject()
    private val commandManager: CommandManager by inject()

    override suspend fun onEnableAsync() {
        startKoin {
            modules(module {
                single<Logger> { getLogger() }
                single<JavaPlugin> { this@LifeCycle }
                single<TraitManager> { TraitManager(get(), get()) }
                single<AgingManager> { AgingManager(get(), get()) }
                single<DataManager> { DataManager(get(), get(), get(), get()) }
                single<NotificationManager> { NotificationManager() }
                single<CommandManager> { CommandManager(get(), get()) }
            })
        }

        CommandAPI.onEnable()

        // Setup config
        saveDefaultConfig()
        saveResource("age_stages.yml", false)

        // Setup plugin
        dataManager.setupDatabase()
        dataManager.startBackupJob()
        agingManager.initializeAgingCycle()
        traitManager.initializeTraits()
        commandManager.registerCommands()

        // Register listeners
        server.pluginManager.registerSuspendingEvents(
            PlayerListener(
                dataManager, agingManager, traitManager
            ), this
        )

        server.pluginManager.registerEvents(
            AgingListener(
                traitManager,
                agingManager,
                notificationManager,
            ), this
        )

    }

    override suspend fun onLoadAsync() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this))
    }

    override suspend fun onDisableAsync() {
        CommandAPI.onDisable()
        dataManager.savePlayers(agingManager.players)
        agingManager.players.forEach { agingManager.clearPlayerStageEffects(it) }
    }
}
