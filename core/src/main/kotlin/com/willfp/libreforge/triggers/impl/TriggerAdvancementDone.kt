package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent

object TriggerAdvancementDone : Trigger("advancement_done") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun handle(event: PlayerAdvancementDoneEvent) {
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                event = event,
            )
        )
    }
}