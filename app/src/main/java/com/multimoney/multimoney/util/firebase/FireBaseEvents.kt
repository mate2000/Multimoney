package com.multimoney.multimoney.util.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

sealed class FireBaseEvents(
    val event: String,
    val eventName: String,
    val parametersValue: String,
) {
    object Splash : FireBaseEvents("Splash", "Splash", "1001 - Splash")
    object OnboardingOne : FireBaseEvents("onboarding_1", "onboarding_1", "1002 - onboarding_1")
    object OnboardingTwo : FireBaseEvents("onboarding_2", "onboarding_2", "1003 - onboarding_2")
    object OnboardingThree : FireBaseEvents("onboarding_3", "onboarding_3", "1004 - onboarding_3")
    object SignUpOne : FireBaseEvents("signup_1", "signup_1", "2000 - signup_1")
    object SingUpTwo : FireBaseEvents("signup_2", "signup_2", "2001 - signup_2")
    object SignUpThree : FireBaseEvents("signup_3", "signup_3", "2002 - signup_3")
    object SignUpFour : FireBaseEvents("signup_4", "signup_4", "2003 - signup_4")
    object SignUpFive : FireBaseEvents("signup_5", "signup_5", "2004 - signup_5")
    object SignUpSuccess : FireBaseEvents("signup_exito", "signup_exito", "2005 - signup_exito")
    object LoginPassword :
        FireBaseEvents("login_password", "login_password", "3000 - login_password")

    object LoginBiometrics :
        FireBaseEvents("login_biometria", "login_biometria", "3001 - login_biometria")

    fun logEvent(context: Context) {
        val params = Bundle()
        params.putString(this.eventName, this.parametersValue)
        FirebaseAnalytics.getInstance(context).logEvent(this.event, params)
    }
}