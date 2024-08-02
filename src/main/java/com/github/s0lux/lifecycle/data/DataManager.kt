package com.github.s0lux.lifecycle.data

import com.github.s0lux.lifecycle.aging.AgingManager
import com.github.s0lux.lifecycle.trait.TraitManager
import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*
import java.util.logging.Logger

class DataManager(
    private val logger: Logger,
    private val javaPlugin: JavaPlugin,
    private val traitManager: TraitManager,
    private val agingManager: AgingManager
) : KoinComponent {
    private val pluginFolder: String = javaPlugin.dataFolder.absolutePath
    private var connection: Connection? = null
    private var backupJob: Job? = null
    private val backupInterval: Int = javaPlugin.config.getInt("lifecycle.backup-interval")
    private val lifespan: Int = javaPlugin.config.getInt("lifecycle.lifespan")

    fun setupDatabase() {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:${pluginFolder}/players.db")

            val statement: Statement = connection!!.createStatement()
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Players (
                    uuid VARCHAR(36) UNIQUE,
                    current_age INTEGER,
                    current_ticks INTEGER,
                    traits VARCHAR(64)
                );
            """.trimIndent())
            statement.close()
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

    suspend fun savePlayers(players: List<LifeCyclePlayer>) {
        withContext(Dispatchers.IO) {
            connection?.let { conn ->
                conn.prepareStatement("""
                    INSERT OR REPLACE INTO Players (uuid, current_age, current_ticks, traits)
                    VALUES (?, ?, ?, ?)
                """.trimIndent()).use { stmt ->
                    players.forEach { player ->
                        stmt.setString(1, player.bukkitPlayer.uniqueId.toString())
                        stmt.setInt(2, player.currentAge)
                        stmt.setInt(3, player.currentTicks)
                        stmt.setString(4, player.traits.joinToString { it.name })
                        stmt.executeUpdate()
                    }
                }
            }
        }
    }

    suspend fun getPlayer(uuid: String): LifeCyclePlayer {
        return withContext(Dispatchers.IO) {
            connection?.let { conn ->
                conn.prepareStatement("""
                    INSERT OR IGNORE INTO Players (uuid, current_age, current_ticks, traits)
                    VALUES (?, 0, 0, '')
                """.trimIndent()).use { stmt ->
                    stmt.setString(1, uuid)
                    stmt.executeUpdate()
                }

                conn.prepareStatement("SELECT * FROM Players WHERE uuid = ?").use { stmt ->
                    stmt.setString(1, uuid)
                    val resultSet = stmt.executeQuery()
                    if (resultSet.next()) {
                        val playerTraits = resultSet.getString("traits")
                            .split(", ")
                            .mapNotNull { traitName -> traitManager.getTraitFromName(traitName) }
                            .toMutableList()

                        LifeCyclePlayer(
                            bukkitPlayer = Bukkit.getPlayer(UUID.fromString(uuid))!!,
                            currentAge = resultSet.getInt("current_age"),
                            currentTicks = resultSet.getInt("current_ticks"),
                            traits = playerTraits,
                            lifespan = lifespan
                        )
                    } else {
                        throw IllegalStateException("Player with uuid $uuid not found in database")
                    }
                }
            } ?: throw IllegalStateException("Database connection is not initialized")
        }
    }
}