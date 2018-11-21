package hangduykhiem.com.thesisocr.di.component

import android.app.Activity
import dagger.Subcomponent
import hangduykhiem.com.thesisocr.di.modules.ActivityModule
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.di.scope.PerActivity

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun plus(controllerModule: ControllerModule) : ControllerComponent

    fun inject(activity: Activity)

}
