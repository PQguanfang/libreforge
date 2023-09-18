package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.BrushableBlock
import org.bukkit.block.data.Levelled
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.loot.LootContext
import org.bukkit.loot.LootTable
import org.bukkit.loot.LootTables
import java.util.*

object EffectReplaceNear : Effect<NoCompileData>("replace_near") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius!")
        require("radius_y", "You must specify the y radius!")
        require("replace_to", "You must specify the block to replace to!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val radius = config.getIntFromExpression("radius", data)
        val radiusY = config.getIntFromExpression("radius_y", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val replaceTo = Items.lookup(config.getString("replace_to")).item.type

        if (!replaceTo.isBlock) {
            return false
        }

        val whitelist = config.getStringsOrNull("whitelist")

        val duration = config.getOrNull("duration") { getIntFromExpression(it, data) }

        val exposedBlocksOnly = config.getBool("exposed_only")
        val sourceBlocksOnly = config.getBool("source_only")

        var replaceAmount = 0

        for (x in (-radius..radius)) {
            for (y in (-radiusY..radiusY)) {
                for (z in (-radius..radius)) {

                    val toReplace = block.world.getBlockAt(
                        block.location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    )

                    if (config.getStrings("blacklist").containsIgnoreCase(toReplace.type.name)) {
                        continue
                    }

                    if (whitelist != null) {
                        if (!whitelist.containsIgnoreCase(toReplace.type.name)) {
                            continue
                        }
                    }

                    if (toReplace.type == Material.AIR) {
                        continue
                    }

                    if (exposedBlocksOnly && !toReplace.getRelative(BlockFace.UP, 1).isEmpty) {
                        continue
                    }

                    if (sourceBlocksOnly && toReplace.isLiquid) {
                        val liquidData = toReplace.blockData
                        if (liquidData is Levelled) {
                            if (liquidData.level != 0) {
                                continue
                            }
                        }
                    }

                    if (!(AntigriefManager.canBreakBlock(player, toReplace) && AntigriefManager.canPlaceBlock(player, toReplace))) {
                        continue
                    }

                    if (duration != null && duration > 0) {
                        val oldBlock = toReplace.type
                        val oldBlockData = toReplace.blockData
                        toReplace.setMetadata("rn-block", plugin.createMetadataValue(true))

                        plugin.scheduler.runLater(duration.toLong()) {
                            if (toReplace.hasMetadata("rn-block")) {
                                toReplace.type = oldBlock
                                toReplace.blockData = oldBlockData
                                toReplace.removeMetadata("rn-block", plugin)
                            }
                        }
                    }

                    toReplace.type = replaceTo

                    replaceAmount++

                    val state = toReplace.state

                    if (state is BrushableBlock && Prerequisite.HAS_1_19_4.isMet) {
                        val lootList = ArrayList<LootTable>()
                        lootList.add(LootTables.DESERT_WELL_ARCHAEOLOGY.lootTable)
                        lootList.add(LootTables.DESERT_PYRAMID_ARCHAEOLOGY.lootTable)
                        lootList.add(LootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON.lootTable)
                        lootList.add(LootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE.lootTable)
                        lootList.add(LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY.lootTable)
                        lootList.add(LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY.lootTable)
                        val loot = lootList.random()
                        val random = Random()
                        val context: LootContext = LootContext.Builder(toReplace.location).build()
                        val lootItem = loot.populateLoot(random, context).random()
                        state.setItem(lootItem)
                        state.update()
                    }

                }
            }
        }

        if (config.getBool("damage_item")) {
            EffectDamageItem.applyDamage(data.player.inventory.itemInMainHand, replaceAmount, player)
        }

        return true
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block

        if (!block.hasMetadata("rn-block")) {
            return
        }

        block.removeMetadata("rn-block", plugin)
        block.type = Material.AIR
        event.isCancelled = true
    }
}
