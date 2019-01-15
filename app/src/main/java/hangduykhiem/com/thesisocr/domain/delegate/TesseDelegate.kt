package hangduykhiem.com.thesisocr.domain.delegate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.googlecode.tesseract.android.TessBaseAPI
import hangduykhiem.com.thesisocr.di.modules.AppModule
import hangduykhiem.com.thesisocr.helper.applyCompletableSchedulers
import hangduykhiem.com.thesisocr.helper.applySchedulers
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Named


class TesseDelegate @Inject constructor(
    @Named(AppModule.APP_CONTEXT) val context: Context
) {

    val TESSBASE_PATH = Environment.getExternalStorageDirectory().toString()
    private var currentLanguages: String = ""
    private val tessBaseAPI: TessBaseAPI = TessBaseAPI()

    fun initLanguages(lang: String): Completable {
        return Completable.fromCallable {
            if (lang != currentLanguages) {
                val success = tessBaseAPI.init(TESSBASE_PATH, lang)
                if (success) {
                    currentLanguages = lang
                }
            }
        }.compose(applyCompletableSchedulers())
    }

    fun getText(uri: Uri): Observable<String> {
        return Observable.fromCallable {
            val bitmap = getBitmap(uri)
            tessBaseAPI.setImage(bitmap)
            return@fromCallable tessBaseAPI.utF8Text
        }.compose(applySchedulers())
    }

    private fun getBitmap(uri: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(uri)
        val result = BitmapFactory.decodeStream(input)
        input?.close()
        val resizedBitmap: Bitmap
        if (result.height > result.width && result.height > 1000) {
            resizedBitmap = Bitmap.createScaledBitmap(
                result, (result.width.toFloat() / result.height * 1000).toInt(), 1000, false
            )
            result.recycle()
        } else if (result.width > result.height && result.width > 1000) {
            resizedBitmap = Bitmap.createScaledBitmap(
                result, 1000, (result.height.toFloat() / result.width * 1000).toInt(), false
            )
            result.recycle()
        } else {
            resizedBitmap = result
        }
        return resizedBitmap
    }
}