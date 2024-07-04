package com.github.s0lux.lifecycle.managers

import com.github.s0lux.lifecycle.schemas.LifeCyclePlayersTable
import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.koin.core.component.KoinComponent
import java.util.*
import java.util.logging.Logger

class LifeCycleDataManager(
    private val logger: Logger,
    private val javaPlugin: JavaPlugin,
    private val lifeCycleTraitManager: LifeCycleTraitManager
) : KoinComponent {
    private val pluginFolder: String = javaPlugin.dataFolder.absolutePath
    private var database: Database = Database.connect("jdbc:sqlite:${pluginFolder}/database.db", "org.sqlite.JDBC")

    suspend fun savePlayers(players: List<LifeCyclePlayer>) {
        withContext(Dispatchers.IO) {
            players.forEach { player ->
                transaction(database) {
                    LifeCyclePlayersTable.upsert {
                        it[uuid] = player.bukkitPlayer.uniqueId.toString()
                        it[currentAge] = player.currentAge
                        it[currentTicks] = player.currentTicks
                        it[traits] = player.traits.joinToString { it.name }
                    }
                }
            }
        }
    }

    suspend fun getPlayer(uuid: String): LifeCyclePlayer {
        return withContext(Dispatchers.IO) {
            transaction(database) {
                LifeCyclePlayersTable.insertIgnore {
                    it[LifeCyclePlayersTable.uuid] = uuid
                    it[currentAge] = 0
                    it[currentTicks] = 0
                    it[traits] = ""
                }

                LifeCyclePlayersTable.selectAll().where {
                    LifeCyclePlayersTable.uuid eq uuid
                }.singleOrNull()?.let { result ->

                    val playerTraits = result[LifeCyclePlayersTable.traits]?.split(", ")?.mapNotNull { traitName ->
                        lifeCycleTraitManager.getTrait(traitName)
                    }

                    LifeCyclePlayer(
                        bukkitPlayer = Bukkit.getPlayer(UUID.fromString(result[LifeCyclePlayersTable.uuid]))!!,
                        currentAge = result[LifeCyclePlayersTable.currentAge],
                        currentTicks = result[LifeCyclePlayersTable.currentTicks],
                        traits = playerTraits ?: emptyList()
                    )
                } ?: throw IllegalStateException("Player with uuid $uuid not found in database")
            }
        }
    }

}