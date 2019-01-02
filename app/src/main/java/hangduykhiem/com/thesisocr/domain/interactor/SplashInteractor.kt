package hangduykhiem.com.thesisocr.domain.interactor

import android.Manifest
import com.wolt.tacotaco.Interactor
import hangduykhiem.com.thesisocr.domain.delegate.LanguageAssetDelegate
import hangduykhiem.com.thesisocr.domain.delegate.PermissionDelegate
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.controller.ToScanControllerTransition
import hangduykhiem.com.thesisocr.view.controller.ToPermissionDeniedDialogTransition
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SplashInteractor @Inject constructor(
        val permissionDelegate: PermissionDelegate,
        val languageAssetDelegate: LanguageAssetDelegate
) : Interactor<NoArg, NoModel>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onForeground() {
        super.onForeground()
        checkPermissionAndFiles()
    }

    override fun onDetach() {
        compositeDisposable.clear()
        super.onDetach()
    }

    private fun checkPermissionAndFiles() {
        permissionDelegate.checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) { result ->
            if (result) {
                checkFiles()
            } else {
                showPermissionDenied()
            }
        }
    }

    private fun showPermissionDenied() {
        navigate(ToPermissionDeniedDialogTransition)
    }

    private fun checkFiles() {
        if (languageAssetDelegate.shouldCopyLanguageAsset()) {
            copyAsset()
        } else {
            navigate(ToScanControllerTransition)
        }
    }

    fun copyAsset() {
        compositeDisposable.add(languageAssetDelegate.moveLanguageAssetToFolder().subscribe(
                {
                    navigate(ToScanControllerTransition)
                },
                {
                    it.printStackTrace()
                }
        ))
    }
}