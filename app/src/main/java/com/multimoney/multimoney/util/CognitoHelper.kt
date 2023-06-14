package com.multimoney.multimoney.util

import android.content.Context
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.multimoney.multimoney.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import timber.log.Timber

class CognitoHelper @Inject constructor(@ApplicationContext private val context: Context) {

    fun initCognito() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            val configuration = AmplifyConfiguration.builder(context, R.raw.amplifyconfiguration).build()
            Amplify.configure(configuration, context)
            Timber.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Timber.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
    }

    fun signOut(signOutError: (exception: AuthException) -> Unit) {
        Amplify.Auth.signOut({
            Timber.d("signOut Success")
        }, signOutError)
    }
}
