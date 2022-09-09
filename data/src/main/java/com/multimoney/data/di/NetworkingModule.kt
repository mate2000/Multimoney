package com.multimoney.data.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.multimoney.data.BuildConfig
import com.multimoney.data.R
import com.multimoney.data.networking.BalanceApi
import com.multimoney.data.networking.CreditApi
import com.multimoney.data.networking.SecurityApi
import com.multimoney.data.util.CertificateUtil
import com.multimoney.data.util.DataStorePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    private val loggingInterceptor: HttpLoggingInterceptor by lazy { loggingInterceptorProvider() }

    private fun loggingInterceptorProvider(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) {
            (HttpLoggingInterceptor.Level.BODY)
        } else {
            (HttpLoggingInterceptor.Level.BASIC)
        }
        return logging
    }

    private fun authOkHttpClientProvider(
        certificateUtil: CertificateUtil,
        dataStorePreferences: DataStorePreferences
    ): OkHttpClient {
        val provider = OkHttpClient.Builder()
        provider
            .addNetworkInterceptor { chain ->
                val token = runBlocking {
                    dataStorePreferences.getAuthToken().first()
                }
                val request = chain.request().newBuilder()
                    .addHeader(
                        AUTHORIZATION_HEADER,
                        "Bearer $token"
                    )
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        if (BuildConfig.BUILD_TYPE != DEBUG) {
            provider.sslSocketFactory(
                certificateUtil.getSSLContext(R.raw.ssl_certificate).socketFactory,
                certificateUtil.getX509TrustManager()
            )
        }
        return provider.build()
    }

    private fun apolloAuthorizedClientProvider(
        @ApplicationContext context: Context,
        schema: String,
        certificateUtil: CertificateUtil,
        dataStorePreferences: DataStorePreferences
    ): ApolloClient {
        val sqlNormalizedCacheFactory =
            SqlNormalizedCacheFactory(context, APOLLO_PREFIX_DB + schema + APOLLO_SUFFIX_DB)

        return ApolloClient.Builder()
            .serverUrl(BuildConfig.API_URL + schema)
            .normalizedCache(sqlNormalizedCacheFactory)
            .okHttpClient(
                authOkHttpClientProvider(
                    certificateUtil,
                    dataStorePreferences = dataStorePreferences
                )
            )
            .build()
    }

    @Singleton
    @Provides
    fun securityApi(
        @ApplicationContext context: Context,
        util: CertificateUtil,
        preferences: DataStorePreferences
    ): SecurityApi =
        SecurityApi(
            apolloAuthorizedClient = apolloAuthorizedClientProvider(
                context,
                SCHEMA_SECURITY,
                util,
                preferences
            )
        )

    @Singleton
    @Provides
    fun balanceApi(
        @ApplicationContext context: Context,
        util: CertificateUtil,
        preferences: DataStorePreferences
    ): BalanceApi =
        BalanceApi(apolloAuthorizedClientProvider(context, SCHEMA_BALANCES, util, preferences))

    @Singleton
    @Provides
    fun creditApi(
        @ApplicationContext context: Context,
        util: CertificateUtil,
        preferences: DataStorePreferences
    ): CreditApi =
        CreditApi(apolloAuthorizedClientProvider(context, SCHEMA_CREDIT, util, preferences))

    companion object {
        const val DEBUG = "debug"
        const val AUTHORIZATION_HEADER = "Authorization"
        const val TIMEOUT = 120L
        const val APOLLO_PREFIX_DB = "multimoney_apollo_"
        const val APOLLO_SUFFIX_DB = "_db"
        const val SCHEMA_SECURITY = "security"
        const val SCHEMA_BALANCES = "balances"
        const val SCHEMA_CREDIT = "credit"
    }
}


