package com.multimoney.multimoney.util.cryptography

import javax.crypto.Cipher

/**
 * Handles encryption and decryption
 */
interface CryptographyManager {
    fun getInitializedCipherForEncryption(keyName: String): Cipher

    fun getInitializedCipherForDecryption(keyName: String, initializationVector: ByteArray): Cipher

    /**
     * The Cipher created with [getInitializedCipherForEncryption] is used here
     */
    fun encryptData(userCredentials: UserCredentials, cipher: Cipher): CiphertextWrapper

    /**
     * The Cipher created with [getInitializedCipherForDecryption] is used here
     */
    fun decryptData(ciphertext: ByteArray, cipher: Cipher): UserCredentials
    fun persistCiphertextWrapperToPreferences(ciphertextWrapper: CiphertextWrapper)
    fun getCiphertextWrapperFromPreferences(onCiphertextWrapperSuccess: (ciphertextWrapper: CiphertextWrapper) -> Unit)
    fun eraseCypherTextFromSharedPreferences()
}