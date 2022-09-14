import com.amazonaws.mobile.client.AWSMobileClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.multimoney.data.util.DataStorePreferences
import kotlinx.coroutines.flow.first

class AuthorizationInterceptor(private val dataStorePreferences: DataStorePreferences) : HttpInterceptor {

    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {

        var token = dataStorePreferences.getAuthToken().first()
        val response = chain.proceed(request.newBuilder().addHeader(AUTHORIZATION_HEADER, "$BEARER $token").build())

        return if (response.statusCode == UNAUTHORIZED_CODE) {
            try {
                token = AWSMobileClient.getInstance().tokens.idToken.tokenString
                dataStorePreferences.setAuthToken(token)
                chain.proceed(request.newBuilder().addHeader(AUTHORIZATION_HEADER, "$BEARER $token").build())
            } catch (exeption: Exception) {
                dataStorePreferences.setAuthToken("")
                response
            }

        } else {
            response
        }
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER = "Bearer"
        const val UNAUTHORIZED_CODE = 401
    }
}