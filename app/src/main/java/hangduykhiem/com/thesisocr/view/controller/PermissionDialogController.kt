package hangduykhiem.com.thesisocr.view.controller

import android.os.Bundle
import android.widget.TextView
import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.MainActivity

class PermissionDialogController : BaseController<NoArg, NoModel>(NoArg) {
    override val layoutId: Int = R.layout.controller_dialog_permission
    override val interactor: Interactor<NoArg, NoModel>? = null

    private val tvOkay: TextView by bindView(R.id.tvOkay)

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        super.onPostInflate(savedViewState)
        tvOkay.setOnClickListener {
            dispatchTransitionToParent(FromPermissionDeniedDialogTransition)
        }
    }

}

object ToPermissionDeniedDialogTransition : Transition
object FromPermissionDeniedDialogTransition : Transition