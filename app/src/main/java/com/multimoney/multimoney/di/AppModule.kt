package com.multimoney.multimoney.di

import android.content.Context
import com.multimoney.data.util.connectivity.Connectivity
import com.multimoney.data.util.connectivity.ConnectivityImpl
import com.multimoney.domain.interaction.accountsmart.SubscriptionAccountSmartContractUseCase
import com.multimoney.domain.interaction.credit.SubscriptionCreditContractEventUseCase
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.CreditSubscriptionManager
import com.multimoney.multimoney.presentation.ui.smart.origination.SmartSubscriptionManager
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.NotificationCommunicator
import com.multimoney.multimoney.presentation.util.NotificationCommunicatorImpl
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
    fun provideConnectivity(@ApplicationContext context: Context): Connectivity =
        ConnectivityImpl(context)

    @Singleton
    @Provides
    fun provideFireBaseEventHelper(@ApplicationContext context: Context) =
        FireBaseEventHelper(context)

    @Singleton
    @Provides
    fun provideCountDownTimer() = MMCountDownTimer()

    @Singleton
    @Provides
    fun provideNotificationCommunicator(): NotificationCommunicator = NotificationCommunicatorImpl()

    @Singleton
    @Provides
    fun provideCreditSubscriptionManager(subscriptionCreditContractEventUseCase: SubscriptionCreditContractEventUseCase) =
        CreditSubscriptionManager(subscriptionCreditContractEventUseCase)

    @Singleton
    @Provides
    fun provideSmartSubscriptionManager(subscriptionAccountSmartContractUseCase: SubscriptionAccountSmartContractUseCase) =
        SmartSubscriptionManager(subscriptionAccountSmartContractUseCase)
}
