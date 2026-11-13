package PJ.campaign

import PJ.utils.PJPeople
import PJ.utils.PJPeople.PROJET
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.impl.campaign.ids.Skills
import com.fs.starfarer.api.util.IntervalUtil

class ProjetScript : EveryFrameScript {

    private val interval = IntervalUtil(0.2f, 0.3f)

    // Ships Projet commanded last frame, and the player's core item count last frame (for the right-click drop).
    private val shipsWithProjet = HashSet<String>()
    private var lastCoreCount = 0f

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun advance(amount: Float) {
        val sector = Global.getSector()
        val projet = PJPeople.getPerson(PROJET) ?: return
        val recruited = sector.memoryWithoutUpdate.getBoolean("\$PJ_ProjetRecruited")
        val gaPZ_missionCompleted = sector.memoryWithoutUpdate.getBoolean("\$gaPZ_missionCompleted")
        val gaATG_missionCompleted = sector.memoryWithoutUpdate.getBoolean("\$gaATG_missionCompleted")
        val gaDHO_missionComplete = sector.memoryWithoutUpdate.getBoolean("\$gaDHO_missionComplete")

        // Runs every frame (not on the interval) so a right-click "remove officer" is undone immediately.
        enforceBuiltInCore(projet, recruited)

        interval.advance(amount)
        if (!interval.intervalElapsed()) return

        if (recruited) {
            val faction = sector.playerFaction
            if (!faction.knowsHullMod(HULLMOD_ID)) faction.addKnownHullMod(HULLMOD_ID)
        }

        if (recruited && gaPZ_missionCompleted && !projet.memoryWithoutUpdate.getBoolean(UPGRADED_KEY1)) {
            upgradeProjet1(projet)
            projet.memoryWithoutUpdate.set(UPGRADED_KEY1, true)
        }

        if (recruited && gaATG_missionCompleted && !projet.memoryWithoutUpdate.getBoolean(UPGRADED_KEY2)) {
            upgradeProjet2(projet)
            projet.memoryWithoutUpdate.set(UPGRADED_KEY2, true)
        }

        if (recruited && gaDHO_missionComplete && !projet.memoryWithoutUpdate.getBoolean(UPGRADED_KEY3)) {
            upgradeProjet3(projet)
            projet.memoryWithoutUpdate.set(UPGRADED_KEY3, true)
        }
    }

    private fun enforceBuiltInCore(projet: PersonAPI, recruited: Boolean) {
        val upgraded1 = projet.memoryWithoutUpdate.getBoolean(UPGRADED_KEY1)
        val upgraded2 = projet.memoryWithoutUpdate.getBoolean(UPGRADED_KEY2)
        val upgraded3 = projet.memoryWithoutUpdate.getBoolean(UPGRADED_KEY3)
        val cargo = Global.getSector().playerFleet.cargo
        var reassignedFromRemoval = false
        val nowWithProjet = HashSet<String>()

        Global.getSector().playerFleet.fleetData.membersListCopy.forEach { member ->
            val hasLink = member.variant.hasHullMod(HULLMOD_ID)
            val captain = member.captain

            if (recruited && hasLink && (captain == null || captain.isDefault)) {
                if (member.id in shipsWithProjet) reassignedFromRemoval = true
                member.captain = projet
                if (upgraded1 || upgraded2 || upgraded3) member.updateStats()
                nowWithProjet.add(member.id)
            } else if (captain == projet && !hasLink) {
                member.captain = null
            } else if (captain == projet) {
                nowWithProjet.add(member.id)
            }
        }

        if (projet.aiCoreId=="gamma_core") {
            val coreCount = cargo.getCommodityQuantity("gamma_core")
            if (reassignedFromRemoval && coreCount > lastCoreCount) {
                cargo.removeCommodity("gamma_core", coreCount - lastCoreCount)
            }
            lastCoreCount = cargo.getCommodityQuantity("gamma_core")

            shipsWithProjet.clear()
            shipsWithProjet.addAll(nowWithProjet)
        }

        if (projet.aiCoreId=="beta_core") {
            val coreCount = cargo.getCommodityQuantity("beta_core")
            if (reassignedFromRemoval && coreCount > lastCoreCount) {
                cargo.removeCommodity("beta_core", coreCount - lastCoreCount)
            }
            lastCoreCount = cargo.getCommodityQuantity("beta_core")

            shipsWithProjet.clear()
            shipsWithProjet.addAll(nowWithProjet)
        }

        if (projet.aiCoreId=="alpha_core") {
            val coreCount = cargo.getCommodityQuantity("alpha_core")
            if (reassignedFromRemoval && coreCount > lastCoreCount) {
                cargo.removeCommodity("alpha_core", coreCount - lastCoreCount)
            }
            lastCoreCount = cargo.getCommodityQuantity("alpha_core")

            shipsWithProjet.clear()
            shipsWithProjet.addAll(nowWithProjet)
        }

        if (projet.aiCoreId=="omega_core") {
            val coreCount = cargo.getCommodityQuantity("omega_core")
            if (reassignedFromRemoval && coreCount > lastCoreCount) {
                cargo.removeCommodity("omega_core", coreCount - lastCoreCount)
            }
            lastCoreCount = cargo.getCommodityQuantity("omega_core")

            shipsWithProjet.clear()
            shipsWithProjet.addAll(nowWithProjet)
        }
    }

    private fun upgradeProjet1(projet: PersonAPI) {
        projet.stats.level = 5

        projet.portraitSprite = Global.getSettings().getSpriteName("portraits", "pj_projet1")

        projet.stats.setSkillLevel(Skills.HELMSMANSHIP, 2f)
        projet.stats.setSkillLevel(Skills.TARGET_ANALYSIS, 2f)
        projet.stats.setSkillLevel(Skills.IMPACT_MITIGATION, 2f)
        projet.stats.setSkillLevel(Skills.GUNNERY_IMPLANTS, 2f)
        projet.stats.setSkillLevel(Skills.COMBAT_ENDURANCE, 2f)
        projet.setAICoreId("beta_core")
    }

    private fun upgradeProjet2(projet: PersonAPI) {
        projet.stats.level = 7

        projet.portraitSprite = Global.getSettings().getSpriteName("portraits", "pj_projet2")

        projet.stats.setSkillLevel(Skills.HELMSMANSHIP, 2f)
        projet.stats.setSkillLevel(Skills.TARGET_ANALYSIS, 2f)
        projet.stats.setSkillLevel(Skills.IMPACT_MITIGATION, 2f)
        projet.stats.setSkillLevel(Skills.FIELD_MODULATION, 2f)
        projet.stats.setSkillLevel(Skills.GUNNERY_IMPLANTS, 2f)
        projet.stats.setSkillLevel(Skills.COMBAT_ENDURANCE, 2f)
        projet.stats.setSkillLevel(Skills.DAMAGE_CONTROL, 2f)
        projet.setAICoreId("alpha_core")
    }

    private fun upgradeProjet3(projet: PersonAPI) {
        projet.stats.level = 9

        projet.portraitSprite = Global.getSettings().getSpriteName("portraits", "pj_projet3")

        projet.stats.setSkillLevel(Skills.HELMSMANSHIP, 2f)
        projet.stats.setSkillLevel(Skills.TARGET_ANALYSIS, 2f)
        projet.stats.setSkillLevel(Skills.IMPACT_MITIGATION, 2f)
        projet.stats.setSkillLevel(Skills.FIELD_MODULATION, 2f)
        projet.stats.setSkillLevel(Skills.GUNNERY_IMPLANTS, 2f)
        projet.stats.setSkillLevel(Skills.COMBAT_ENDURANCE, 2f)
        projet.stats.setSkillLevel(Skills.DAMAGE_CONTROL, 2f)
        projet.stats.setSkillLevel(Skills.POINT_DEFENSE, 2f)
        projet.stats.setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 2f)
        projet.setAICoreId("omega_core")
    }


    companion object {
        const val HULLMOD_ID = "PJ_projetLink"
        private const val UPGRADED_KEY1 = "\$PJ_ProjetThreatUpgraded1"
        private const val UPGRADED_KEY2 = "\$PJ_ProjetThreatUpgraded2"
        private const val UPGRADED_KEY3 = "\$PJ_ProjetThreatUpgraded3"

        fun register() {
            if (!Global.getSector().hasTransientScript(ProjetScript::class.java)) {
                Global.getSector().addTransientScript(ProjetScript())
            }
        }
    }
}