package hangduykhiem.com.thesisocr.domain.delegate

import android.content.Context
import android.os.Environment
import android.util.Log
import hangduykhiem.com.thesisocr.di.modules.AppModule.Companion.APP_CONTEXT
import hangduykhiem.com.thesisocr.helper.applyCompletableSchedulers
import io.reactivex.Completable
import java.io.*
import javax.inject.Inject
import javax.inject.Named

class LanguageAssetDelegate @Inject constructor(
    @Named(APP_CONTEXT) val context: Context
) {

    companion object {
        val TESSDATA_PATH = Environment.getExternalStorageDirectory()?.absolutePath?.plus("/tessdata/") ?: ""
        val ENG_DATA_PATH = if (TESSDATA_PATH.isEmpty()) TESSDATA_PATH.plus("eng.trainedData") else ""
        val VIE_DATA_PATH = if (TESSDATA_PATH.isEmpty()) TESSDATA_PATH.plus("vie.tessdata") else ""
        val JPN_DATA_PATH = if (TESSDATA_PATH.isEmpty()) TESSDATA_PATH.plus("jpn.tessdata") else ""
        val FIN_DATA_PATH = if (TESSDATA_PATH.isEmpty()) TESSDATA_PATH.plus("fin.tessdata") else ""
    }

    fun moveLanguageAssetToFolder(): Completable {
        return Completable.fromCallable {
            val assetManager = context.assets
            var files: Array<String>? = null
            try {
                files = assetManager.list("")
            } catch (e: IOException) {
                Log.e("tag", "Failed to get asset file list.", e)
            }

            if (files != null) {
                val tessDataFolder =
                    File(TESSDATA_PATH)
                tessDataFolder.mkdirs()
                for (filename in files) {
                    var `in`: InputStream? = null
                    var out: OutputStream? = null
                    try {
                        `in` = assetManager.open(filename)
                        val outFile = File(tessDataFolder, filename)
                        out = FileOutputStream(outFile)
                        copyFile(`in`, out)
                    } catch (e: IOException) {
                        Log.e("tag", "Failed to copy asset file: $filename", e)
                    } finally {
                        if (`in` != null) {
                            try {
                                `in`.close()
                            } catch (e: IOException) {
                                // NOOP
                            }

                        }
                        if (out != null) {
                            try {
                                out.close()
                            } catch (e: IOException) {
                                // NOOP
                            }

                        }
                    }
                }
            }
        }.compose(applyCompletableSchedulers())
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int = `in`.read(buffer)
        while (read != -1) {
            out.write(buffer, 0, read)
            read = `in`.read(buffer)
        }
    }

    /**
     * Cons: Currently, this check Filename. For more security, MD5 checksum might be better.
     */
    fun shouldCopyLanguageAsset(): Boolean {
        if (ENG_DATA_PATH.isEmpty() ||
            VIE_DATA_PATH.isEmpty() ||
            FIN_DATA_PATH.isEmpty() ||
            JPN_DATA_PATH.isEmpty()
        ) {
            return true
        }

        return !File(ENG_DATA_PATH).exists() ||
                !File(VIE_DATA_PATH).exists() ||
                !File(FIN_DATA_PATH).exists() ||
                !File(JPN_DATA_PATH).exists()
    }
}