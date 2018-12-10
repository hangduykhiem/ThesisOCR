package hangduykhiem.com.thesisocr.view.controller

import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.SplashInteractor
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.MainActivity
import javax.inject.Inject

class SplashController : BaseController<NoArg, NoModel>(NoArg) {

    override val layoutId: Int = R.layout.controller_splash
    @Inject
    override lateinit var interactor: SplashInteractor

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

}

object ToSplashControllerTransition : Transition