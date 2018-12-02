package hangduykhiem.com.thesisocr.view.controller

import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.RootInteractor
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.MainActivity
import javax.inject.Inject

class RootController : BaseController<NoArg, NoModel>(NoArg) {

    override val layoutId = R.layout.controller_root

    @Inject
    override lateinit var interactor: RootInteractor

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun handleTransition(transition: Transition) {
        when (transition) {
            ToMainControllerTransition -> {
                val controller = MainController()
                pushChild(controller, R.id.flMainContainer)
            }
            is ToResultControllerTranstion -> {
                val controller = ResultController(transition.args)
                pushChild(controller, R.id.flDialogContainer)
            }
            FromResultControllerTranstion -> {
                popChild(R.id.flDialogContainer, ResultController::class.java.name)
            }
        }
    }

}
