import com.amazonaws.mobile.client.AWSMobileClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.multimoney.data.BuildConfig.CLIENT_ID
import com.multimoney.data.BuildConfig.CLIENT_SECRET
import com.multimoney.data.mapper.security.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.QueryGetTokenUseCase
import com.multimoney.domain.model.util.onSuccess
import kotlinx.coroutines.flow.first

class AuthorizationInterceptor(
    private val dataStorePreferences: DataStorePreferences,
    private val refreshTokenGraphqlApi: GraphqlApi
) : HttpInterceptor {

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        var token = dataStorePreferences.getAuthToken().first()
        val response = chain.proceed(
            request.newBuilder()
                .addHeader(AUTHORIZATION_HEADER, "$BEARER $token")
                .build()
        )

        return when (response.statusCode) {
            UNAUTHORIZED_CODE -> {
                try {
                    token = if (dataStorePreferences.isSignUpFlow().first()) {
                        val responseToken = refreshTokenGraphqlApi.queryGetToken().execute()
                        responseToken.data?.mapToDomainModel()?.accessToken.orEmpty()
                    } else {
                        AWSMobileClient.getInstance().tokens.idToken.tokenString
                    }
                    dataStorePreferences.setAuthToken(token)
                    chain.proceed(
                        request.newBuilder()
                            .addHeader(AUTHORIZATION_HEADER, "$BEARER $token")
                            .build()
                    )
                } catch (exception: Exception) {
                    dataStorePreferences.setAuthToken("")
                    dataStorePreferences.isForceShowBiometricPrompt(true)
                    response
                }
            }
            DUPLICATED_SESSION -> {
                dataStorePreferences.setAuthToken("")
                dataStorePreferences.isForceShowBiometricPrompt(true)
                dataStorePreferences.isSessionDuplicated(true)
                response
            }
            else -> response
        }
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER = "Bearer"
        const val UNAUTHORIZED_CODE = 401
        const val DUPLICATED_SESSION = 403
    }
}
