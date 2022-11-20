package com.multimoney.multimoney.di

import android.content.Context
import com.multimoney.data.util.connectivity.Connectivity
import com.multimoney.data.util.connectivity.ConnectivityImpl
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.util.firebase.FireBaseEventHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    // Binds
    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideConnectivity(@ApplicationContext context: Context): Connectivity = ConnectivityImpl(context)

    @Singleton
    @Provides
    fun provideFireBaseEventHelper(@ApplicationContext context: Context) = FireBaseEventHelper(context)

    @Singleton
    @Provides
    fun provideCountDownTimer() = MMCountDownTimer()
}
