package com.multimoney.data.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.multimoney.data.BuildConfig
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) {

    private val okHttpClient: OkHttpClient by lazy { okHttpClientProvider() }

    private val authOkHttpClient: OkHttpClient by lazy { authOkHttpClientProvider() }

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

    private fun okHttpClientProvider() = OkHttpClient.Builder()
        .addNetworkInterceptor { chain ->
            val request = chain.request().newBuilder()
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

    private fun authOkHttpClientProvider() = OkHttpClient.Builder()
        .addNetworkInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader(AUTHORIZATION_HEADER, "Bearer ${dataStorePreferences.getAuthToken()}")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

   /* @Singleton
    @Provides
    fun okHttpClient(certificateUtil: CertificateUtil): OkHttpClient {
        val logging = HttpLoggingInterceptor()

        logging.level = if (BuildConfig.DEBUG) {
            (HttpLoggingInterceptor.Level.BODY)
        } else {
            (HttpLoggingInterceptor.Level.BASIC)
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
//            .sslSocketFactory(
//                certificateUtil.getSSLContext(R.raw.ssl_certificate).socketFactory,
//                certificateUtil.getX509TrustManager()
//            )
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }*/

    /* @Singleton
     @Provides
     fun apolloClient(
         @ApplicationContext context: Context,
         schema: String,
         certificateUtil: CertificateUtil
     ): ApolloClient {

         val sqlNormalizedCacheFactory =
             SqlNormalizedCacheFactory(context, APOLLO_PREFIX_DB + schema + APOLLO_SUFFIX_DB)

         return ApolloClient.Builder()
             .serverUrl(BuildConfig.API_URL + schema)
             .normalizedCache(sqlNormalizedCacheFactory)
             .okHttpClient(okHttpClient(certificateUtil))
             .build()
     }*/

    private fun apolloBasicClientProvider(
        @ApplicationContext context: Context,
        schema: String
    ): ApolloClient {

        val sqlNormalizedCacheFactory =
            SqlNormalizedCacheFactory(context, APOLLO_PREFIX_DB + schema + APOLLO_SUFFIX_DB)

        return ApolloClient.Builder()
            .serverUrl(BuildConfig.API_URL + schema)
            .normalizedCache(sqlNormalizedCacheFactory)
            .okHttpClient(okHttpClient)
            .build()
    }


    private fun apolloAuthorizedClientProvider(
        @ApplicationContext context: Context,
        schema: String
    ): ApolloClient {
        val sqlNormalizedCacheFactory =
            SqlNormalizedCacheFactory(context, APOLLO_PREFIX_DB + schema + APOLLO_SUFFIX_DB)

        return ApolloClient.Builder()
            .serverUrl(BuildConfig.API_URL + schema)
            .normalizedCache(sqlNormalizedCacheFactory)
            .okHttpClient(authOkHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun securityApi(
        @ApplicationContext context: Context
    ): SecurityApi =
        SecurityApi(
            apolloBasicClient = apolloBasicClientProvider(
                context,
                SCHEMA_SECURITY
            ),
            apolloAuthorizedClient = apolloAuthorizedClientProvider(
                context,
                SCHEMA_SECURITY
            )
        )

    @Singleton
    @Provides
    fun balanceApi(
        @ApplicationContext context: Context
    ): BalanceApi =
        BalanceApi(apolloAuthorizedClientProvider(context, SCHEMA_BALANCES))

    @Singleton
    @Provides
    fun creditApi(
        @ApplicationContext context: Context
    ): CreditApi =
        CreditApi(apolloAuthorizedClientProvider(context, SCHEMA_CREDIT))

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val TIMEOUT = 120L
        const val APOLLO_PREFIX_DB = "multimoney_apollo_"
        const val APOLLO_SUFFIX_DB = "_db"
        const val SCHEMA_SECURITY = "security"
        const val SCHEMA_BALANCES = "balances"
        const val SCHEMA_CREDIT = "credit"
    }
}


