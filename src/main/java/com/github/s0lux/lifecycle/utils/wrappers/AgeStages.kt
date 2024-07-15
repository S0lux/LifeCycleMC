package com.github.s0lux.lifecycle.utils.wrappers

import AgeStage

class AgeStages(val stages: List<AgeStage>) {
    init {
        validateStages()
    }

    private fun validateStages() {
        val ages = stages.map { it.age }
        if (ages.size != ages.distinct().size) {
            throw IllegalArgumentException("Each stage must have a unique age")
        }

        val names = stages.map { it.name.lowercase() }
        if (names.size != names.distinct().size) {
            throw IllegalArgumentException("Each stage must have a unique name")
        }

        if (stages.find { it.age == 0 } == null)
            throw IllegalArgumentException("A starting stage must be defined (stage with age = 0)")
    }

    fun getStageForAge(age: Int): AgeStageResult {
        val stage = stages.filter { it.age <= age }.maxBy { it.age }
        return AgeStageResult(stage, stage.age == age)
    }
}

data class AgeStageResult(val stage: AgeStage, val isNewStage: Boolean)