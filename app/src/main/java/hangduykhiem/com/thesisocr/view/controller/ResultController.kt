package hangduykhiem.com.thesisocr.view.controller

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.wolt.tacotaco.components.ChangePayload
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.ResultArgs
import hangduykhiem.com.thesisocr.domain.interactor.ResultInteractor
import hangduykhiem.com.thesisocr.domain.interactor.ResultModel
import hangduykhiem.com.thesisocr.helper.WorkState
import hangduykhiem.com.thesisocr.view.MainActivity
import javax.inject.Inject

class ResultController(
    args: ResultArgs
) : BaseController<ResultArgs, ResultModel>(args) {

    override val layoutId = R.layout.controller_result
    private val ivResult: ImageView by bindView(R.id.ivResult)
    private val tvResult: TextView by bindView(R.id.tvResult)
    private val pbLoading: ProgressBar by bindView(R.id.pbLoading)
    private val tvCopy: TextView by bindView(R.id.tvCopy)

    @Inject
    override lateinit var interactor: ResultInteractor

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        super.onPostInflate(savedViewState)
        tvCopy.setOnClickListener {
            sendCommand(CopyToClipboardCommand)
        }
    }

    override fun onBackPressed(): Boolean {
        dispatchTransitionToParent(FromResultControllerTransition)
        return true
    }

    override fun renderModel(oldModel: ResultModel?, newModel: ResultModel, payload: ChangePayload?) {
        renderLoading(oldModel, newModel)
        renderImage(oldModel, newModel)
    }

    private fun renderLoading(oldModel: ResultModel?, newModel: ResultModel) {
        if (oldModel?.loadingState != newModel.loadingState) {
            when (newModel.loadingState) {
                WorkState.Complete -> {
                    tvResult.text = newModel.resultString
                    pbLoading.visibility = View.GONE
                }
                WorkState.InProgress -> {
                    pbLoading.visibility = View.VISIBLE
                }
                is WorkState.Fail -> {
                }
            }
        }
    }

    private fun renderImage(oldModel: ResultModel?, newModel: ResultModel) {
        if (oldModel?.uriString != newModel.uriString && newModel.uriString != null) {
            Glide.with(activity).load(Uri.parse(newModel.uriString)).into(ivResult)
        }
    }

    object CopyToClipboardCommand : Command

}

class ToResultControllerTransition(val args: ResultArgs) : Transition
object FromResultControllerTransition : Transition