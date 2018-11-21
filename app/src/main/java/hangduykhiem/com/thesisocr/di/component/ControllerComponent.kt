package hangduykhiem.com.thesisocr.di.component

import dagger.Subcomponent
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.di.scope.PerController
import hangduykhiem.com.thesisocr.view.controller.MainController
import hangduykhiem.com.thesisocr.view.controller.RootController

@PerController
@Subcomponent(modules = [ControllerModule::class])
interface ControllerComponent {

    fun inject(rootController: RootController)

    fun inject(mainController: MainController)
}
