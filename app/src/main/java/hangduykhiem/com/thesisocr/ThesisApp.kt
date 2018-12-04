package hangduykhiem.com.thesisocr

import android.app.Application
import android.content.Context
import hangduykhiem.com.thesisocr.di.component.AppComponent
import hangduykhiem.com.thesisocr.di.component.DaggerAppComponent
import hangduykhiem.com.thesisocr.di.modules.AppModule

class ThesisApp : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    lateinit var appContext: Context

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initDagger()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        appComponent.inject(this)
    }

}