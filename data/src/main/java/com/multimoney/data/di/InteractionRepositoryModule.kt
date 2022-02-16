package com.multimoney.data.di

import com.multimoney.data.repository.LaunchRepositoryImpl
import com.multimoney.domain.repository.LaunchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class InteractionRepositoryModule {
    @Binds
    abstract fun bindLaunchRepository(launchRepositoryImpl: LaunchRepositoryImpl): LaunchRepository
}
