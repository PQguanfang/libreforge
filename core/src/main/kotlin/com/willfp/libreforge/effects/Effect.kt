package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.defaultMap
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.applyHolder
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.mutators.emptyMutatorList
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.UUID

abstract class Effect<T>(
    final override val id: String
) : Compilable<T>(), Listener {
    // Maps Player UUIDs to the effect count.
    private val effectCounter = defaultMap<UUID, Int>(0)

    // The identifier factory.
    private val identifierFactory = IdentifierFactory(UUID.nameUUIDFromBytes(id.toByteArray()))

    /**
     * If the effect should be reloaded.
     */
    open val shouldReload = true

    /**
     * The run order.
     */
    open val runOrder = RunOrder.NORMAL

    /**
     * If the effect can be delayed.
     */
    open val supportsDelay = true

    /**
     * The required trigger parameters.
     */
    protected open val parameters: Set<TriggerParameter> = emptySet()

    /**
     * If the effect is permanent.
     */
    open val isPermanent: Boolean
        get() = parameters.isEmpty()

    /**
     * If the effect supports a certain [trigger].
     */
    fun supportsTrigger(trigger: Trigger) =
        Triggers.withParameters(parameters)(trigger, emptyMutatorList())

    /**
     * If the effect supports a certain [trigger] after [mutators] are applied to it.
     */
    fun supportsTrigger(trigger: Trigger, mutators: MutatorList) =
        Triggers.withParameters(parameters)(trigger, mutators)

    /**
     * Enable a permanent effect.
     *
     * @param player The player.
     * @param config The effect config.
     */
    fun enable(
        player: Player,
        holder: ProvidedHolder,
        config: ChainElement<T>,
        isReload: Boolean = false
    ) {
        if (isReload && !shouldReload) {
            return
        }

        // Increment first to fix reload bug where effects are applied twice.
        effectCounter[player.uniqueId]++
        val count = effectCounter[player.uniqueId]

        val withHolder = config.config.applyHolder(holder, player)

        onEnable(player, withHolder, identifierFactory.makeIdentifiers(count), holder, config.compileData)
    }

    /**
     * Handle the enabling of this permanent effect.
     *
     * @param player The player.
     * @param config The config.
     * @param identifiers The identifiers.
     * @param compileData The compile data.
     */
    protected open fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: T
    ) {
        // Override when needed.
    }

    /**
     * Disable a permanent effect.
     *
     * @param player The player.
     */
    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) {
        if (isReload && !shouldReload) {
            return
        }

        if (effectCounter[player.uniqueId] == 0) {
            return
        }

        val count = effectCounter[player.uniqueId]--
        onDisable(player, identifierFactory.makeIdentifiers(count), holder)
    }

    /**
     * Handle the disabling of this permanent effect.
     *
     * @param player The player.
     * @param identifiers The identifiers.
     */
    protected open fun onDisable(
        player: Player,
        identifiers: Identifiers,
        holder: ProvidedHolder
    ) {
        // Override when needed.
    }

    /**
     * Trigger the effect.
     *
     * Returns if the execution was successful.
     *
     * @param trigger The trigger.
     * @param config The config.
     */
    fun trigger(
        trigger: DispatchedTrigger,
        config: ChainElement<T>
    ): Boolean = onTrigger(config.config, trigger.data, config.compileData)

    /**
     * Handle triggering.
     *
     * Returns if the execution was successful.
     *
     * @param data The trigger data.
     * @param compileData The compile data.
     */
    protected open fun onTrigger(
        config: Config,
        data: TriggerData,
        compileData: T
    ): Boolean {
        return false
    }

    /**
     * If the effect should trigger.
     */
    fun shouldTrigger(
        trigger: DispatchedTrigger,
        config: ChainElement<T>
    ): Boolean = shouldTrigger(config.config, trigger.data, config.compileData)

    /**
     * If the effect should trigger, ran before effect arguments in order
     * to prevent unnecessary calculations.
     */
    protected open fun shouldTrigger(
        config: Config,
        data: TriggerData,
        compileData: T
    ): Boolean {
        return true
    }

    final override fun onRegister() {
        plugin.runWhenEnabled {
            plugin.eventManager.unregisterListener(this)
            plugin.eventManager.registerListener(this)
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }
}
