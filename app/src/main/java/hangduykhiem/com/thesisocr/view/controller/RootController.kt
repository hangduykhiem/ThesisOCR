package hangduykhiem.com.thesisocr.view.controller

import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.RootInteractor
import hangduykhiem.com.thesisocr.helper.DialogPopAnimation
import hangduykhiem.com.thesisocr.helper.DialogPushAnimation
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
            ToSplashControllerTransition -> {
                val controller = SplashController()
                pushChild(controller, R.id.flMainContainer)
            }
            ToMainTabsTransition -> {
                val controller = MainTabsController()
                pushChild(controller, R.id.flMainContainer)
            }
            FromResultControllerTransition -> {
                popChild(R.id.flMainContainer, ResultController::class.java.name)
            }
            is ToPermissionDeniedDialogTransition -> {
                val controller = PermissionDialogController()
                pushChild(controller, R.id.flDialogContainer, DialogPushAnimation())
            }
            FromPermissionDeniedDialogTransition -> {
                popChild(R.id.flDialogContainer, PermissionDialogController::class.java.name, DialogPopAnimation())
            }
            is ToResultControllerTransition -> {
                val controller = ResultController(transition.args)
                pushChild(controller, R.id.flMainContainer)
            }

        }
    }

}
