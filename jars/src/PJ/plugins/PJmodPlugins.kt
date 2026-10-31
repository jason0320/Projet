
package PJ.plugins

import PJ.campaign.ProjetScript
import PJ.utils.PJPeople
import com.fs.starfarer.api.BaseModPlugin

class PJmodPlugins : BaseModPlugin() {

    override fun onNewGame() {
        initializeProjet()
    }

    override fun onGameLoad(newGame: Boolean) {
        initializeProjet()
    }

    private fun initializeProjet() {
        PJPeople.register()
        ProjetScript.register()
    }


}
