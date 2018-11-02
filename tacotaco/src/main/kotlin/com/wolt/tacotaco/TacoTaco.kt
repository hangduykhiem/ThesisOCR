package com.wolt.tacotaco

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.wolt.tacotaco.Controller.LifecycleStage.*
import com.wolt.tacotaco.components.SerializableSingleton
import com.wolt.tacotaco.helpers.LifecycleConductor
import com.wolt.tacotaco.helpers.StoreRestoreHelper

class TacoTaco(private val activity: Activity) {

    internal companion object {
        private const val TAG_FRAGMENT = "rootFragment"
    }

    fun install(serializableSingletons: List<SerializableSingleton>, rootControllerFactory: () -> Controller<*, *>) {
        var fragment = findFragment()
        if (fragment == null) {
            fragment = RootFragment()
            activity.fragmentManager.beginTransaction().add(fragment, TAG_FRAGMENT).commit()
        }
        fragment.singletons = serializableSingletons
        fragment.controllerFactory = rootControllerFactory
    }

    fun onBackPressed() = findFragment()?.onBackPressed() ?: false

    private fun findFragment() = activity.fragmentManager.findFragmentByTag(TAG_FRAGMENT) as RootFragment?

    internal class RootFragment : Fragment() {

        lateinit var singletons: List<SerializableSingleton>
        lateinit var controllerFactory: () -> Controller<*, *>
        private lateinit var rootController: Controller<*, *>

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            Crashlytics.log("TacoTaco onActivityCreated ${activity.hashCode()}")
            if (savedInstanceState != null) {
                try {
                    rootController = StoreRestoreHelper.restoreRoot(activity, singletons, savedInstanceState)
                    return
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        throw e
                    } else {
                        Crashlytics.logException(e)
                    }
                }
            }
            rootController = controllerFactory.invoke()
            rootController.activityInternal = activity
            LifecycleConductor.changeStage(rootController, TopDeflated)
        }

        override fun onStart() {
            super.onStart()
            LifecycleConductor.changeStage(rootController, TopInflatedForeground)
        }

        override fun onStop() {
            super.onStop()
            LifecycleConductor.changeStage(rootController, TopInflatedBackground)
        }

        override fun onDestroy() {
            super.onDestroy()
            LifecycleConductor.changeStage(rootController, Dead)
        }

        fun onBackPressed() = rootController.onBackPressed()

        override fun onSaveInstanceState(outState: Bundle) {
            try {
                StoreRestoreHelper.storeRoot(activity, singletons, rootController, outState)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    throw e
                } else {
                    Crashlytics.logException(e)
                }
            }
        }

        override fun onLowMemory() {
            super.onLowMemory()
            rootController.onLowMemoryInternal()
        }

        override fun onTrimMemory(level: Int) {
            super.onTrimMemory(level)
            val checkList = listOf(TRIM_MEMORY_RUNNING_LOW, TRIM_MEMORY_RUNNING_CRITICAL, TRIM_MEMORY_BACKGROUND,
                    TRIM_MEMORY_MODERATE, TRIM_MEMORY_COMPLETE)
            if (level in checkList) {
                rootController.onTrimMemory()
            }
        }

    }

}
