package PJ.campaign

import PJ.utils.PJPeople
import PJ.utils.PJPeople.PROJET
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.util.IntervalUtil

class ProjetScript : EveryFrameScript {

    private val interval = IntervalUtil(0.2f, 0.3f)

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun advance(amount: Float) {
        interval.advance(amount)
        if (!interval.intervalElapsed()) return

        val projet = PJPeople.getPerson(PROJET) ?: return
        val recruited = Global.getSector().memoryWithoutUpdate.getBoolean("\$PJ_ProjetRecruited")

        // The refit picker only lists hullmods the player faction knows, so unlock it once recruited.
        if (recruited) {
            val faction = Global.getSector().playerFaction
            if (!faction.knowsHullMod(HULLMOD_ID)) faction.addKnownHullMod(HULLMOD_ID)
        }

        Global.getSector().playerFleet.fleetData.membersListCopy.forEach { member ->
            if (recruited
                && member.variant.hasHullMod(HULLMOD_ID)
                && (member.captain == null || member.captain.isDefault)
            ) {
                member.captain = projet
            } else if (member.captain == projet && !member.variant.hasHullMod(HULLMOD_ID)) {
                member.captain = null
            }
        }
    }

    companion object {
        const val HULLMOD_ID = "PJ_projetLink"

        fun register() {
            if (!Global.getSector().hasTransientScript(ProjetScript::class.java)) {
                Global.getSector().addTransientScript(ProjetScript())
            }
        }
    }
}