package hangduykhiem.com.thesisocr.view.controller

import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.MainActivity

class SettingsController : BaseController<NoArg, NoModel>(NoArg) {
    override val layoutId: Int = R.layout.controller_settings
    override val interactor: Interactor<NoArg, NoModel>? = null

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)

    }

}

object ToSettingsControllerTransition : Transition