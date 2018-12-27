package hangduykhiem.com.thesisocr.view.controller

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.MainActivity

class PermissionDialogController(val onResult: (Boolean) -> Unit) : BaseController<NoArg, NoModel>(NoArg) {
    override val layoutId: Int = R.layout.controller_dialog_permission
    override val interactor = null

    val tvToSettings: TextView by bindView(R.id.tvToSettings)
    val tvCancel: TextView by bindView(R.id.tvCancel)

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        super.onPostInflate(savedViewState)
        tvToSettings.setOnClickListener {
            activity.startActivityForResult(Intent(Settings.ACTION_SETTINGS), 0)
            onResult(true)
            dispatchTransitionToParent(FromPermissionDialogTransition)
        }
        tvCancel.setOnClickListener {
            onResult(false)
            dispatchTransitionToParent(FromPermissionDialogTransition)
        }
    }

}

class ToPermissionDialogTransition(val onResult: (Boolean) -> Unit) : Transition
object FromPermissionDialogTransition : Transition