package hangduykhiem.com.thesisocr.domain.interactor

import android.util.Log
import com.wolt.tacotaco.Interactor
import hangduykhiem.com.thesisocr.domain.delegate.LanguageAssetDelegate
import hangduykhiem.com.thesisocr.domain.delegate.PermissionDelegate
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
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

    fun requestStoragePermission() {
        permissionDelegate.requestStoragePermission { result ->
            if (result) {
                copyAsset()
            } else {
                //TODO: handle this
            }
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