package hangduykhiem.com.thesisocr.view

import android.os.Bundle
import com.wolt.tacotaco.TacoTaco
import hangduykhiem.com.thesisocr.di.component.ActivityComponent
import hangduykhiem.com.thesisocr.view.controller.RootController

class MainActivity : BaseActivity() {

    private val tacoTaco = TacoTaco(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tacoTaco.install(emptyList()) {
            RootController()
        }
    }

    override fun injectThis(component: ActivityComponent) {
        component.inject(this)
    }

}
