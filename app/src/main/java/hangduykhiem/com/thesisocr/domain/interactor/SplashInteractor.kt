package hangduykhiem.com.thesisocr.domain.interactor

import android.util.Log
import com.wolt.tacotaco.Interactor
import hangduykhiem.com.thesisocr.domain.delegate.LanguageAssetDelegate
import hangduykhiem.com.thesisocr.domain.delegate.PermissionDelegate
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.controller.ToPermissionDialogTransition
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SplashInteractor @Inject constructor(
    val permissionDelegate: PermissionDelegate,
    val languageAssetDelegate: LanguageAssetDelegate

) : Interactor<NoArg, NoModel>() {

    val compositeDisposable = CompositeDisposable()


    override fun onAttach(restored: Boolean) {
        super.onAttach(restored)
        if (!restored) {
            requestStoragePermission()
        }
    }

    override fun onDetach() {
        compositeDisposable.clear()
        super.onDetach()
    }

    override fun onForeground() {
        super.onForeground()
        requestStoragePermission()
    }

    fun requestStoragePermission() {
        permissionDelegate.requestStoragePermission { result ->
            if (result) {
                if (languageAssetDelegate.shouldCopyLanguageAsset()) {
                    copyAsset()
                }
            } else {
                navigate(ToPermissionDialogTransition { permissionDialogResultHanlder(it) })
            }
        }
    }

    fun permissionDialogResultHanlder(success: Boolean) {
        if (success) {

        } else {

        }
    }

    fun copyAsset() {
        compositeDisposable.add(languageAssetDelegate.moveLanguageAssetToFolder().subscribe(
            {
                Log.d("SplashInteractor", "Yey")
            },
            {
                Log.d("SplashInteractor", "Nay")
                it.printStackTrace()
            }
        ))
    }
}