package hangduykhiem.com.thesisocr.domain.interactor

import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Args
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.helper.WorkState
import javax.inject.Inject

class ResultInteractor @Inject constructor(

) : Interactor<ResultArgs, ResultModel>() {

    override fun onAttach(restored: Boolean) {
        super.onAttach(restored)
        if (!restored) {
            updateModel(ResultModel(loadingState = WorkState.Other))
        }
    }

}

data class ResultArgs(
    val filePath: String? = null,
    val id: String? = null
) : Args

data class ResultModel(
    val filePath: String? = null,
    val resultString: String? = null,
    val loadingState: WorkState
) : Model