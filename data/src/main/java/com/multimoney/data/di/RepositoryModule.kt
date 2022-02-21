package com.multimoney.data.di

import com.multimoney.data.database.dao.TestDao
import com.multimoney.data.networking.MultimoneyApi
import com.multimoney.data.repository.LaunchRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideLaunchRepository(multimoneyApi: MultimoneyApi, testDao: TestDao) =
        LaunchRepositoryImpl(multimoneyApi, testDao)
}
