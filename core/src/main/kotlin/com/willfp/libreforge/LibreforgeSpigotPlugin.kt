package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.libreforge.configs.ChainsYml
import com.willfp.libreforge.configs.lrcdb.CommandLrcdb
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.integrations.aureliumskills.AureliumSkillsIntegration
import com.willfp.libreforge.integrations.boosters.BoostersIntegration
import com.willfp.libreforge.integrations.ecoarmor.EcoArmorIntegration
import com.willfp.libreforge.integrations.ecobosses.EcoBossesIntegration
import com.willfp.libreforge.integrations.ecoenchants.EcoEnchantsIntegration
import com.willfp.libreforge.integrations.ecoitems.EcoItemsIntegration
import com.willfp.libreforge.integrations.ecojobs.EcoJobsIntegration
import com.willfp.libreforge.integrations.ecopets.EcoPetsIntegration
import com.willfp.libreforge.integrations.ecoskills.EcoSkillsIntegration
import com.willfp.libreforge.integrations.jobs.JobsIntegration
import com.willfp.libreforge.integrations.levelledmobs.LevelledMobsIntegration
import com.willfp.libreforge.integrations.mcmmo.McMMOIntegration
import com.willfp.libreforge.integrations.paper.PaperIntegration
import com.willfp.libreforge.integrations.reforges.ReforgesIntegration
import com.willfp.libreforge.integrations.scyther.ScytherIntegration
import com.willfp.libreforge.integrations.talismans.TalismansIntegration
import com.willfp.libreforge.integrations.tmmobcoins.TMMobcoinsIntegration
import com.willfp.libreforge.integrations.vault.VaultIntegration
import org.bukkit.event.Listener

internal lateinit var plugin: EcoPlugin
    private set

class LibreforgeSpigotPlugin : EcoPlugin() {
    val chainsYml = ChainsYml(this)

    init {
        plugin = this
    }

    override fun handleEnable() {
        this.logger.info("")
        this.logger.info("Hey, what's this plugin doing here? I didn't install it!")
        this.logger.info("libreforge is the effects system for plugins like EcoEnchants,")
        this.logger.info("EcoJobs, EcoItems, etc. If you're looking for config options for")
        this.logger.info("things like cooldown messages, lrcdb, and stuff like that, you'll")
        this.logger.info("find it under /plugins/libreforge")
        this.logger.info("")
        this.logger.info("Don't worry about updating libreforge, it's handled automatically!")
        this.logger.info("")

        if (Prerequisite.HAS_PAPER.isMet) {
            PaperIntegration.load()
        }
    }

    override fun handleReload() {
        for (config in chainsYml.getSubsections("chains")) {
            Effects.register(
                config.getString("id"),
                Effects.compileChain(
                    config.getSubsections("effects"),
                    NormalExecutorFactory.create(),
                    ViolationContext(this, "chains.yml")
                ) ?: continue
            )
        }
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            TriggerPlaceholderListener,
            EffectCollisionFixer
        )
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("AureliumSkills") { AureliumSkillsIntegration.load() },
            IntegrationLoader("Boosters") { BoostersIntegration.load() },
            IntegrationLoader("EcoArmor") { EcoArmorIntegration.load() },
            IntegrationLoader("EcoBosses") { EcoBossesIntegration.load() },
            IntegrationLoader("EcoEnchants") { EcoEnchantsIntegration.load() },
            IntegrationLoader("EcoItems") { EcoItemsIntegration.load() },
            IntegrationLoader("EcoJobs") { EcoJobsIntegration.load() },
            IntegrationLoader("EcoPets") { EcoPetsIntegration.load() },
            IntegrationLoader("EcoSkills") { EcoSkillsIntegration.load() },
            IntegrationLoader("Jobs") { JobsIntegration.load() },
            IntegrationLoader("LevelledMobs") { LevelledMobsIntegration.load() },
            IntegrationLoader("mcMMO") { McMMOIntegration.load() },
            IntegrationLoader("Reforges") { ReforgesIntegration.load() },
            IntegrationLoader("Scyther") { ScytherIntegration.load() },
            IntegrationLoader("Talismans") { TalismansIntegration.load() },
            IntegrationLoader("TMMobcoins") { TMMobcoinsIntegration.load() },
            IntegrationLoader("Vault") { VaultIntegration.load() }
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandLrcdb(this)
        )
    }
}