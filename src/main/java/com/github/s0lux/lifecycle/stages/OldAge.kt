package com.github.s0lux.lifecycle.stages

import com.github.s0lux.lifecycle.utils.interfaces.AgeStage
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object OldAge: AgeStage {
    override val potionEffects: List<PotionEffect> = listOf(
        PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0, false, false)
    )
    override val attributeModifiers: List<Pair<Attribute, Double>> = listOf()
}