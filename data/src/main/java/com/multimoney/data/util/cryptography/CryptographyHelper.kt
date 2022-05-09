package com.multimoney.data.util.cryptography

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptographyHelper @Inject constructor() {

    private val charset by lazy {
        Charset.forName(CHARACTER_ENCODING)
    }

    private fun getCipher() =
        Cipher.getInstance("${KEY_ALGORITHM_AES}/${BLOCK_MODE_GCM}/${ENCRYPTION_PADDING_NONE}")

    fun getInitializedCipherForEncryption(
        keyAlias: String,
        isUserAuthenticationRequired: Boolean = false
    ): Cipher {
        val cipher = getCipher()
        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateSecretKey(keyAlias, isUserAuthenticationRequired)
        )
        return cipher
    }

    fun getInitializedCipherForDecryption(
        keyAlias: String,
        initializationVector: ByteArray,
        isUserAuthenticationRequired: Boolean = false
    ): Cipher {
        val cipher = getCipher()
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(keyAlias, isUserAuthenticationRequired),
            GCMParameterSpec(SPEC_LEN, initializationVector)
        )
        return cipher
    }

    fun encryptData(
        keyAlias: String,
        text: String
    ): CiphertextWrapper {
        val cipher = getInitializedCipherForEncryption(keyAlias)
        return CiphertextWrapper(cipher.doFinal(text.toByteArray(charset)), cipher.iv)
    }

    fun encryptData(text: String, cipher: Cipher) =
        CiphertextWrapper(cipher.doFinal(text.toByteArray(charset)), cipher.iv)

    fun decryptData(
        keyAlias: String, encryptedData: ByteArray,
        initializationVector: ByteArray
    ) =
        getInitializedCipherForDecryption(keyAlias, initializationVector).doFinal(encryptedData)
            .toString(charset)

    fun decryptData(encryptedData: ByteArray, cipher: Cipher) =
        cipher.doFinal(encryptedData).toString(charset)

    private fun getOrCreateSecretKey(
        keyAlias: String,
        isUserAuthenticationRequired: Boolean = false
    ): SecretKey {
        // If Secretkey was previously created for that keyName, then grab and return it.
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null) // Keystore must be loaded before it can be accessed
        keyStore.getKey(keyAlias, null)?.let { return it as SecretKey }

        // if you reach here, then a new SecretKey must be generated for that keyName
        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        paramsBuilder.apply {
            setBlockModes(BLOCK_MODE_GCM)
            setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(isUserAuthenticationRequired)
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }

    companion object {
        private const val CHARACTER_ENCODING = "UTF-8"
        private const val KEY_SIZE = 256
        private const val SPEC_LEN = 128
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }
}