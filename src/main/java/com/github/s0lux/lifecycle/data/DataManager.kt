package com.github.s0lux.lifecycle.data

import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.trait.LifeCycleTraitManager
import com.github.s0lux.lifecycle.player.BukkitPlayerWrapper
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import java.util.*
import java.util.logging.Logger

class DataManager(
    private val logger: Logger,
    private val javaPlugin: JavaPlugin,
    private val lifeCycleTraitManager: LifeCycleTraitManager,
    private val agingManager: AgingManager
) : KoinComponent {
    private val pluginFolder: String = javaPlugin.dataFolder.absolutePath
    private var database: Database = Database.connect("jdbc:sqlite:${pluginFolder}/database.db", "org.sqlite.JDBC")
    private var backupJob: Job? = null
    private val backupInterval: Int = javaPlugin.config.getInt("lifecycle.backup-interval")
    private val lifespan: Int = javaPlugin.config.getInt("lifecycle.lifespan")

    suspend fun setupDatabase() {
        withContext(Dispatchers.IO) {
            transaction(database) {
                SchemaUtils.createMissingTablesAndColumns(PlayerSchema)
            }
        }
    }

    suspend fun startBackupJob() {
        if (backupJob?.isActive == true) {
            logger.info("Backup job is already running")
            return
        }

        backupJob = javaPlugin.launch(Dispatchers.IO) {
            delay(backupInterval.ticks)

            while (isActive) {
                savePlayers(agingManager.players)
                delay(backupInterval.ticks)
            }
        }
    }

    suspend fun savePlayers(players: List<BukkitPlayerWrapper>) {
        withContext(Dispatchers.IO) {
            players.forEach { player ->
                transaction(database) {
                    PlayerSchema.upsert {
                        it[uuid] = player.bukkitPlayer.uniqueId.toString()
                        it[currentAge] = player.currentAge
                        it[currentTicks] = player.currentTicks
                        it[traits] = player.traits.joinToString { it.name }
                    }
                }
            }
        }
    }

    suspend fun getPlayer(uuid: String): BukkitPlayerWrapper {
        return withContext(Dispatchers.IO) {
            transaction(database) {
                PlayerSchema.insertIgnore {
                    it[PlayerSchema.uuid] = uuid
                    it[currentAge] = 0
                    it[currentTicks] = 0
                    it[traits] = ""
                }

                PlayerSchema.selectAll().where {
                    PlayerSchema.uuid eq uuid
                }.singleOrNull()?.let { result ->

                    val playerTraits = result[PlayerSchema.traits]?.split(", ")?.mapNotNull { traitName ->
                        lifeCycleTraitManager.getTraitFromName(traitName)
                    }?.toMutableList()

                    BukkitPlayerWrapper(
                        bukkitPlayer = Bukkit.getPlayer(UUID.fromString(result[PlayerSchema.uuid]))!!,
                        currentAge = result[PlayerSchema.currentAge],
                        currentTicks = result[PlayerSchema.currentTicks],
                        traits = playerTraits ?: mutableListOf(),
                        lifespan = lifespan
                    )
                } ?: throw IllegalStateException("Player with uuid $uuid not found in database")
            }
        }
    }

}