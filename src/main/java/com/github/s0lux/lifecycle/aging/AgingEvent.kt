package com.github.s0lux.lifecycle.aging

import com.github.s0lux.lifecycle.player.LifeCyclePlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AgingEvent(val player: LifeCyclePlayer, val stageInfo: AgeStageResult): Event() {
    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}