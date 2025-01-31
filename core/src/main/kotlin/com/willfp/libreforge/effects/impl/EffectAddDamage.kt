package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.event.entity.EntityDamageEvent

object EffectAddDamage : Effect<NoCompileData>("add_damage") {
    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require("damage", "You must specify the damage to add!")
    }

    override val supportsDelay = false

    override val runOrder = RunOrder.LATE

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageEvent ?: return false
        event.damage += config.getDoubleFromExpression("damage", data)
        Bukkit.getConsoleSender().sendMessage("改为伤害: " + event.damage)

        return true
    }
}
