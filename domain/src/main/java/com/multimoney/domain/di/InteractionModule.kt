package com.multimoney.domain.di

import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCase
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCaseImpl
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.interaction.security.MutationUserValidationUseCaseImpl
import com.multimoney.domain.repository.SecurityRepository
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
    fun provideMutationUserValidationUseCase(securityRepository: SecurityRepository): MutationUserValidationUseCase =
        MutationUserValidationUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationUpdateUserRegisterUseCase(securityRepository: SecurityRepository): MutationUpdateUserRegisterUseCase =
        MutationUpdateUserRegisterUseCaseImpl(securityRepository)
}
