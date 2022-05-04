package com.multimoney.multimoney.util.cryptography

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.multimoney.data.util.GsonHelper
import com.multimoney.multimoney.util.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

/**
 * To get an instance of this private CryptographyManagerImpl class, use the top-level function
 * fun CryptographyManager(): CryptographyManager = CryptographyManagerImpl()
 */
class CryptographyManagerImpl @Inject constructor(
    private val gsonHelper: GsonHelper,
    private val preferences: Preferences
) :
    CryptographyManager {

    override fun getInitializedCipherForEncryption(keyName: String): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher
    }

    override fun getInitializedCipherForDecryption(
        keyName: String,
        initializationVector: ByteArray
    ): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, initializationVector))
        return cipher
    }

    override fun encryptData(userCredentials: UserCredentials, cipher: Cipher): CiphertextWrapper {
        val json = gsonHelper.convertToString(userCredentials)
        val ciphertext = cipher.doFinal(json.toByteArray(Charset.forName(CHARACTER_ENCODING)))
        return CiphertextWrapper(ciphertext, cipher.iv)
    }

    override fun decryptData(ciphertext: ByteArray, cipher: Cipher): UserCredentials {
        val biteArray = cipher.doFinal(ciphertext)
        val json = String(biteArray, Charset.forName(CHARACTER_ENCODING))
        return gsonHelper.convertToData(
            json,
            UserCredentials::class.java
        )
    }

    private fun getCipher(): Cipher {
        val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
        return Cipher.getInstance(transformation)
    }

    private fun getOrCreateSecretKey(keyName: String): SecretKey {
        // If Secretkey was previously created for that keyName, then grab and return it.
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null) // Keystore must be loaded before it can be accessed
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        // if you reach here, then a new SecretKey must be generated for that keyName
        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        paramsBuilder.apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }

    override fun persistCiphertextWrapperToPreferences(
        ciphertextWrapper: CiphertextWrapper
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            preferences.setCiphertextWrapper(gsonHelper.convertToString(ciphertextWrapper))
        }
    }

    override fun getCiphertextWrapperFromPreferences(onCiphertextWrapperSuccess: (ciphertextWrapper: CiphertextWrapper) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            preferences.getCiphertextWrapper().collect { ciphertextWrapper ->
                onCiphertextWrapperSuccess(
                    gsonHelper.convertToData(
                        ciphertextWrapper,
                        CiphertextWrapper::class.java
                    )
                )
            }
        }
    }

    override fun eraseCypherTextFromSharedPreferences() {
        CoroutineScope(Dispatchers.IO).launch {
            preferences.removeCiphertextWrapper()
        }
    }

    companion object {
        private const val CHARACTER_ENCODING = "UTF-8"
        private const val KEY_SIZE = 256
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    }
}