package hangduykhiem.com.thesisocr.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import hangduykhiem.com.thesisocr.data.dao.OcrResultDAO
import hangduykhiem.com.thesisocr.data.model.Converters
import hangduykhiem.com.thesisocr.data.model.OcrResultData

@Database(entities = [OcrResultData::class], version = 1)
@TypeConverters(Converters::class)
abstract class ResultDatabase : RoomDatabase() {
    abstract fun ocrDao(): OcrResultDAO
}