package hangduykhiem.com.thesisocr.di.modules

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import hangduykhiem.com.thesisocr.view.BaseActivity
import javax.inject.Named

@Module
class ActivityModule(private val activity: BaseActivity) {

    companion object {
        const val ACTIVITY_CONTEXT = "activityContext"
    }

    @Provides
    fun provideActivity(): BaseActivity {
        return activity
    }

    @Provides
    @Named(ACTIVITY_CONTEXT)
    fun provideContext(): Context {
        return activity
    }

}