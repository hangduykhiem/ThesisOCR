package hangduykhiem.com.thesisocr.domain.interactor

import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.domain.model.OcrResultDomainModel
import hangduykhiem.com.thesisocr.domain.repository.OcrResultRepository
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.WorkState
import hangduykhiem.com.thesisocr.helper.WorkState.*
import hangduykhiem.com.thesisocr.view.controller.HistoryController
import hangduykhiem.com.thesisocr.view.controller.ToResultControllerTransition
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HistoryInteractor @Inject constructor(
    private val ocrResultRepository: OcrResultRepository
) : Interactor<NoArg, HistoryModel>() {

    private val ocrDisposable = CompositeDisposable()

    override fun onAttach(restored: Boolean) {
        super.onAttach(restored)
        if (!restored) {
            updateModel(HistoryModel(arrayListOf(), Other))
        }
    }

    override fun onForeground() {
        super.onForeground()
        loadHistory()
    }

    private fun loadHistory() {
        updateModel(model.copy(loadingState = InProgress))
        ocrDisposable.addAll(ocrResultRepository.getAllOcrResult().subscribe(
            {
                updateModel(model.copy(loadingState = Complete, historyResultModels = it))
            },
            {
                updateModel(model.copy(loadingState = Fail(it)))
            }
        ))
    }

    override fun handleCommand(command: Command) {
        when (command) {
            is HistoryController.GoToResultCommand -> {
                navigate(ToResultControllerTransition(command.resultArgs))
            }
        }
    }


    override fun onDetach() {
        ocrDisposable.clear()
    }
}

data class HistoryModel(
    val historyResultModels: List<OcrResultDomainModel>,
    val loadingState: WorkState
) : Model
