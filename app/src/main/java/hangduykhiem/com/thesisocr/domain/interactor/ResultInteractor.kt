package hangduykhiem.com.thesisocr.domain.interactor

import android.net.Uri
import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Args
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.domain.delegate.PermissionDelegate
import hangduykhiem.com.thesisocr.domain.delegate.TesseDelegate
import hangduykhiem.com.thesisocr.helper.WorkState
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ResultInteractor @Inject constructor(
    val tesseDelegate: TesseDelegate,
    val permissionDelegate: PermissionDelegate
) : Interactor<ResultArgs, ResultModel>() {

    val disposables = CompositeDisposable()

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

    override fun onDetach() {
        disposables.clear()
    }

    private fun getResult() {
        updateModel(model.copy(loadingState = WorkState.InProgress))
        tesseDelegate.initLanguage("jpn")
        disposables.add(tesseDelegate.getText(model.uri!!).subscribe(
            { updateModel(model.copy(loadingState = WorkState.Complete, resultString = it)) },
            { updateModel(model.copy(loadingState = WorkState.Fail(it))) }
        ))
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