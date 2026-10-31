
package PJ.utils

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.PersonImportance
import com.fs.starfarer.api.characters.FullName
import com.fs.starfarer.api.characters.ImportantPeopleAPI
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.impl.campaign.ids.Personalities
import com.fs.starfarer.api.impl.campaign.ids.Ranks
import com.fs.starfarer.api.impl.campaign.ids.Skills
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.AICores

object PJPeople {

    const val PROJET = "pj_projet"

    fun getPerson(id: String): PersonAPI? {
        return Global.getSector().importantPeople.getPerson(id)
    }

    fun register() {
        createCharacters()
    }

    private fun createCharacters() {
        val ip: ImportantPeopleAPI = Global.getSector().importantPeople

        if (getPerson(PROJET) != null) return

        val person = Global.getFactory().createPerson().apply {
            id = PROJET
            setFaction("player")
            gender = FullName.Gender.FEMALE
            rankId = Ranks.PILOT
            name.first = "Pro-Jet"
            name.last = ""
            importance = PersonImportance.VERY_HIGH
            portraitSprite = Global.getSettings()
                .getSpriteName("portraits", "pj_projet")
            setPersonality(Personalities.RECKLESS)

            stats.level = 3
            stats.setSkillLevel(Skills.HELMSMANSHIP, 2f)
            stats.setSkillLevel(Skills.IMPACT_MITIGATION, 2f)
            stats.setSkillLevel(Skills.COMBAT_ENDURANCE, 2f)
            setAICoreId("gamma_core")

            memoryWithoutUpdate.set("\$chatterChar", "projet")
        }

        ip.addPerson(person)
    }
}
