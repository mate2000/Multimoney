package com.multimoney.domain.di

import com.multimoney.domain.interaction.GetLaunchListUseCase
import com.multimoney.domain.interaction.GetLaunchListUseCaseImpl
import com.multimoney.domain.repository.LaunchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InteractionModule {
    @Provides
    @Singleton
    fun provideGetLaunchListUseCase(launchRepository: LaunchRepository): GetLaunchListUseCase =
        GetLaunchListUseCaseImpl(launchRepository)
}
