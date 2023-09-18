package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable


object EffectDamageItem : Effect<NoCompileData>("damage_item") {
    override val isPermanent = false

    override val arguments = arguments {
        require("damage", "You must specify the amount of damage!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val victim = data.victim

        val damage = config.getIntFromExpression("damage", data)

        return applyDamage(item, damage, victim)
    }
    @JvmStatic
    fun applyDamage(item: ItemStack, damage: Int, victim: LivingEntity?) : Boolean {
        if (victim == null) return false

        val meta = item.itemMeta ?: return false

        if (meta.isUnbreakable || meta !is Damageable) {
            return false
        }

        // Edge cases
        if (item.type == Material.CARVED_PUMPKIN || item.type == Material.PLAYER_HEAD) {
            return false
        }

        if (victim is Player) {
            @Suppress("DEPRECATION")
            val event = PlayerItemDamageEvent(victim, item, damage)
            Bukkit.getPluginManager().callEvent(event)
            if (!event.isCancelled) {

                meta.damage += event.damage

                if (meta.damage >= item.type.maxDurability) {
                    meta.damage = item.type.maxDurability.toInt()

                    item.itemMeta = meta

                    Bukkit.getPluginManager().callEvent(PlayerItemBreakEvent(victim, item))
                    victim.playSound(victim.location, Sound.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1f, 1f)

                    item.type = Material.AIR
                } else {
                    item.itemMeta = meta
                }
            }
        } else {
            meta.damage += damage

            if (meta.damage >= item.type.maxDurability) {
                meta.damage = item.type.maxDurability.toInt()

                item.itemMeta = meta

                item.type = Material.AIR
            } else {
                item.itemMeta = meta
            }
        }

        return true

    }
}
