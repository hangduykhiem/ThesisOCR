package hangduykhiem.com.thesisocr.di.component

import dagger.Subcomponent
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.di.scope.PerController
import hangduykhiem.com.thesisocr.view.controller.*

@PerController
@Subcomponent(modules = [ControllerModule::class])
interface ControllerComponent {

    fun inject(rootController: RootController)

    fun inject(scanController: ScanController)

    fun inject(resultController: ResultController)

    fun inject(splashController: SplashController)

    fun inject(permissionDialogController: PermissionDialogController)

    fun inject(mainTabsController: MainTabsController)
}
