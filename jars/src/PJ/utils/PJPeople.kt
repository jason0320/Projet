
package PJ.utils

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.PersonImportance
import com.fs.starfarer.api.characters.FullName
import com.fs.starfarer.api.characters.ImportantPeopleAPI
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.impl.campaign.ids.Personalities
import com.fs.starfarer.api.impl.campaign.ids.Skills

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
            setFaction("pj_projetFaction")
            gender = FullName.Gender.FEMALE
            rankId = "pj_projet"
            postId = "pj_projet"
            name.first = "Pro-Jet"
            name.last = ""
            importance = PersonImportance.VERY_HIGH
            portraitSprite = Global.getSettings()
                .getSpriteName("portraits", "pj_projet")
            setPersonality(Personalities.RECKLESS)

            stats.level = 5
            stats.setSkillLevel(Skills.COMBAT_ENDURANCE, 1f)
            stats.setSkillLevel(Skills.SYSTEMS_EXPERTISE, 1f)
            stats.setSkillLevel(Skills.GUNNERY_IMPLANTS, 1f)
            stats.setSkillLevel(Skills.ORDNANCE_EXPERTISE, 1f)
            stats.setSkillLevel(Skills.FIELD_MODULATION, 1f)

            memoryWithoutUpdate.set("\$chatterChar", "projet")
        }

        ip.addPerson(person)
    }
}
