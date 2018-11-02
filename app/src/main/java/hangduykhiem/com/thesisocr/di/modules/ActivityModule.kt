package hangduykhiem.com.thesisocr.di.modules

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ActivityModule(private val activity: Activity) {

    companion object {
        const val ACTIVITY_CONTEXT = "activityContext"
    }

    @Provides
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    @Named(ACTIVITY_CONTEXT)
    fun provideContext(): Context {
        return activity
    }

}