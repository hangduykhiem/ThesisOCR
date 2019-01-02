package hangduykhiem.com.thesisocr.view.controller

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.wolt.tacotaco.Controller
import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.BaseActivity

class MainTabsController : BaseController<NoArg, NoModel>(NoArg) {

    override val layoutId = R.layout.controller_main_tabs
    private val bottomNavigationView: BottomNavigationView by bindView(R.id.bottomNavigationView)

    override val interactor: Interactor<NoArg, NoModel>? = null


    override fun inject() {
        (activity as BaseActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        setupBottomNavigationView()
        bottomNavigationView.selectedItemId = R.id.menu_to_main
    }

    private fun setupBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_to_settings -> handleTransition(ToSettingsControllerTransition)
                R.id.menu_to_main -> handleTransition(ToScanControllerTransition)
                R.id.menu_to_history -> handleTransition(ToHistoryControllerTransition)
                else -> {
                    /* Do nothing */
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun handleTransition(transition: Transition) {
        when (transition) {
            ToScanControllerTransition -> goToTab(ScanController::class.java.name, { ScanController() })
            ToHistoryControllerTransition -> goToTab(HistoryController::class.java.name, { HistoryController() })
            ToSettingsControllerTransition -> goToTab(SettingsController::class.java.name, { SettingsController() })
            else -> dispatchTransitionToParent(transition)
        }
    }

    private fun goToTab(tag: String, createControllerAction: () -> Controller<*, *>, recreate: Boolean = false) {
        val backstack = getBackstack(R.id.flTabsContainer).toMutableList()
        if (recreate) {
            backstack.removeAll { it.tag == tag }
        }
        val index = backstack.indexOfFirst { it.tag == tag }
        if (index == -1) {
            val controller = createControllerAction()
            backstack.add(controller)
        } else {
            backstack.add(backstack.removeAt(index))
        }
        setBackstack(R.id.flTabsContainer, backstack)
    }

}

object ToMainTabsTransition : Transition
