package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*

object EffectFlight : Effect<NoCompileData>("flight") {
    override val shouldReload = false

    private val players = listMap<UUID, UUID>()

    private val schedule = HashMap<UUID, BukkitTask>()

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        if (player.gameMode != GameMode.SURVIVAL) return
        players[player.uniqueId] += identifiers.uuid
        schedule[player.uniqueId] = plugin.scheduler.runTimer(20L, 20L) {
            player.allowFlight = players[player.uniqueId].isNotEmpty()
        }

    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        if (player.gameMode != GameMode.SURVIVAL) return
        players[player.uniqueId] -= identifiers.uuid
        schedule.remove(player.uniqueId)
        player.allowFlight = players[player.uniqueId].isNotEmpty()
    }
}
