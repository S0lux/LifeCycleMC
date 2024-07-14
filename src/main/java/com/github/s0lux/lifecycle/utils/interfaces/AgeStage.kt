package com.github.s0lux.lifecycle.utils.interfaces

import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffect

interface AgeStage {
    val potionEffects: List<PotionEffect>
    val attributeModifiers: List<Pair<Attribute, Double>>
}