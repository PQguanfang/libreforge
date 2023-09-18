package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.SmithItemEvent

object TriggerSmith : Trigger("smith") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: SmithItemEvent) {

        if (event.inventory.result == null) return

        val player = event.whoClicked as? Player ?: return
        val item = event.inventory.result

        this.dispatch(
            player,
            TriggerData(
                player = player,
                item = item
            )
        )
    }
}
