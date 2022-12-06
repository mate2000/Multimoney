package com.multimoney.multimoney.util

import android.content.Context
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class CognitoHelper @Inject constructor(@ApplicationContext private val context: Context) {

    fun initCognito() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(context)
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
