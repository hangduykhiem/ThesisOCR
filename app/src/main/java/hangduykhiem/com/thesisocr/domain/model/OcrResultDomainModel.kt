package hangduykhiem.com.thesisocr.domain.model

import android.net.Uri
import java.io.Serializable
import java.util.*

data class OcrResultDomainModel(
    var uid: Int,
    var uri: Uri,
    var result: String,
    var timestamp: Date
) : Serializable