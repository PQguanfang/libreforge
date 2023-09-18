package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

object EffectAutoPlant : Effect<NoCompileData>("auto_plant") {

    private val players = listMap<UUID, UUID>()

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        players[player.uniqueId] += identifiers.uuid
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        players[player.uniqueId] -= identifiers.uuid
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun handle(event: BlockBreakEvent) {
        if (event.isCancelled)
            return

        val player = event.player

        if (players[player.uniqueId].isEmpty()) {
            return
        }

        val block = event.block

        applyPlant(player, block)

    }

    @JvmStatic
    fun applyPlant(player: Player, block: Block) {

        if (players[player.uniqueId].isEmpty()) {
            return
        }

        val type = block.type

        if (type in arrayOf(
                Material.GLOW_BERRIES,
                Material.SWEET_BERRY_BUSH,
                Material.CACTUS,
                Material.BAMBOO,
                Material.CHORUS_FLOWER,
                Material.SUGAR_CANE
            )
        ) {
            return
        }

        val blockData = block.blockData

        if (blockData !is Ageable) {
            return
        }

        if (blockData.age != blockData.maximumAge) {
            return
        }

        val item = ItemStack(
            when (type) {
                Material.WHEAT -> Material.WHEAT_SEEDS
                Material.POTATOES -> Material.POTATO
                Material.CARROTS -> Material.CARROT
                Material.BEETROOTS -> Material.BEETROOT_SEEDS
                else -> type
            }
        )

        val hasSeeds = player.inventory.removeItem(item).isEmpty()

        if (!hasSeeds) {
            return
        }

        blockData.age = 0

        plugin.scheduler.runLater(5L) {
            block.type = type
            block.blockData = blockData

            // Improves compatibility with other plugins.
            Bukkit.getPluginManager().callEvent(
                BlockPlaceEvent(
                    block,
                    block.state,
                    block.getRelative(BlockFace.DOWN),
                    player.inventory.itemInMainHand,
                    player,
                    true,
                    EquipmentSlot.HAND
                )
            )
        }
    }
}