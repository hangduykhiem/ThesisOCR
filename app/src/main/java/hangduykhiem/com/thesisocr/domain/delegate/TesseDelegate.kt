package hangduykhiem.com.thesisocr.domain.delegate

import android.content.Context
import javax.inject.Inject
import com.googlecode.tesseract.android.TessBaseAPI

class TesseDelegate @Inject constructor() {

    init {
        val tessBaseAPI = TessBaseAPI()
    }
}