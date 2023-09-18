package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrElse
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.inventory.meta.Damageable

object EffectAgeCrop : Effect<NoCompileData>("age_crop") {
    override val parameters = setOf(
        TriggerParameter.BLOCK
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val crop = data.block ?: data.location?.block ?: return false
        val state = crop.blockData as? Ageable ?: return false
        if (state.age == state.maximumAge) {
            return false
        }

        val age = config.getOrElse("age", 1) { getIntFromExpression(it, data) }

        val newAge = (state.age + age).coerceAtMost(state.maximumAge)
        state.age = newAge
        crop.blockData = state

        val player = data.player ?: return true

        val item = player.inventory.itemInMainHand

        val meta = item.itemMeta ?: return false

        if (meta.isUnbreakable || meta !is Damageable) {
            return true
        }

        // Edge cases
        if (item.type == Material.CARVED_PUMPKIN || item.type == Material.PLAYER_HEAD) {
            return true
        }

        if (config.getBool("damage_item")) {
            EffectDamageItem.applyDamage(item, age, player)
        }

        return true
    }
}
