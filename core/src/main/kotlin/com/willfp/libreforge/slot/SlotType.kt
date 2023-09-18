package com.willfp.libreforge.slot

import com.willfp.eco.core.registry.KRegistrable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class SlotType(
    override val id: String
) : KRegistrable {
    /**
     * Add to the slot, returning if it was successful.
     */
    abstract fun addToSlot(player: Player, item: ItemStack): Boolean

    /**
     * Get the items in the slot.
     */
    abstract fun getItems(player: Player): List<ItemStack>

    /**
     * Get the slots that this slot type uses.
     */
    abstract fun getItemSlots(player: Player): List<Int>

    override fun equals(other: Any?): Boolean {
        return other is SlotType && other.id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
