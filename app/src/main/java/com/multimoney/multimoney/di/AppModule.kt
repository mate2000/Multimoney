package com.multimoney.multimoney.di

import com.multimoney.data.util.connectivity.Connectivity
import com.multimoney.data.util.connectivity.ConnectivityImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    // Binds
    @ExperimentalCoroutinesApi
    @Singleton
    @Binds
    abstract fun bindConnectivity(connectivityImpl: ConnectivityImpl): Connectivity
}
