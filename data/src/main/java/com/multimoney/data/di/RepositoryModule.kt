package com.multimoney.data.di

import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.repository.BalanceRepositoryImpl
import com.multimoney.data.repository.CreditRepositoryImpl
import com.multimoney.data.repository.CryptoRepositoryImpl
import com.multimoney.data.repository.MultimoneyVisaRepositoryImpl
import com.multimoney.data.repository.SecurityRepositoryImpl
import com.multimoney.data.repository.SmartAccountRepositoryImpl
import com.multimoney.data.repository.VirtualCardRepositoryImpl
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
    fun provideSecurityRepository(graphqlApi: GraphqlApi) =
        SecurityRepositoryImpl(graphqlApi)

    @Provides
    @Singleton
    fun provideBalanceRepository(graphqlApi: GraphqlApi) =
        BalanceRepositoryImpl(graphqlApi)

    @Provides
    @Singleton
    fun provideCreditRepository(graphqlApi: GraphqlApi) =
        CreditRepositoryImpl(graphqlApi)

    @Provides
    @Singleton
    fun provideAccountSmartRepository(graphqlApi: GraphqlApi) =
        SmartAccountRepositoryImpl(graphqlApi)

    @Provides
    @Singleton
    fun provideMultimoneyVisaRepository(graphqlApi: GraphqlApi) =
        MultimoneyVisaRepositoryImpl(graphqlApi)

    @Provides
    @Singleton
    fun provideCryptoRepository(graphqlApi: GraphqlApi) =
        CryptoRepositoryImpl(graphqlApi)

    @Provides
    @Singleton
    fun provideVirtualCardRepository(graphqlApi: GraphqlApi) =
        VirtualCardRepositoryImpl(graphqlApi)
}
