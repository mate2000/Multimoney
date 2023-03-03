package com.multimoney.data.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.reflect.TypeToken
import com.multimoney.data.base.BaseDataStorePreferences
import com.multimoney.data.util.cryptography.CryptographyHelper
import com.multimoney.domain.model.security.SmartTransferLimit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
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

    suspend fun setAuthToken(token: String) {
        setSecuredData(TOKEN_KEY, token)
    }

    fun getAuthToken(): Flow<String> =
        getSecuredData(TOKEN_KEY, "")

    suspend fun setIdBrand(idBrand: String) {
        setSecuredData(ID_BRAND, idBrand)
    }

    fun getIdBrand(): Flow<String> = getSecuredData(ID_BRAND, "")

    suspend fun setPkUser(pkUser: String) {
        setSecuredData(PK_USER, pkUser)
    }

    fun getPkUser(): Flow<String> = getSecuredData(PK_USER, "")

    suspend fun setIdentification(identification: String) {
        setSecuredData(IDENTIFICATION, identification)
    }

    fun getIdentification(): Flow<String> = getSecuredData(IDENTIFICATION, "")

    suspend fun setUserEmail(userEmail: String) =
        setSecuredData(USER_EMAIL_KEY, userEmail)

    fun getUserEmail(): Flow<String> = getSecuredData(USER_EMAIL_KEY, "")

    suspend fun setUserName(userName: String) =
        setSecuredData(USER_NAME_KEY, userName)

    fun getUserName(): Flow<String> = getSecuredData(USER_NAME_KEY, "")

    suspend fun setUserPhoneNumber(phone: String) = setSecuredData(USER_PHONE_NUMBER_KEY, phone)

    fun getUserPhoneNumber(): Flow<String> = getSecuredData(USER_PHONE_NUMBER_KEY, "")

    suspend fun setUserPhoneNumberWithCode(phone: String) =
        setSecuredData(USER_PHONE_NUMBER_WITH_CODE_KEY, phone)

    fun getUserPhoneNumberWithCode(): Flow<String> =
        getSecuredData(USER_PHONE_NUMBER_WITH_CODE_KEY, "")

    suspend fun setUserPassword(userPassword: String, cipher: Cipher) =
        setSecuredData(USER_PASSWORD_KEY, userPassword, cipher)

    fun getUserPassword(cipher: Cipher): Flow<String> =
        getSecuredData(USER_PASSWORD_KEY, "", cipher)

    fun getUserPasswordVector(): Flow<ByteArray> =
        getVector(USER_PASSWORD_KEY, byteArrayOf())

    suspend fun isBiometricsEnabled(isBiometricsEnabled: Boolean) =
        setData(BIOMETRICS_ENABLED_KEY, isBiometricsEnabled)

    fun isBiometricsEnabled(): Flow<Boolean> = getData(BIOMETRICS_ENABLED_KEY, false)

    suspend fun isSessionDuplicated(isSessionEnabled: Boolean) =
        setData(SESSION_DUPLICATED_KEY, isSessionEnabled)

    fun isSessionDuplicated(): Flow<Boolean> = getData(SESSION_DUPLICATED_KEY, false)

    suspend fun isForceShowBiometricPrompt(isForceShowBiometricPrompt: Boolean) =
        setData(FORCE_SHOW_BIOMETRICS_PROMPT, isForceShowBiometricPrompt)

    fun isForceShowBiometricPrompt(): Flow<Boolean> = getData(FORCE_SHOW_BIOMETRICS_PROMPT, false)

    suspend fun isSignOutOnBackground(isSignOutOnBackground: Boolean) =
        setData(SIGN_OUT_ON_BACKGROUND, isSignOutOnBackground)

    fun isSignOutOnBackground(): Flow<Boolean> = getData(SIGN_OUT_ON_BACKGROUND, false)

    suspend fun isOnBoardingEnabled(isOnBoardingEnabled: Boolean) =
        setData(ON_BOARDING_ENABLED_KEY, isOnBoardingEnabled)

    fun isOnBoardingEnabled(): Flow<Boolean> = getData(ON_BOARDING_ENABLED_KEY, true)

    private suspend fun setUniqueID(uuid: String) {
        setSecuredData(UNIQUE_ID, uuid)
    }

    suspend fun getUniqueId(): Flow<String> {
        if (getSecuredData(UNIQUE_ID, "").first().isEmpty()) {
            setUniqueID(UUID.randomUUID().toString())
        }
        return getSecuredData(UNIQUE_ID, "")
    }

    suspend fun setDeviceID(deviceId: String) {
        setData(DEVICE_ID, deviceId)
    }

    fun getDeviceId(): Flow<String> {
        return getData(DEVICE_ID, "")
    }

    suspend fun isContactPermissionRequested(isOnBoardingEnabled: Boolean) =
        setData(CONTACT_PERMISSION_STATE_KEY, isOnBoardingEnabled)

    fun isContactPermissionRequested(): Flow<Boolean> = getData(CONTACT_PERMISSION_STATE_KEY, false)

    suspend fun isVisaCardExpiredDialogEnabled(dialogEnabled: Boolean) {
        setData(VISA_CARD_EXPIRED_DIALOG_KEY, dialogEnabled)
    }

    fun isVisaCardExpiredEnabled(): Flow<Boolean> = getData(VISA_CARD_EXPIRED_DIALOG_KEY, true)

    suspend fun setVolatileDialogVisible(isVisible: Boolean) {
        setData(VOLATILE_DIALOG_KEY, isVisible)
    }

    fun isVolatileDialogVisible(): Flow<Boolean> = getData(VOLATILE_DIALOG_KEY, true)

    suspend fun setNotShowAgainVerifyCryptoAddress() =
        setData(NOT_SHOW_AGAIN_VERIFY_CRYPTO_ADDRESS, true)

    fun getNotShowAgainVerifyCryptoAddress(): Flow<Boolean> = getData(NOT_SHOW_AGAIN_VERIFY_CRYPTO_ADDRESS, false)

    suspend fun isCameraPermissionRequested(permissionRequested: Boolean) =
        setData(CAMERA_PERMISSION_STATE_KEY, permissionRequested)

    fun isCameraPermissionRequested(): Flow<Boolean> = getData(CAMERA_PERMISSION_STATE_KEY, false)

    suspend fun saveCryptoOrigin(cryptoOrigin: String) {
        setData(CRYPTO_ORIGIN_KEY, cryptoOrigin)
    }

    fun getCryptoOrigin(): Flow<String> = getData(CRYPTO_ORIGIN_KEY, "")

    suspend fun saveEnableCryptoTransfer(enable: Boolean) {
        setData(ENABLE_CRYPTO_TRANSFER_KEY, enable)
    }

    fun isCryptoTransferEnabled(): Flow<Boolean> = getData(ENABLE_CRYPTO_TRANSFER_KEY, false)

    suspend fun setSmartTransferLimit(limits: List<SmartTransferLimit?>) {
        putListFlow(SMART_LIMITS, list = limits)
    }
    fun getSmartTransferLimit() = getListFlow<SmartTransferLimit?>(SMART_LIMITS, object : TypeToken<List<SmartTransferLimit>>() {}.type)

    companion object {
        private val UNIQUE_ID = stringPreferencesKey("unique_id")
        private val DEVICE_ID = stringPreferencesKey("device_id")
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val ID_BRAND = stringPreferencesKey("id_brand")
        private val PK_USER = stringPreferencesKey("pk_user")
        private val IDENTIFICATION = stringPreferencesKey("identification")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email_key")
        private val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        private val USER_PHONE_NUMBER_KEY = stringPreferencesKey("user_phone_number_key")
        private val USER_PHONE_NUMBER_WITH_CODE_KEY = stringPreferencesKey("user_phone_number_with_code_key")
        private val USER_PASSWORD_KEY = stringPreferencesKey("user_password_key")
        private val BIOMETRICS_ENABLED_KEY = booleanPreferencesKey("biometrics_enabled_key")
        private val SESSION_DUPLICATED_KEY = booleanPreferencesKey("session_duplicated_key")
        private val FORCE_SHOW_BIOMETRICS_PROMPT = booleanPreferencesKey("force_show_biometrics_prompt")
        private val ON_BOARDING_ENABLED_KEY = booleanPreferencesKey("on_boarding_enabled_key")
        private val CONTACT_PERMISSION_STATE_KEY = booleanPreferencesKey("contact_permission_state_key")
        private val SIGN_OUT_ON_BACKGROUND = booleanPreferencesKey("sign_out_on_background")
        private val VISA_CARD_EXPIRED_DIALOG_KEY = booleanPreferencesKey("visa_card_expired_dialog_key")
        private val VOLATILE_DIALOG_KEY = booleanPreferencesKey("volatile_dialog_key")
        private val NOT_SHOW_AGAIN_VERIFY_CRYPTO_ADDRESS = booleanPreferencesKey("not_show_again_verify_crypto_address")
        private val CAMERA_PERMISSION_STATE_KEY = booleanPreferencesKey("camera_permission_state_key")
        private val CRYPTO_ORIGIN_KEY = stringPreferencesKey("crypto_origin_key")
        private val ENABLE_CRYPTO_TRANSFER_KEY = booleanPreferencesKey("enable_crypto_transfer_key")
        private val SMART_LIMITS = stringPreferencesKey("smart_limits")
    }
}
