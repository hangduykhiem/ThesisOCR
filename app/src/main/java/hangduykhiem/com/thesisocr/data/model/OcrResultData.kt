package hangduykhiem.com.thesisocr.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "results")
data class OcrResultData(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "file_name") var uri: String,
    @ColumnInfo(name = "result") var result: String,
    @ColumnInfo(name = "timestamp") var timestamp: Date
)