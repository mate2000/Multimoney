package com.multimoney.multimoney.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.multimoney.data.BuildConfig.DATA_STORE_BIOMETRIC_KEY
import com.multimoney.data.util.cryptography.CryptographyHelper
import javax.inject.Inject

class BiometricHelper @Inject constructor(
    private val cryptographyHelper: CryptographyHelper
) {

    private fun setBiometricPromptInfo(
        title: String,
        description: String,
        negative: String
    ) =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
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
        description: String,
        negative: String,
        activity: FragmentActivity,
        processError: (Int, CharSequence) -> Unit = { _: Int, _: CharSequence -> {} },
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        initializationVector: ByteArray? = null
    ) = initBiometricPrompt(
        activity = activity,
        processError = processError,
        processSuccess = processSuccess
    ).authenticate(
        setBiometricPromptInfo(
            title = title,
            description = description,
            negative = negative
        ),
        initializationVector?.let {
            BiometricPrompt.CryptoObject(
                cryptographyHelper.getInitializedCipherForDecryption(
                    DATA_STORE_BIOMETRIC_KEY,
                    it,
                    true
                )
            )
        } ?: run {
            BiometricPrompt.CryptoObject(
                cryptographyHelper.getInitializedCipherForEncryption(DATA_STORE_BIOMETRIC_KEY, true)
            )
        }
    )

    fun isBiometricAvailable(context: Context) = BiometricManager.from(context)
        .canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
}