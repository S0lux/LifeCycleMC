package com.github.s0lux.lifecycle.utils.wrappers

import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffect

sealed class AgeStageEffect {
    data class EffectModifier(
        val effect: PotionEffect
    ) : AgeStageEffect()

    data class AttributeModifier(
        val attribute: Attribute,
        val value: Double
    ) : AgeStageEffect()
}