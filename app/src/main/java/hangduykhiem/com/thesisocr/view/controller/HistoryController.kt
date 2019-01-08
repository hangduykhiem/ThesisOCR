package hangduykhiem.com.thesisocr.view.controller

import android.animation.Animator
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.wolt.tacotaco.components.ChangePayload
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.HistoryInteractor
import hangduykhiem.com.thesisocr.domain.interactor.HistoryModel
import hangduykhiem.com.thesisocr.domain.interactor.ResultArgs
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.WorkState
import hangduykhiem.com.thesisocr.helper.buildAnimator
import hangduykhiem.com.thesisocr.view.BaseActivity
import hangduykhiem.com.thesisocr.view.MainActivity
import hangduykhiem.com.thesisocr.view.adapter.HistoryAdapter
import javax.inject.Inject

class HistoryController : BaseController<NoArg, HistoryModel>(NoArg) {

    override val layoutId: Int = R.layout.controller_history
    private val rvList: RecyclerView by bindView(R.id.rvList)
    private val tvFail: TextView by bindView(R.id.tvFail)
    private val pbLoading: ProgressBar by bindView(R.id.pbLoading)
    @Inject
    override lateinit var interactor: HistoryInteractor
    @Inject
    lateinit var baseActivity: BaseActivity
    private lateinit var adapter: HistoryAdapter

    private var resultAnimator: Animator? = null

    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        super.onPostInflate(savedViewState)
        setupRecyclerView()
    }

    override fun onBackground() {
        rvList.visibility = View.GONE // Help animation looks smoother when onForeground()
    }

    override fun onDeflate() {
        resultAnimator?.cancel()
        resultAnimator = null
    }

    private fun setupRecyclerView() {
        val viewManager = LinearLayoutManager(baseActivity)
        adapter = HistoryAdapter { sendCommand(it) }
        rvList.adapter = adapter
        rvList.layoutManager = viewManager
    }

    private fun showResult() {
        resultAnimator?.cancel()
        resultAnimator = buildAnimator(300,
            onStart = {
                rvList.visibility = View.VISIBLE
                rvList.alpha = 0f
            },
            onUpdate = {
                rvList.alpha = it
                tvFail.alpha = 1 - it
                pbLoading.alpha = 1 - it
            },
            onEnd = {
                tvFail.visibility = View.GONE
                pbLoading.visibility = View.GONE
            })
        resultAnimator?.start()
    }

    private fun showFail() {
        resultAnimator?.cancel()
        resultAnimator = buildAnimator(300,
            onStart = {
                tvFail.visibility = View.VISIBLE
                tvFail.alpha = 0f
            },
            onUpdate = {
                tvFail.alpha = it
                rvList.alpha = 1 - it
                pbLoading.alpha = 1 - it
            },
            onEnd = {
                rvList.visibility = View.GONE
                pbLoading.visibility = View.GONE
            })
        resultAnimator?.start()
    }

    private fun showLoading() {
        resultAnimator?.cancel()
        resultAnimator = buildAnimator(300,
            onStart = {
                pbLoading.visibility = View.VISIBLE
                pbLoading.alpha = 0f
            },
            onUpdate = {
                pbLoading.alpha = it
                rvList.alpha = 1 - it
                tvFail.alpha = 1 - it
            },
            onEnd = {
                tvFail.visibility = View.GONE
                rvList.visibility = View.GONE
            })
        resultAnimator?.start()
    }

    override fun renderModel(oldModel: HistoryModel?, newModel: HistoryModel, payload: ChangePayload?) {
        renderLoadingState(oldModel, newModel)
        renderItems(newModel)
    }


    private fun renderLoadingState(oldModel: HistoryModel?, newModel: HistoryModel) {
        if (oldModel?.loadingState != newModel.loadingState) {
            when (newModel.loadingState) {
                WorkState.Complete -> showResult()
                WorkState.InProgress -> showLoading()
                is WorkState.Fail -> showFail()
            }
        }
    }

    private fun renderItems(newModel: HistoryModel) {
        if (newModel.loadingState == WorkState.Complete
            && !newModel.historyResultModels.isEmpty()
        ) {
            adapter.setItems(newModel.historyResultModels)
        }
    }

    class GoToResultCommand(val resultArgs: ResultArgs) : Command

}

object ToHistoryControllerTransition : Transition