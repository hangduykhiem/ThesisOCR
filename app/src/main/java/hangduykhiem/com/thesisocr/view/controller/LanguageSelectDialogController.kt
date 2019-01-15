package hangduykhiem.com.thesisocr.view.controller

import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import com.wolt.tacotaco.components.ChangePayload
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.LanguageSelectInteractor
import hangduykhiem.com.thesisocr.domain.interactor.LanguageSelectModel
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.view.MainActivity
import javax.inject.Inject

class LanguageSelectDialogController : BaseController<NoArg, LanguageSelectModel>(NoArg) {
    override val layoutId: Int = R.layout.controller_dialog_language_select
    @Inject
    override lateinit var interactor: LanguageSelectInteractor

    private val tvConfirm: TextView by bindView(R.id.tvConfirm)
    private val cbEng: CheckBox by bindView(R.id.cbEng)
    private val cbVie: CheckBox by bindView(R.id.cbVie)
    private val cbFin: CheckBox by bindView(R.id.cbFin)
    private val cbJap: CheckBox by bindView(R.id.cbJap)

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        super.onPostInflate(savedViewState)
        tvConfirm.setOnClickListener {
            val languageSet = mutableSetOf<String>()
            if (cbEng.isChecked) languageSet.add("ENG")
            if (cbVie.isChecked) languageSet.add("VIE")
            if (cbFin.isChecked) languageSet.add("FIN")
            if (cbJap.isChecked) languageSet.add("JPN")
            sendCommand(UpdateLanguageCommand(languageSet))
            dispatchTransitionToParent(FromLanguageSelectDialogTransition)
        }
    }

    override fun renderModel(oldModel: LanguageSelectModel?, newModel: LanguageSelectModel, payload: ChangePayload?) {
        if (oldModel?.languageSet != newModel.languageSet) {
            cbEng.isChecked = newModel.languageSet.contains("ENG")
            cbVie.isChecked = newModel.languageSet.contains("VIE")
            cbFin.isChecked = newModel.languageSet.contains("FIN")
            cbJap.isChecked = newModel.languageSet.contains("JPN")
        }
    }

    override fun onBackPressed(): Boolean {
        dispatchTransitionToParent(FromLanguageSelectDialogTransition)
        return true
    }

    class UpdateLanguageCommand(val languages: Set<String>) : Command

}

object ToLanguageSelectDialogTranstion : Transition
object FromLanguageSelectDialogTransition : Transition