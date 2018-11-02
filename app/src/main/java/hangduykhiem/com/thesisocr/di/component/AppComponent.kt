package hangduykhiem.com.thesisocr.di.component

import dagger.Component
import hangduykhiem.com.thesisocr.ThesisApp
import hangduykhiem.com.thesisocr.di.modules.ActivityModule
import hangduykhiem.com.thesisocr.di.modules.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun plus(module: ActivityModule): ActivityComponent

    fun inject(app: ThesisApp)
}

