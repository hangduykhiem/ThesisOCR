package hangduykhiem.com.thesisocr.di.component

import dagger.Subcomponent
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.di.scope.PerController
import hangduykhiem.com.thesisocr.view.controller.MainController
import hangduykhiem.com.thesisocr.view.controller.ResultController
import hangduykhiem.com.thesisocr.view.controller.RootController
import hangduykhiem.com.thesisocr.view.controller.SplashController

@PerController
@Subcomponent(modules = [ControllerModule::class])
interface ControllerComponent {

    fun inject(rootController: RootController)

    fun inject(mainController: MainController)

    fun inject(resultController: ResultController)

    fun inject(splashController: SplashController)
}
