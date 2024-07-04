package com.github.s0lux.lifecycle.utils.enums

import net.kyori.adventure.text.format.NamedTextColor

enum class Rarity(val weight: Int, val color: NamedTextColor) {
    COMMON(50, NamedTextColor.GRAY),
    RARE(25, NamedTextColor.BLUE),
    EPIC(10, NamedTextColor.LIGHT_PURPLE),
    LEGENDARY(5, NamedTextColor.RED),
}