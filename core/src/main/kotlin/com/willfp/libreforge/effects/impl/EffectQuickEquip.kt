package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object EffectQuickEquip : Effect<NoCompileData>("quick_equip") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val item = player.inventory.itemInMainHand

        var equip : EquipmentSlot = EquipmentSlot.HAND

        if (item.type.isAir) return false

        if (item.type in arrayOf(Material.LEATHER_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.GOLDEN_HELMET,
            Material.DIAMOND_HELMET,
            Material.IRON_HELMET,
            Material.NETHERITE_HELMET))
            equip = EquipmentSlot.HEAD

        if (item.type in arrayOf(Material.LEATHER_CHESTPLATE,
                Material.CHAINMAIL_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE,
                Material.DIAMOND_CHESTPLATE,
                Material.IRON_CHESTPLATE,
                Material.NETHERITE_CHESTPLATE))
            equip = EquipmentSlot.CHEST

        if (item.type in arrayOf(Material.LEATHER_LEGGINGS,
                Material.CHAINMAIL_LEGGINGS,
                Material.GOLDEN_LEGGINGS,
                Material.DIAMOND_LEGGINGS,
                Material.IRON_LEGGINGS,
                Material.NETHERITE_LEGGINGS))
            equip = EquipmentSlot.LEGS

        if (item.type in arrayOf(Material.LEATHER_BOOTS,
                Material.CHAINMAIL_BOOTS,
                Material.GOLDEN_BOOTS,
                Material.DIAMOND_BOOTS,
                Material.IRON_BOOTS,
                Material.NETHERITE_BOOTS))
            equip = EquipmentSlot.FEET

        if (item.type in arrayOf(Material.TOTEM_OF_UNDYING,
                Material.SHIELD))
            equip = EquipmentSlot.OFF_HAND

        val oldItem = player.inventory.getItem(equip)

        if (oldItem == item) return false

        player.inventory.setItemInMainHand(null)
        player.inventory.setItem(equip, item)
        if (!oldItem.type.isAir) player.inventory.addItem(oldItem)

        return true
    }

}
