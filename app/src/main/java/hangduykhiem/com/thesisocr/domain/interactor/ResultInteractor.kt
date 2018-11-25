package hangduykhiem.com.thesisocr.domain.interactor

import android.net.Uri
import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Args
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.domain.delegate.PermissionDelegate
import hangduykhiem.com.thesisocr.domain.delegate.TesseDelegate
import hangduykhiem.com.thesisocr.helper.WorkState
import javax.inject.Inject

class ResultInteractor @Inject constructor(
    val tesseDelegate: TesseDelegate,
    val permissionDelegate: PermissionDelegate
) : Interactor<ResultArgs, ResultModel>() {

    override fun onAttach(restored: Boolean) {
        super.onAttach(restored)
        if (!restored) {
            if (args.uri != null) {
                updateModel(ResultModel(loadingState = WorkState.Other, uri = args.uri))
            } else if (args.id != null) {
                // TODO: Load from Room
            }
            permissionDelegate.requestStoragePermission {
                if (it) {
                    getResult()
                } else {
                    updateModel(ResultModel(loadingState = WorkState.Fail(SecurityException())))
                }
            }
        }
    }

    private fun getResult() {
        updateModel(model.copy(loadingState = WorkState.InProgress))
        tesseDelegate.initLanguage("jpn")
        val result = tesseDelegate.getText(model.uri!!)
        updateModel(model.copy(loadingState = WorkState.Complete, resultString = result))
    }
}

data class ResultArgs(
    val uri: Uri? = null,
    val id: String? = null
) : Args

data class ResultModel(
    val uri: Uri? = null,
    val resultString: String? = null,
    val loadingState: WorkState
) : Model