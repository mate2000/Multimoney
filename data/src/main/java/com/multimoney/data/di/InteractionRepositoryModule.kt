package com.multimoney.data.di

import com.multimoney.data.repository.BalanceRepositoryImpl
import com.multimoney.data.repository.CreditRepositoryImpl
import com.multimoney.data.repository.MultimoneyVisaRepositoryImpl
import com.multimoney.data.repository.SecurityRepositoryImpl
import com.multimoney.data.repository.SmartAccountRepositoryImpl
import com.multimoney.domain.repository.BalanceRepository
import com.multimoney.domain.repository.CreditRepository
import com.multimoney.domain.repository.MultimoneyVisaRepository
import com.multimoney.domain.repository.SecurityRepository
import com.multimoney.domain.repository.SmartAccountRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class InteractionRepositoryModule {
    @Binds
    abstract fun bindSecurityRepository(securityRepositoryImpl: SecurityRepositoryImpl): SecurityRepository

    @Binds
    abstract fun bindBalanceRepository(balanceRepositoryImpl: BalanceRepositoryImpl): BalanceRepository

    @Binds
    abstract fun bindCreditRepository(creditRepositoryImpl: CreditRepositoryImpl): CreditRepository

    @Binds
    abstract fun bindSmartRepository(smartAccountRepositoryImpl: SmartAccountRepositoryImpl): SmartAccountRepository

    @Binds
    abstract fun bindMultimoneyRepository(multimoneyVisaRepositoryImpl: MultimoneyVisaRepositoryImpl): MultimoneyVisaRepository
}
