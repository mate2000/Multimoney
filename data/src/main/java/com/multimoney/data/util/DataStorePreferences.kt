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

    suspend fun setIdBrand(idBrand: String) {
        setSecuredData(ID_BRAND, idBrand)
    }

    fun getIdBrand(): Flow<String> = getSecuredData(ID_BRAND, "")

    suspend fun setPkUser(pkUser: String) {
        setSecuredData(PK_USER, pkUser)
    }

    fun getPkUser(): Flow<String> = getSecuredData(PK_USER, "")

    suspend fun setUserEmail(userEmail: String) =
        setSecuredData(USER_EMAIL_KEY, userEmail)

    fun getUserEmail(): Flow<String> = getSecuredData(USER_EMAIL_KEY, "")

    suspend fun setUserName(userName: String) =
        setSecuredData(USER_NAME_KEY, userName)

    fun getUserName(): Flow<String> = getSecuredData(USER_NAME_KEY, "")

    suspend fun setUserPassword(userPassword: String, cipher: Cipher) =
        setSecuredData(USER_PASSWORD_KEY, userPassword, cipher)

    fun getUserPassword(cipher: Cipher): Flow<String> =
        getSecuredData(USER_PASSWORD_KEY, "", cipher)

    fun getUserPasswordVector(): Flow<ByteArray> =
        getVector(USER_PASSWORD_KEY, byteArrayOf())

    suspend fun isBiometricsEnabled(isBiometricsEnabled: Boolean) =
        setData(BIOMETRICS_ENABLED_KEY, isBiometricsEnabled)

    fun isBiometricsEnabled(): Flow<Boolean> = getData(BIOMETRICS_ENABLED_KEY, false)

    suspend fun isOnBoardingEnabled(isOnBoardingEnabled: Boolean) =
        setData(ON_BOARDING_ENABLED_KEY, isOnBoardingEnabled)

    fun isOnBoardingEnabled(): Flow<Boolean> = getData(ON_BOARDING_ENABLED_KEY, true)

    companion object {
        private val ID_BRAND = stringPreferencesKey("id_brand")
        private val PK_USER = stringPreferencesKey("pk_user")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email_key")
        private val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        private val USER_PASSWORD_KEY = stringPreferencesKey("user_password_key")
        private val BIOMETRICS_ENABLED_KEY = booleanPreferencesKey("biometrics_enabled_key")
        private val ON_BOARDING_ENABLED_KEY = booleanPreferencesKey("on_boarding_enabled_key")
    }
}