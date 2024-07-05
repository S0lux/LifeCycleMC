package com.github.s0lux.lifecycle.events

import com.github.s0lux.lifecycle.utils.wrappers.LifeCyclePlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AgingEvent(val player: LifeCyclePlayer): Event() {
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