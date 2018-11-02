package hangduykhiem.com.thesisocr.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class AppModule(val context: Context) {

    companion object {
        const val APP_CONTEXT = "appContext"
    }

    @Provides
    @Named(APP_CONTEXT)
    internal fun provideContext(): Context {
        return context
    }

}