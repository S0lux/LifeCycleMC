package com.github.s0lux.lifecycle.utils.helpers

import AgeStage
import AgeStageConfig
import AgeStageUnparsedEffect
import com.github.s0lux.lifecycle.utils.wrappers.AgeStages
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Logger

fun loadAgeStagesFromYaml(file: File, logger: Logger): AgeStages {
    val config = YamlConfiguration.loadConfiguration(file).getList("age-stages")
    val stages = config?.mapNotNull { stage ->
        if (stage !is Map<*, *>) return@mapNotNull null

        val effects = (stage["effects"] as? List<Map<*, *>>)?.mapNotNull { effect ->
            AgeStageUnparsedEffect(
                type = effect["type"] as? String ?: return@mapNotNull null,
                effect = effect["effect"] as? String,
                amplifier = (effect["amplifier"] as? Number)?.toInt(),
                attribute = effect["attribute"] as? String,
                value = (effect["value"] as? Number)?.toLong()
            )
        } ?: emptyList()

        val stageConfig = AgeStageConfig(
            name = stage["name"] as? String ?: return@mapNotNull null,
            age = (stage["age"] as? Number)?.toInt() ?: return@mapNotNull null,
            effects = effects
        )

        AgeStage(stageConfig)
    } ?: emptyList()

    logger.info("Loaded ${stages.size} age stages")
    return AgeStages(stages)
}