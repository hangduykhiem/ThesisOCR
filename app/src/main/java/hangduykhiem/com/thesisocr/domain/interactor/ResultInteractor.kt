package hangduykhiem.com.thesisocr.domain.interactor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Args
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.domain.delegate.PermissionDelegate
import hangduykhiem.com.thesisocr.domain.delegate.TesseDelegate
import hangduykhiem.com.thesisocr.domain.model.OcrResultDomainModel
import hangduykhiem.com.thesisocr.domain.repository.OcrResultRepository
import hangduykhiem.com.thesisocr.helper.WorkState
import hangduykhiem.com.thesisocr.view.BaseActivity
import hangduykhiem.com.thesisocr.view.controller.ResultController
import hangduykhiem.com.thesisocr.view.controller.SettingsController
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class ResultInteractor @Inject constructor(
    private val tesseDelegate: TesseDelegate,
    private val permissionDelegate: PermissionDelegate,
    private val ocrResultRepository: OcrResultRepository,
    private val activity: BaseActivity
) : Interactor<ResultArgs, ResultModel>() {

    private val disposables = CompositeDisposable()
    private var currentLanguages = setOf("ENG");

    override fun onAttach(restored: Boolean) {
        super.onAttach(restored)
        checkLanguage()
        if (!restored) {
            if (args.uriString != null) {
                updateModel(ResultModel(loadingState = WorkState.Other, uriString = args.uriString))
                permissionDelegate.checkAndRequestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) { result ->
                    if (result) {
                        getResult()
                    } else {
                        updateModel(ResultModel(loadingState = WorkState.Fail(SecurityException())))
                    }
                }
            } else if (args.ocrResultDomainModel != null) {
                val result = args.ocrResultDomainModel!!
                updateModel(
                    ResultModel(
                        loadingState = WorkState.Complete,
                        uriString = result.uriString,
                        resultString = result.result
                    )
                )
            }
        }
    }

    override fun onForeground() {
        checkLanguage()
    }

    private fun checkLanguage() {
        currentLanguages = activity.getSharedPreferences(SettingsController.SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .getStringSet(LanguageSelectInteractor.LANGUAGE_KEY, setOf("ENG"))!!
    }

    override fun onDetach() {
        disposables.clear()
    }

    override fun handleCommand(command: Command) {
        when (command) {
            ResultController.CopyToClipboardCommand -> {
                copyResultToClipboard()
            }
        }
    }

    private fun copyResultToClipboard() {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("OCR result", model.resultString)
        if (clipboard != null) {
            clipboard.primaryClip = clip
        }
    }

    private fun getResult() {
        updateModel(model.copy(loadingState = WorkState.InProgress))
        val uri = Uri.parse(model.uriString) ?: return
        disposables.add(tesseDelegate.initLanguages(currentLanguages.joinToString("+")).andThen(
            tesseDelegate.getText(uri).flatMapCompletable {
                updateModel(model.copy(loadingState = WorkState.Complete, resultString = it))
                ocrResultRepository.saveOcrResult(uri = uri, result = it)
            }
        ).subscribe(
            { },
            { updateModel(model.copy(loadingState = WorkState.Fail(it))) }
        ))
    }

}

data class ResultArgs(
    val uriString: String? = null,
    val ocrResultDomainModel: OcrResultDomainModel? = null
) : Args

data class ResultModel(
    val uriString: String? = null,
    val resultString: String? = null,
    val loadingState: WorkState
) : Model