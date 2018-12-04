package hangduykhiem.com.thesisocr.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import hangduykhiem.com.thesisocr.data.model.OcrResultData
import io.reactivex.Single

@Dao
interface OcrResultDAO {

    @Query("SELECT * FROM results")
    fun getAllOcrResult(): Single<List<OcrResultData>>

    @Insert
    fun insertAll(vararg users: OcrResultData)

    @Delete
    fun deleteAll(vararg users: OcrResultData)

}