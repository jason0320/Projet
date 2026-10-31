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

        // Runs every frame (not on the interval) so a right-click "remove officer" is undone immediately.
        enforceBuiltInCore(projet, recruited)

        interval.advance(amount)
        if (!interval.intervalElapsed()) return

        if (recruited) {
            val faction = sector.playerFaction
            if (!faction.knowsHullMod(HULLMOD_ID)) faction.addKnownHullMod(HULLMOD_ID)
        }

        val encounteredThreat = sector.playerMemoryWithoutUpdate.getBoolean("\$encounteredThreat")

        if (recruited && encounteredThreat && !projet.memoryWithoutUpdate.getBoolean(UPGRADED_KEY)) {
            upgradeProjet(projet)
            projet.memoryWithoutUpdate.set(UPGRADED_KEY, true)
        }
    }

    private fun enforceBuiltInCore(projet: PersonAPI, recruited: Boolean) {
        val upgraded = projet.memoryWithoutUpdate.getBoolean(UPGRADED_KEY)
        val cargo = Global.getSector().playerFleet.cargo
        var reassignedFromRemoval = false
        val nowWithProjet = HashSet<String>()

        Global.getSector().playerFleet.fleetData.membersListCopy.forEach { member ->
            val hasLink = member.variant.hasHullMod(HULLMOD_ID)
            val captain = member.captain

            if (recruited && hasLink && (captain == null || captain.isDefault)) {
                if (member.id in shipsWithProjet) reassignedFromRemoval = true
                member.captain = projet
                if (upgraded) member.updateStats()
                nowWithProjet.add(member.id)
            } else if (captain == projet && !hasLink) {
                member.captain = null
            } else if (captain == projet) {
                nowWithProjet.add(member.id)
            }
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

    private fun upgradeProjet(projet: PersonAPI) {
        projet.stats.level = 9

        projet.portraitSprite = Global.getSettings().getSpriteName("portraits", "pj_projet1")

        projet.stats.setSkillLevel(Skills.GUNNERY_IMPLANTS, 1f)
        projet.stats.setSkillLevel(Skills.ORDNANCE_EXPERTISE, 1f)
        projet.stats.setSkillLevel(Skills.HELMSMANSHIP, 1f)
        projet.stats.setSkillLevel(Skills.TARGET_ANALYSIS, 1f)
        projet.stats.setSkillLevel(Skills.COMBAT_ENDURANCE, 1f)
        projet.stats.setSkillLevel(Skills.IMPACT_MITIGATION, 1f)
        projet.stats.setSkillLevel(Skills.FIELD_MODULATION, 1f)
        projet.stats.setSkillLevel(Skills.DAMAGE_CONTROL, 1f)
        projet.stats.setSkillLevel(Skills.SYSTEMS_EXPERTISE, 1f)
        projet.setAICoreId("omega_core")
    }

    companion object {
        const val HULLMOD_ID = "PJ_projetLink"
        private const val UPGRADED_KEY = "\$PJ_ProjetThreatUpgraded"

        fun register() {
            if (!Global.getSector().hasTransientScript(ProjetScript::class.java)) {
                Global.getSector().addTransientScript(ProjetScript())
            }
        }
    }
}