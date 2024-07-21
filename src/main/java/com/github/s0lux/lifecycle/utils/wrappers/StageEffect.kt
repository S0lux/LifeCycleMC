package com.github.s0lux.lifecycle.utils.wrappers

import io.papermc.paper.registry.TypedKey
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffect

sealed class StageEffect {
    class Potion(private val effect: String?, private val amplifier: Int?) : StageEffect() {
        init {
            if (effect == null) {
                throw IllegalArgumentException("Potion effect cannot be an empty string. Re-check your age stage configuration")
            }

            if (amplifier == null) {
                throw IllegalArgumentException("Unable to parse potion amplifier for an age stage effect. Re-check your age stage configuration")
            }
        }

        fun get(): PotionEffect {
            val potionType = Registry.POTION_EFFECT_TYPE.get(
                NamespacedKey(
                    NamespacedKey.MINECRAFT_NAMESPACE,
                    effect!!.lowercase()
                )
            )

            if (potionType == null) throw IllegalArgumentException("No potion effect with the name $effect exists")

            return PotionEffect(
                potionType,
                PotionEffect.INFINITE_DURATION,
                amplifier!!,
                false,
                false,
            )
        }
    }

    class Attribute(private val attribute: String?, private val value: Double?) : StageEffect() {
        init {
            if (attribute == null) {
                throw IllegalArgumentException("Attribute cannot be an empty string. Re-check your age stage configuration")
            }

            if (value == null) {
                throw IllegalArgumentException("Unable to parse attribute value for an age stage effect. Re-check your age stage configuration")
            }
        }

        data class AttributeModifier(val attribute: org.bukkit.attribute.Attribute, val value: Double)

        fun get(): AttributeModifier {
            val attributeType = org.bukkit.attribute.Attribute.entries.find { it.name == attribute }
            if (attributeType == null) throw IllegalArgumentException("No attrbiute with the name $attribute exists")
            return AttributeModifier(attributeType, value!!)
        }
    }
}