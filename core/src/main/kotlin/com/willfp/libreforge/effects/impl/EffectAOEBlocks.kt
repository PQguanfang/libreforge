package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.impl.aoe.AOECompileData
import com.willfp.libreforge.effects.impl.aoe.AOEShapes
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectAOEBlocks : Effect<AOECompileData>("aoe_blocks") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("shape", "You must specify a valid shape!", Config::getString) {
            AOEShapes[it] != null
        }
        inherit { AOEShapes[it.getString("shape")] }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: AOECompileData): Boolean {
        val player = data.player ?: return false
        val shape = compileData.shape ?: return false

        for (block in shape.getBlocks(
            player.location.toFloat3(),
            player.eyeLocation.direction.toFloat3(),
            player.location.world,
            data
        ).filterNot { it.isEmpty }) {
            compileData.chain
                ?.trigger(
                    data.copy(
                        block = block,
                        location = block.location.add(0.5, 0.5, 0.5)
                    ).dispatch(player)
                )
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): AOECompileData {
        return AOECompileData(
            AOEShapes.compile(config, context),
            Effects.compileRichChain(
                config,
                context.with("aoe effects")
            )
        )
    }
}
