package com.multimoney.multimoney.di

import com.multimoney.data.util.connectivity.Connectivity
import com.multimoney.data.util.connectivity.ConnectivityImpl
import com.multimoney.multimoney.util.preference.Preference
import com.multimoney.multimoney.util.preference.PreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @ExperimentalCoroutinesApi
    @Singleton
    @Binds
    abstract fun bindConnectivity(connectivityImpl: ConnectivityImpl): Connectivity

    @Singleton
    @Binds
    abstract fun bindPreferences(preferenceImpl: PreferenceImpl): Preference
}
