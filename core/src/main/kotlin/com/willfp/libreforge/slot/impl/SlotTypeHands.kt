package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeHands : SlotType("hands") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!SlotTypeMainhand.addToSlot(player, item)) {
            return SlotTypeOffhand.addToSlot(player, item)
        }

        return true
    }

    override fun getItems(player: Player): List<ItemStack> {
        return SlotTypeOffhand.getItems(player) + SlotTypeMainhand.getItems(player)
    }

    override fun getItemSlots(player: Player): List<Int> {
        return SlotTypeOffhand.getItemSlots(player) + SlotTypeMainhand.getItemSlots(player)
    }
}
