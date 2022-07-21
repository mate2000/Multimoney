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
    fun okHttpClient(certificateUtil: CertificateUtil): OkHttpClient {
        val logging = HttpLoggingInterceptor()

        logging.level = if (BuildConfig.DEBUG) {
            (HttpLoggingInterceptor.Level.BODY)
        } else {
            (HttpLoggingInterceptor.Level.BASIC)
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .sslSocketFactory(
                certificateUtil.getSSLContext(R.raw.ssl_certificate).socketFactory,
                certificateUtil.getX509TrustManager()
            )
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
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
    }

    @Singleton
    @Provides
    fun securityApi(@ApplicationContext context: Context, certificateUtil: CertificateUtil): SecurityApi =
        SecurityApi(apolloClient(context, SCHEMA_SECURITY, certificateUtil))

    @Singleton
    @Provides
    fun balanceApi(@ApplicationContext context: Context, certificateUtil: CertificateUtil): BalanceApi =
        BalanceApi(apolloClient(context, SCHEMA_BALANCES, certificateUtil))

    @Singleton
    @Provides
    fun creditApi(@ApplicationContext context: Context, certificateUtil: CertificateUtil): CreditApi =
        CreditApi(apolloClient(context, SCHEMA_CREDIT, certificateUtil))

    companion object {
        const val TIMEOUT = 30L
        const val APOLLO_PREFIX_DB = "multimoney_apollo_"
        const val APOLLO_SUFFIX_DB = "_db"
        const val SCHEMA_SECURITY = "security"
        const val SCHEMA_BALANCES = "balances"
        const val SCHEMA_CREDIT = "balances"
    }
}
