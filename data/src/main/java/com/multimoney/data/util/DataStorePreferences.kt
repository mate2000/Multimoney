package com.multimoney.data.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.multimoney.data.base.BaseDataStorePreferences
import com.multimoney.data.util.cryptography.CryptographyHelper
import kotlinx.coroutines.flow.Flow
import javax.crypto.Cipher
import javax.inject.Inject

class DataStorePreferences @Inject constructor(
    dataStore: DataStore<Preferences>,
    gsonHelper: GsonHelper,
    cryptographyHelper: CryptographyHelper
) : BaseDataStorePreferences(
    dataStore = dataStore,
    gsonHelper = gsonHelper,
    cryptographyHelper = cryptographyHelper
) {

    suspend fun setUserEmail(userEmail: String) =
        setSecuredData(USER_EMAIL_KEY, userEmail)

    fun getUserEmail(): Flow<String> = getSecuredData(USER_EMAIL_KEY, "")

    suspend fun setUserPassword(userPassword: String, cipher: Cipher) =
        setSecuredData(USER_PASSWORD_KEY, userPassword, cipher)

    fun getUserPassword(cipher: Cipher): Flow<String> =
        getSecuredData(USER_PASSWORD_KEY, "", cipher)

    fun getUserPasswordVector(): Flow<ByteArray> =
        getVector(USER_PASSWORD_KEY, byteArrayOf())

    suspend fun isBiometricsEnabled(isBiometricsEnabled: Boolean) =
        setData(BIOMETRICS_ENABLED_KEY, isBiometricsEnabled)

    fun isBiometricsEnabled(): Flow<Boolean> = getData(BIOMETRICS_ENABLED_KEY, false)

    companion object {
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email_key")
        private val USER_PASSWORD_KEY = stringPreferencesKey("user_password_key")
        private val BIOMETRICS_ENABLED_KEY = booleanPreferencesKey("biometrics_enabled_key")
    }
}