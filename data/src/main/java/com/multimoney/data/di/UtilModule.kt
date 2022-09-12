package com.multimoney.data.di

import com.multimoney.data.util.authenticator.Builder
import com.multimoney.data.util.authenticator.Callback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilModule {
    @Provides
    @Singleton
    fun provideFourOhOneAuthenticatorBuilder(callback: Callback) =
        Builder(callback)
}
