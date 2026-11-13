package PJ.rulecmd

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.util.Misc

class pj_showRandomPerson: BaseCommandPlugin() {
    override fun execute(ruleId: String?, dialog: InteractionDialogAPI?, params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean {
        val faction = params!![0].string
        val many = params[1].getInt(memoryMap)
        val exception = ArrayList<String>()
        //   val namecsv = Global.getSettings().loadCSV("data/characters/person_names.csv")
        for (i in 0..many) {
            val person = Global.getFactory().createPerson().apply {
                setFaction(faction)
                if (Math.random() > 0.5f) { // TODO : add exception for already picked portraits
                    portraitSprite = Global.getSettings().getFactionSpec(faction).malePortraits.pick()
                    while (exception.contains(portraitSprite)) {
                        portraitSprite = Global.getSettings().getFactionSpec(faction).malePortraits.pick()
                    }
                } else {
                    portraitSprite = Global.getSettings().getFactionSpec(faction).femalePortraits.pick()
                    while (exception.contains(portraitSprite)) {
                        portraitSprite = Global.getSettings().getFactionSpec(faction).femalePortraits.pick()
                    }
                }
                exception.add(portraitSprite)
            }
            when (i){
                0 -> {
                    dialog!!.visualPanel.showPersonInfo(person, true, false)
                } 1 -> {
                dialog!!.visualPanel.showSecondPerson(person)
            } 2 -> {
                dialog!!.visualPanel.showThirdPerson(person)
            }
            }
        }
        return true
    }
}