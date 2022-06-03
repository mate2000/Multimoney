package com.multimoney.data.di

import com.multimoney.data.networking.BalanceApi
import com.multimoney.data.networking.SecurityApi
import com.multimoney.data.repository.BalanceRepositoryImpl
import com.multimoney.data.repository.SecurityRepositoryImpl
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
    fun provideSecurityRepository(securityApi: SecurityApi) =
        SecurityRepositoryImpl(securityApi)

    @Provides
    @Singleton
    fun provideBalanceRepository(balanceApi: BalanceApi) =
        BalanceRepositoryImpl(balanceApi)
}
