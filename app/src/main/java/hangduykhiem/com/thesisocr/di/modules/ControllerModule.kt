package hangduykhiem.com.thesisocr.di.modules

import com.wolt.tacotaco.Controller
import com.wolt.tacotaco.components.LifecycleProvider
import dagger.Module
import dagger.Provides

@Module
class ControllerModule(private val controller: Controller<*, *>) {

    @Provides
    fun provideViewLifecycleProvider(): LifecycleProvider = controller.lifecycleProvider

}
