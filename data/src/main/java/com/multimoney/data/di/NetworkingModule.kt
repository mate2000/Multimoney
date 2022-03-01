package com.multimoney.data.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.multimoney.data.BuildConfig
import com.multimoney.data.networking.MultimoneyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    @Singleton
    @Provides
    fun apolloClient(@ApplicationContext context: Context): ApolloClient {
        val logging = HttpLoggingInterceptor()

        logging.level = if (BuildConfig.DEBUG) {
            (HttpLoggingInterceptor.Level.BODY)
        } else {
            (HttpLoggingInterceptor.Level.BASIC)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()

        val sqlNormalizedCacheFactory = SqlNormalizedCacheFactory(context, APOLLO_DB)

        return ApolloClient.Builder()
            .serverUrl(BuildConfig.API_URL)
            .normalizedCache(sqlNormalizedCacheFactory)
            .okHttpClient(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun multimoneyApi(@ApplicationContext context: Context): MultimoneyApi =
        MultimoneyApi(apolloClient(context))

    companion object {
        const val TIMEOUT = 30L
        const val APOLLO_DB = "multimoney_apollo_db"
    }
}
