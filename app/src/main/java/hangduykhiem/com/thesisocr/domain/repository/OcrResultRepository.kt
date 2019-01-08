package hangduykhiem.com.thesisocr.domain.repository

import android.net.Uri
import hangduykhiem.com.thesisocr.data.dao.OcrResultDAO
import hangduykhiem.com.thesisocr.data.model.OcrResultData
import hangduykhiem.com.thesisocr.domain.model.OcrResultDomainModel
import hangduykhiem.com.thesisocr.helper.applyCompletableSchedulers
import hangduykhiem.com.thesisocr.helper.applySingleSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OcrResultRepository @Inject constructor(
    val ocrResultDAO: OcrResultDAO
) {

    fun getAllOcrResult(): Single<List<OcrResultDomainModel>> {
        return ocrResultDAO.getAllOcrResult().map { list ->
            list.map { data ->
                OcrResultDomainModel(data.uid!!, data.uri, data.result, data.timestamp)
            }.reversed()
        }.delay(500, TimeUnit.MILLISECONDS).compose(applySingleSchedulers())
    }

    fun saveOcrResult(uri: Uri, result: String): Completable {
        return Completable.fromCallable {
            ocrResultDAO.insertAll(
                OcrResultData(
                    uri = uri.toString(),
                    result = result,
                    timestamp = Date(System.currentTimeMillis())
                )
            )
        }.compose(applyCompletableSchedulers())
    }

}