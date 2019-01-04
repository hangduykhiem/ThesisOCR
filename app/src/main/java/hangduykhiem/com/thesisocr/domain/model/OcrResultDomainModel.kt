package hangduykhiem.com.thesisocr.domain.model

import java.io.Serializable
import java.util.*

data class OcrResultDomainModel(
    var uid: Int,
    var uriString: String,
    var result: String,
    var timestamp: Date
) : Serializable