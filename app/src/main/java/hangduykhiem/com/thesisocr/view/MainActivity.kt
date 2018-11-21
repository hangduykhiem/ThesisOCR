package hangduykhiem.com.thesisocr.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wolt.tacotaco.TacoTaco
import hangduykhiem.com.thesisocr.ThesisApp
import hangduykhiem.com.thesisocr.di.component.ActivityComponent
import hangduykhiem.com.thesisocr.di.modules.ActivityModule
import hangduykhiem.com.thesisocr.view.controller.RootController

class MainActivity : AppCompatActivity() {

    private val tacoTaco = TacoTaco(this)
    lateinit var component: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tacoTaco.install(emptyList()) {
            RootController()
        }
        component = ThesisApp.appComponent
            .plus(ActivityModule(this))
        component.inject(this)
    }

}
