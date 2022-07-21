package com.multimoney.domain.di

import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceUseCaseImpl
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCaseImpl
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCase
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCaseImpl
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCase
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCaseImpl
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.interaction.security.MutationUserValidationUseCaseImpl
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCase
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCaseImpl
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCaseImpl
import com.multimoney.domain.repository.BalanceRepository
import com.multimoney.domain.repository.SecurityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InteractionModule {

    // Security

    @Provides
    @Singleton
    fun provideMutationUserValidationUseCase(securityRepository: SecurityRepository): MutationUserValidationUseCase =
        MutationUserValidationUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationUpdateUserRegisterUseCase(securityRepository: SecurityRepository): MutationUpdateUserRegisterUseCase =
        MutationUpdateUserRegisterUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryValidationSecurityUseCase(securityRepository: SecurityRepository): QueryValidationSecurityUseCase =
        QueryValidationSecurityUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryDataInformationUseCase(securityRepository: SecurityRepository): QueryDataInformationClientUseCase =
        QueryDataInformationClientUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationSendPinProcessUseCase(securityRepository: SecurityRepository): MutationSendPinProcessUseCase =
        MutationSendPinProcessUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationOnFidoInitialProcess(securityRepository: SecurityRepository): MutationOnFidoInitialProcessUseCase =
        MutationOnFidoInitialProcessUseCaseImpl(securityRepository)

    // Balance

    @Provides
    @Singleton
    fun provideQueryBalanceUseCase(balanceRepository: BalanceRepository): QueryBalanceUseCase =
        QueryBalanceUseCaseImpl(balanceRepository)

    // Credit
}
