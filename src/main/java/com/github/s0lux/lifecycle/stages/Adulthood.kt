package com.github.s0lux.lifecycle.stages

import com.github.s0lux.lifecycle.utils.interfaces.AgeStage
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffect

object Adulthood: AgeStage {
    override val potionEffects: List<PotionEffect> = listOf()
    override val attributeModifiers: List<Pair<Attribute, Double>> = listOf()
}