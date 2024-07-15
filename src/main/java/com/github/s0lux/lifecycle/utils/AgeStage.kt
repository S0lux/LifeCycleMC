import com.github.s0lux.lifecycle.utils.wrappers.AgeStageEffect
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffect


class AgeStage(config: AgeStageConfig) {
    val name: String = config.name

    val age: Int = config.age.also {
        if (it < 0) throw IllegalArgumentException("Invalid age for stage $name")
    }

    val effects: List<AgeStageEffect> = parseEffects(config.effects)

    private fun parseEffects(section: List<AgeStageUnparsedEffect>?): List<AgeStageEffect> {
        return section?.mapNotNull { effectMap ->
            when (effectMap.type) {
                "POTION" -> AgeStageEffect.EffectModifier(
                    PotionEffect(
                        Registry.POTION_EFFECT_TYPE.get(
                            NamespacedKey(
                                NamespacedKey.MINECRAFT_NAMESPACE,
                                effectMap.effect?.lowercase() as String
                            )
                        )!!,
                        PotionEffect.INFINITE_DURATION,
                        effectMap.amplifier as Int,
                        false,
                        false,
                    )
                )

                "ATTRIBUTE" -> AgeStageEffect.AttributeModifier(
                    Attribute.valueOf(effectMap.attribute!!),
                    (effectMap.value!!).toDouble()
                )

                else -> null
            }
        } ?: emptyList()
    }
}

data class AgeStageConfig (
    val name: String,
    val age: Int,
    val effects: List<AgeStageUnparsedEffect>
)

data class AgeStageUnparsedEffect (
    val type: String,
    val effect: String? = null,
    val amplifier: Int? = null,
    val attribute: String? = null,
    val value: Long? = null
)