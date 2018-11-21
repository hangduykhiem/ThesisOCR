package hangduykhiem.com.thesisocr.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hangduykhiem.com.thesisocr.ThesisApp
import hangduykhiem.com.thesisocr.di.component.ActivityComponent
import hangduykhiem.com.thesisocr.di.modules.ActivityModule
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    lateinit var component: ActivityComponent
    private val callbacksList = ArrayList<ActivityCallbacks>()
    private var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        initDependencies()
    }

    private fun initDependencies() {
        component = ThesisApp.appComponent
            .plus(ActivityModule(this))
        injectThis(component)
    }

    protected abstract fun injectThis(component: ActivityComponent)


    override fun onResume() {
        super.onResume()
        for (c in callbacksList) {
            c.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        for (c in callbacksList) {
            c.onPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (c in callbacksList) {
            c.onDestroy()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // callbacksList can be modified inside the loop
        val list = ArrayList<ActivityCallbacks>(callbacksList)
        for (c in list) {
            if (callbacksList.contains(c)) {
                c.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (c in callbacksList) {
            c.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        for (c in callbacksList) {
            c.onSaveInstanceState(outState)
        }
    }

}

abstract class ActivityCallbacks {

    abstract fun onResume()

    abstract fun onPause()

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    abstract fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    )

    abstract fun onSaveInstanceState(outState: Bundle)

    abstract fun onDestroy()

}
