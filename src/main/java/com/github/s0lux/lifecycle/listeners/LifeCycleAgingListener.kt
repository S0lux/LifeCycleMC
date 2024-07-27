package com.github.s0lux.lifecycle.listeners

import com.github.s0lux.lifecycle.events.AgingEvent
import com.github.s0lux.lifecycle.managers.LifeCycleNotificationManager
import com.github.s0lux.lifecycle.managers.LifeCycleTraitManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class LifeCycleAgingListener(
    private val lifeCycleTraitManager: LifeCycleTraitManager,
    private val lifeCycleNotificationManager: LifeCycleNotificationManager
): Listener {
    @EventHandler
    fun onPlayerAge(event: AgingEvent) {
        val player = event.player
        val agingInfo = event.stageInfo

        if (agingInfo.stage.traitSlot > -1) {
            val trait = lifeCycleTraitManager.addRandomTraitToPlayer(player, agingInfo.stage.traitSlot)

            if (trait != null) {
                lifeCycleNotificationManager.notifyTraitObtained(player, trait)
            }
            else lifeCycleNotificationManager.notifyUnableToObtainTrait(player)
        }
    }
}