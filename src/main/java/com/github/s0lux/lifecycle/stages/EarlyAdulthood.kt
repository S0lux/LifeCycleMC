package com.github.s0lux.lifecycle.stages

import com.github.s0lux.lifecycle.utils.interfaces.AgeStage
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object EarlyAdulthood: AgeStage {
    override val potionEffects: List<PotionEffect> = listOf()
    override val attributeModifiers: List<Pair<Attribute, Double>> = listOf(
        Pair(Attribute.GENERIC_MAX_HEALTH, -4.0)
    )
}