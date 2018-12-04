package hangduykhiem.com.thesisocr.domain.model

import android.net.Uri
import java.util.*

data class OcrResultDomain(
    var uid: Int,
    var uri: Uri,
    var result: String,
    var timestamp: Date
)