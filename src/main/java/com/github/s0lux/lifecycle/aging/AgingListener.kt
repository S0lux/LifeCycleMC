package com.github.s0lux.lifecycle.aging

import com.github.s0lux.lifecycle.notification.NotificationManager
import com.github.s0lux.lifecycle.trait.TraitManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AgingListener(
    private val traitManager: TraitManager,
    private val agingManager: AgingManager,
    private val notificationManager: NotificationManager
): Listener {
    @EventHandler
    fun onPlayerAge(event: AgingEvent) {
        val player = event.player
        val agingInfo = event.stageInfo

        if (agingInfo.stage.traitSlot > -1) {
            val trait = traitManager.addRandomTraitToPlayer(player, agingInfo.stage.traitSlot)

            if (trait != null) {
                notificationManager.notifyTraitObtained(player, trait)
            }
            else notificationManager.notifyUnableToObtainTrait(player)
        }

        traitManager.activateTraits(player)
        agingManager.updatePlayerStageEffects(player)
    }
}