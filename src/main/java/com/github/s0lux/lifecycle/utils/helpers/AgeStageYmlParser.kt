package com.github.s0lux.lifecycle.utils.helpers

import com.github.s0lux.lifecycle.utils.wrappers.AgeStages
import com.github.s0lux.lifecycle.utils.wrappers.StageEffect
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Logger

fun loadAgeStagesFromYaml(file: File, logger: Logger): AgeStages {
    val config = YamlConfiguration.loadConfiguration(file)
    val stagesList = config.getList("age-stages")

    if (stagesList == null || stagesList.isEmpty()) {
        logger.warning("Could not load age stages. No age stages found in configuration.")
        return AgeStages(emptyList())
    }

    val stageConfigs: List<StageConfig> = stagesList.mapNotNull { unparsedStage ->
        if (unparsedStage !is LinkedHashMap<*, *>) return@mapNotNull null
        val stageEffects: MutableList<StageEffect> = mutableListOf()

        if (unparsedStage["effects"] is ArrayList<*>) {
            (unparsedStage["effects"] as ArrayList<*>).forEach { it ->
                val raw = it as LinkedHashMap<*, *>

                when (raw["type"]) {
                    "POTION" -> stageEffects.add(StageEffect.Potion(
                        effect = raw["effect"]?.toString(),
                        amplifier = raw["amplifier"]?.toString()?.toIntOrNull()))

                    "ATTRIBUTE" -> stageEffects.add(StageEffect.Attribute(
                        attribute = raw["attribute"]?.toString().orEmpty(),
                        value = raw["value"]?.toString()?.toDoubleOrNull()))
                }
            }
        }

        StageConfig(
            name = unparsedStage["name"].toString(),
            age = unparsedStage["age"]?.toString()?.toIntOrNull(),
            effects = stageEffects
        )
    }

    return AgeStages(stageConfigs)
}

data class StageConfig (
    val name: String,
    val age: Int? = 0,
    val effects: List<StageEffect>
)