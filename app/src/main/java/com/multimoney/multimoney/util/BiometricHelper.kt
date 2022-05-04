package com.multimoney.multimoney.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.util.cryptography.CryptographyManagerImpl
import javax.inject.Inject

class BiometricHelper @Inject constructor(private val cryptographyManagerImpl: CryptographyManagerImpl) {

    private fun setBiometricPromptInfo(
        title: String,
        subtitle: String,
        description: String,
        negative: String
    ) =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            // Authenticate without requiring the user to press a "confirm"
            // button after satisfying the biometric check
            .setConfirmationRequired(false)
            .setNegativeButtonText(negative)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()

    private fun initBiometricPrompt(
        activity: FragmentActivity,
        processError: (Int, CharSequence) -> Unit = { _: Int, _: CharSequence -> {} },
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
    ) = BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(activity),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                processError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                processSuccess(result)
            }
        })

    fun showBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        negative: String,
        activity: FragmentActivity,
        processError: (Int, CharSequence) -> Unit = { _: Int, _: CharSequence -> {} },
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
    ) = initBiometricPrompt(
        activity = activity,
        processError = processError,
        processSuccess = processSuccess
    ).authenticate(
        setBiometricPromptInfo(
            title = title,
            subtitle = subtitle,
            description = description,
            negative = negative
        ),
        BiometricPrompt.CryptoObject(
            cryptographyManagerImpl.getInitializedCipherForEncryption(BuildConfig.SECRET_KEY_NAME)
        )
    )

    fun isBiometricAvailable(context: Context) = BiometricManager.from(context)
        .canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
}