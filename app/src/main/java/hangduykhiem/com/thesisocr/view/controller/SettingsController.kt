package hangduykhiem.com.thesisocr.view.controller

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.LanguageSelectInteractor.Companion.LANGUAGE_KEY
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.MainActivity

class SettingsController : BaseController<NoArg, NoModel>(NoArg) {
    override val layoutId: Int = R.layout.controller_settings
    override val interactor: Interactor<NoArg, NoModel>? = null

    val flLanguageContainer: FrameLayout by bindView(R.id.flLanguageContainer)

    companion object {
        val SHARED_PREFERENCE = "SETTINGS"
    }

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        super.onPostInflate(savedViewState)
        flLanguageContainer.setOnClickListener {
            dispatchTransitionToParent(ToLanguageSelectDialogTranstion)
        }
    }

}

object ToSettingsControllerTransition : Transition