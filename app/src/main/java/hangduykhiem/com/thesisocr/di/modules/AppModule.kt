package hangduykhiem.com.thesisocr.di.modules

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import hangduykhiem.com.thesisocr.data.ResultDatabase
import hangduykhiem.com.thesisocr.data.dao.OcrResultDAO
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(val context: Context) {

    private val resultDatabase = Room.databaseBuilder(
        context,
        ResultDatabase::class.java, "result-db"
    ).build()

    companion object {
        const val APP_CONTEXT = "appContext"
    }

    @Provides
    @Named(APP_CONTEXT)
    internal fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    internal fun provideResultDatabase(): ResultDatabase {
        return resultDatabase
    }

    @Provides
    @Singleton
    internal fun provideProductDAO(resultDatabase: ResultDatabase): OcrResultDAO {
        return resultDatabase.ocrDao()
    }

}