package com.multimoney.data.util.authenticator

import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoJWTParser
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.data.di.NetworkingModule
import com.multimoney.data.util.DataStorePreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Created by rodrigomiranda on 3/3/20.
 * Applaudo Studios
 *
 * Re-authentication via an Authenticator
 *
 * Source:
 * @see https://github.com/Commit451/FourOhOne
 */
class FourOhOneAuthenticator(builder: Builder) : Authenticator {

    private var ignoreHeader = builder.ignoreHeader
    private var callback = builder.callback
    private var retryCount = builder.retryCount

    override fun authenticate(route: Route?, response: Response): Request? {
        return when {
            response.request.header(ignoreHeader) != null ->
                // ignore this call (probably login)
                null
            responseCount(response) >= retryCount -> {
                route?.let {
                    response.let { callback.onUnableToAuthenticate(route, response) }
                }
                null
            }
            else -> {
                route?.let {
                    response.let { callback.onReAuthenticate(route, response) }
                }
            }
        }
    }

    /**
     * Tail recursive function that iterates into inner prior responses to count request attempts.
     * @param response the response of the 401 failure
     * @param result (optional) the attempt counter.

     * @return number of request attempts.
     */
    private tailrec fun responseCount(response: Response?, result: Int = 0): Int =
        if (response?.priorResponse == null) result else responseCount(
            response.priorResponse,
            result + 1
        )

    companion object {

        /**
         * Add this header on any requests that you want to be ignored by
         * the [FourOhOneAuthenticator]. Value of the header is ignored
         */
        const val HEADER_IGNORE = "FourOhOneAuthenticatorIgnore"

        /**
         * Max automatic reattempts before stopping
         */
        const val DEFAULT_RETRY_COUNT = 1

        fun newInstance(preferences: DataStorePreferences) = Builder(object : Callback {
            override fun onReAuthenticate(route: Route, response: Response): Request {
                val requestBuilder = response.request.newBuilder()

                Amplify.Auth.fetchAuthSession({ authSessionSuccess ->
                    val session = authSessionSuccess as AWSCognitoAuthSession
                    when (session.identityId.type) {
                        AuthSessionResult.Type.SUCCESS -> {
                            // Get user attributes in order to save user name for welcome message
                            val payload = CognitoJWTParser.getPayload(session.userPoolTokens.value?.idToken)
                            val token = session.userPoolTokens.value?.idToken
                            val access = session.userPoolTokens.value?.accessToken
                            val refresh = session.userPoolTokens.value?.refreshToken

                            runBlocking {
                                access?.let {
                                    preferences.setAuthToken(it)
                                    requestBuilder.header(
                                        NetworkingModule.AUTHORIZATION_HEADER,
                                        "Bearer $access"
                                    )
                                } ?: run {
                                    preferences.setAuthToken("")
                                }
                            }
                        }
                        AuthSessionResult.Type.FAILURE -> {}
                    }
                }, {

                })
                return requestBuilder.build()
            }

            override fun onUnableToAuthenticate(route: Route, response: Response) {
                /*
                * This function is empty, because the behavior when the authentication fails is handled
                * above in the kotlin.run code block.
                * */
            }
        }).build()
    }
}

/**
 * Builder for [FourOhOneAuthenticator]
 */
class Builder
/**
 * New builder for [FourOhOneAuthenticator] with the required [Callback]
 * @param callback callback
 */
    (val callback: Callback) {
    var retryCount = FourOhOneAuthenticator.DEFAULT_RETRY_COUNT
    var ignoreHeader = FourOhOneAuthenticator.HEADER_IGNORE

    /**
     * The number of times to retry the call before calling [Callback.onUnableToAuthenticate]
     * @param retryCount number of retries. Defaults to 3
     * @return builder
     */
    fun retryCount(retryCount: Int): Builder {
        this.retryCount = retryCount
        return this
    }

    /**
     * Calls that contain this header will be ignored by [FourOhOneAuthenticator].
     * This is useful for ignoring expected 401 calls, such as login with incorrect credentials.
     * Value of the header is ignored
     * @param ignoreHeader the name of the header
     * @return builder
     */
    fun ignoreHeader(ignoreHeader: String): Builder {
        this.ignoreHeader = ignoreHeader
        return this
    }

    /**
     * Build the [FourOhOneAuthenticator]
     * @return the newly built authenticator
     */
    fun build(): FourOhOneAuthenticator = FourOhOneAuthenticator(this)
}

/**
 * Callbacks that [FourOhOneAuthenticator] calls to coordinate re auth and failed auth
 */
interface Callback {

    /**
     * A 401 error has occurred, and an attempt should be made to re authenticate with the server.
     * Note that this is called on a background thread
     * @param route the route of the 401 failure
     * @param response the response of the 401 failure
     * @return a built request that has the correct authentication. Otherwise
     * Null if re-authentication is not possible
     */
    fun onReAuthenticate(route: Route, response: Response): Request

    /**
     * Authentication has been attempted the max number of times, and seems to be impossible.
     * This is a good time to clear credentials, and prompt the user to sign in again.
     * Note that this is called on a background thread
     * @param route the route of the 401 failure
     * @param response the response of the 401 failure
     */
    fun onUnableToAuthenticate(route: Route, response: Response)
}