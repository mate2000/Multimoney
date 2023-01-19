package com.multimoney.data.di

import AuthorizationInterceptor
import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.multimoney.data.BuildConfig
import com.multimoney.data.R
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.util.CertificateUtil
import com.multimoney.data.util.DataStorePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

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
        certificateUtil: CertificateUtil
    ): OkHttpClient {
        val provider = OkHttpClient.Builder()
        provider
            .sslSocketFactory(
                certificateUtil.getSSLContext(R.raw.ssl_certificate).socketFactory,
                certificateUtil.getX509TrustManager()
            )
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        return provider.build()
    }

    private fun apolloAuthorizedClientProvider(
        @ApplicationContext context: Context,
        certificateUtil: CertificateUtil,
        dataStorePreferences: DataStorePreferences
    ): ApolloClient {
        val sqlNormalizedCacheFactory =
            SqlNormalizedCacheFactory(context, APOLLO_DB)

        return ApolloClient.Builder()
            .serverUrl(BuildConfig.API_URL + SCHEMA_GRAPHQL)
            .webSocketServerUrl(BuildConfig.WEBSOCKET_URL + SCHEMA_GRAPHQL)
            .addHttpInterceptor(AuthorizationInterceptor(dataStorePreferences))
            .normalizedCache(sqlNormalizedCacheFactory)
            .okHttpClient(
                authOkHttpClientProvider(
                    certificateUtil
                )
            )
            .build()
    }

    @Singleton
    @Provides
    fun graphqlApi(
        @ApplicationContext context: Context,
        util: CertificateUtil,
        preferences: DataStorePreferences
    ): GraphqlApi =
        GraphqlApi(
            apolloAuthorizedClient = apolloAuthorizedClientProvider(
                context,
                util,
                preferences
            )
        )

    companion object {
        const val TIMEOUT = 120L
        const val APOLLO_DB = "multimoney_apollo_graphql_db"
        const val SCHEMA_GRAPHQL = "graphql"
    }
}
