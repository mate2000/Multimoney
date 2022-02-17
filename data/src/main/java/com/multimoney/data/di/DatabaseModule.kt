package com.multimoney.data.di

import android.content.Context
import androidx.room.Room
import com.multimoney.data.BuildConfig
import com.multimoney.data.database.MultimoneyDatabase
import com.multimoney.data.database.MultimoneyDatabase.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): MultimoneyDatabase {
        val roomInstance = Room.databaseBuilder(
            context.applicationContext,
            MultimoneyDatabase::class.java,
            DATABASE_NAME
        )
        return when (BuildConfig.BUILD_TYPE) {
            RELEASE -> {
                roomInstance.build()
            }
            else -> {
                roomInstance.fallbackToDestructiveMigration().build()
            }
        }
    }

    @Provides
    fun provideUserInitialInfoDao(db: MultimoneyDatabase) = db.testDao()

    companion object {
        const val RELEASE = "release"
    }
}
