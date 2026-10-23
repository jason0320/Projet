package PJ.hullmods

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc

class ProjetLink : BaseHullMod() {

    companion object {
        private const val RECRUITED_KEY = "\$PJ_ProjetRecruited"
    }

    override fun showInRefitScreenModPickerFor(ship: ShipAPI?): Boolean {
        return Global.getSector()
            .memoryWithoutUpdate
            .getBoolean(RECRUITED_KEY)
    }

    override fun addPostDescriptionSection(
        tooltip: TooltipMakerAPI,
        hullSize: ShipAPI.HullSize?,
        ship: ShipAPI?,
        width: Float,
        isForModSpec: Boolean
    ) {
        val pad = 10f

        if (!Global.getSector().memoryWithoutUpdate.getBoolean(RECRUITED_KEY)) {
            tooltip.addPara(
                "The device is clearly meant for installation on ship command systems, but its actual purpose is unknown.",
                pad,
                Misc.getPositiveHighlightColor()
            )
            return
        }

        tooltip.addSectionHeading("Remote Control System", Alignment.MID, pad)
        tooltip.addPara(
            "When no human officer is assigned to this ship, Projet will be assigned command instead.",
            pad,
            Misc.getEnergyMountColor(),
            "Projet"
        )
    }
}
