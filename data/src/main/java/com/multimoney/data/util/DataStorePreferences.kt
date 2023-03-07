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

    // Adjust
    suspend fun isAdjustSplashEventRegister(register: Boolean) =
        setData(SPLASH_ADJUST_KEY, register)

    fun isAdjustSplashEventRegister(): Flow<Boolean> = getData(SPLASH_ADJUST_KEY, true)

    suspend fun isAdjustSingUpButtonClickedEventRegister(register: Boolean) =
        setData(SIGN_UP_BUTTON_CLICKED_ADJUST_KEY, register)

    fun isAdjustSingUpButtonClickedEventRegister(): Flow<Boolean> = getData(SIGN_UP_BUTTON_CLICKED_ADJUST_KEY, true)

    suspend fun isAdjustSingUp1EventRegister(register: Boolean) =
        setData(SIGN_UP_1_ADJUST_KEY, register)

    fun isAdjustSingUp1EventRegister(): Flow<Boolean> = getData(SIGN_UP_1_ADJUST_KEY, true)

    suspend fun isAdjustSingUp2EventRegister(register: Boolean) =
        setData(SIGN_UP_2_ADJUST_KEY, register)

    fun isAdjustSingUp2EventRegister(): Flow<Boolean> = getData(SIGN_UP_2_ADJUST_KEY, true)

    suspend fun isAdjustSingUp3EventRegister(register: Boolean) =
        setData(SIGN_UP_3_ADJUST_KEY, register)

    fun isAdjustSingUp3EventRegister(): Flow<Boolean> = getData(SIGN_UP_3_ADJUST_KEY, true)

    suspend fun isAdjustSingUp4EventRegister(register: Boolean) =
        setData(SIGN_UP_4_ADJUST_KEY, register)

    fun isAdjustSingUp4EventRegister(): Flow<Boolean> = getData(SIGN_UP_4_ADJUST_KEY, true)

    suspend fun isAdjustSingUp5EventRegister(register: Boolean) =
        setData(SIGN_UP_5_ADJUST_KEY, register)

    fun isAdjustSingUp5EventRegister(): Flow<Boolean> = getData(SIGN_UP_5_ADJUST_KEY, true)

    suspend fun isAdjustSingUpAlreadyCustomerEmailEventRegister(register: Boolean) =
        setData(SIGN_UP_ALREADY_CUSTOMER_REGISTERED_EMAIL_ADJUST_KEY, register)

    fun isAdjustSingUpAlreadyCustomerEmailEventRegister(): Flow<Boolean> = getData(SIGN_UP_ALREADY_CUSTOMER_REGISTERED_EMAIL_ADJUST_KEY, true)

    suspend fun isAdjustSingUpAlreadyCustomerOTPEventRegister(register: Boolean) =
        setData(SIGN_UP_ALREADY_CUSTOMER_REGISTERED_OTP_ADJUST_KEY, register)

    fun isAdjustSingUpAlreadyCustomerOTPEventRegister(): Flow<Boolean> = getData(SIGN_UP_ALREADY_CUSTOMER_REGISTERED_OTP_ADJUST_KEY, true)

    suspend fun isAdjustSingUpAlreadyCustomerPasswordEventRegister(register: Boolean) =
        setData(SIGN_UP_ALREADY_CUSTOMER_REGISTERED_PASSWORD_ADJUST_KEY, register)

    fun isAdjustSingUpAlreadyCustomerPasswordEventRegister(): Flow<Boolean> = getData(SIGN_UP_ALREADY_CUSTOMER_REGISTERED_PASSWORD_ADJUST_KEY, true)

    suspend fun isAdjustFirstSingInEventRegister(register: Boolean) =
        setData(SIGN_IN_FIRST_LOGIN_ADJUST_KEY, register)

    fun isAdjustFirstSingInEventRegister(): Flow<Boolean> = getData(SIGN_IN_FIRST_LOGIN_ADJUST_KEY, true)

    suspend fun isAdjustFirstSchedulePaymentEventRegister(register: Boolean) =
        setData(HOME_SCHEDULE_PAYMENT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstSchedulePaymentEventRegister(): Flow<Boolean> = getData(HOME_SCHEDULE_PAYMENT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstPaymentEventRegister(register: Boolean) =
        setData(HOME_PAYMENT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstPaymentEventRegister(): Flow<Boolean> = getData(HOME_PAYMENT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstAccountStatusEventRegister(register: Boolean) =
        setData(HOME_ACCOUNT_STATUS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstAccountStatusEventRegister(): Flow<Boolean> = getData(HOME_ACCOUNT_STATUS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstActivateMMVisaEventRegister(register: Boolean) =
        setData(HOME_ACTIVATE_MM_VISA_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstActivateMMVisaEventRegister(): Flow<Boolean> = getData(HOME_ACTIVATE_MM_VISA_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstLinkMMVisaEventRegister(register: Boolean) =
        setData(HOME_LINK_MM_VISA_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstLinkMMVisaEventRegister(): Flow<Boolean> = getData(HOME_LINK_MM_VISA_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstActivatedMMVisaEventRegister(register: Boolean) =
        setData(HOME_ACTIVATED_MM_VISA_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstActivatedMMVisaEventRegister(): Flow<Boolean> = getData(HOME_ACTIVATED_MM_VISA_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationFirstScreenEventRegister(register: Boolean) =
        setData(ORIGINATION_FIRST_SCREEN_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationFirstScreenEventRegister(): Flow<Boolean> = getData(ORIGINATION_FIRST_SCREEN_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationCrosselingFirstScreenEventRegister(register: Boolean) =
        setData(ORIGINATION_CROSSELING_FIRST_SCREEN_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationCrosselingFirstScreenEventRegister(): Flow<Boolean> = getData(ORIGINATION_CROSSELING_FIRST_SCREEN_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationCheckTermsEventRegister(register: Boolean) =
        setData(ORIGINATION_CHECK_TERMS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationCheckTermsEventRegister(): Flow<Boolean> = getData(ORIGINATION_CHECK_TERMS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationCrosselingCheckTermsEventRegister(register: Boolean) =
        setData(ORIGINATION_CROSSELING_CHECK_TERMS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationCrosselingCheckTermsEventRegister(): Flow<Boolean> = getData(ORIGINATION_CROSSELING_CHECK_TERMS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationConfirmAmountEventRegister(register: Boolean) =
        setData(ORIGINATION_ENTER_AMOUNT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationConfirmAmountEventRegister(): Flow<Boolean> = getData(ORIGINATION_ENTER_AMOUNT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationCrosselingConfirmAmountEventRegister(register: Boolean) =
        setData(ORIGINATION_CROSSELING_ENTER_AMOUNT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationCrosselingConfirmAmountEventRegister(): Flow<Boolean> = getData(ORIGINATION_CROSSELING_ENTER_AMOUNT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationFillAccountEventRegister(register: Boolean) =
        setData(ORIGINATION_FILL_ACCOUNT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationFillAccountEventRegister(): Flow<Boolean> = getData(ORIGINATION_FILL_ACCOUNT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationCrosselingFillAccountEventRegister(register: Boolean) =
        setData(ORIGINATION_CROSSELING_FILL_ACCOUNT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationCrosselingFillAccountEventRegister(): Flow<Boolean> = getData(ORIGINATION_CROSSELING_FILL_ACCOUNT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationMonthlyIncomeEventRegister(register: Boolean) =
        setData(ORIGINATION_MONTHLY_INCOME_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationMonthlyIncomeEventRegister(): Flow<Boolean> = getData(ORIGINATION_MONTHLY_INCOME_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationJobInformationEventRegister(register: Boolean) =
        setData(ORIGINATION_JOB_INFO_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationJobInformationEventRegister(): Flow<Boolean> = getData(ORIGINATION_JOB_INFO_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationCrosselingJobInformationEventRegister(register: Boolean) =
        setData(ORIGINATION_CROSSELING_JOB_INFO_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationCrosselingJobInformationEventRegister(): Flow<Boolean> = getData(ORIGINATION_CROSSELING_JOB_INFO_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationJobAddressEventRegister(register: Boolean) =
        setData(ORIGINATION_JOB_ADDRESS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationJobAddressEventRegister(): Flow<Boolean> = getData(ORIGINATION_JOB_ADDRESS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationOwnAddressEventRegister(register: Boolean) =
        setData(ORIGINATION_OWN_ADDRESS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationOwnAddressEventRegister(): Flow<Boolean> = getData(ORIGINATION_OWN_ADDRESS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationPEPEventRegister(register: Boolean) =
        setData(ORIGINATION_PEP_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationPEPEventRegister(): Flow<Boolean> = getData(ORIGINATION_PEP_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationOnfidoStartsEventRegister(register: Boolean) =
        setData(ORIGINATION_ONFIDO_STARTS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationOnfidoStartsEventRegister(): Flow<Boolean> = getData(ORIGINATION_ONFIDO_STARTS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationOnfidoFinishEventRegister(register: Boolean) =
        setData(ORIGINATION_ONFIDO_FINISH_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationOnfidoFinishEventRegister(): Flow<Boolean> = getData(ORIGINATION_ONFIDO_FINISH_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationEvicertiaSignDocumentEventRegister(register: Boolean) =
        setData(ORIGINATION_EVICERTIA_SIGN_DOCUMENT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationEvicertiaSignDocumentEventRegister(): Flow<Boolean> = getData(ORIGINATION_EVICERTIA_SIGN_DOCUMENT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationCrosselingEvicertiaSignDocumentEventRegister(register: Boolean) =
        setData(ORIGINATION_CROSSELING_EVICERTIA_SIGN_DOCUMENT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationCrosselingEvicertiaSignDocumentEventRegister(): Flow<Boolean> = getData(ORIGINATION_CROSSELING_EVICERTIA_SIGN_DOCUMENT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationEvicertiaCustomerRejectedEventRegister(register: Boolean) =
        setData(ORIGINATION_EVICERTIA_CUSTOMER_REJECTED_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationEvicertiaCustomerRejectedEventRegister(): Flow<Boolean> = getData(ORIGINATION_EVICERTIA_CUSTOMER_REJECTED_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationEvicertiaSuccessEventRegister(register: Boolean) =
        setData(ORIGINATION_EVICERTIA_CUSTOMER_SUCCESS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationEvicertiaSuccessEventRegister(): Flow<Boolean> = getData(ORIGINATION_EVICERTIA_CUSTOMER_SUCCESS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationCrosselingEvicertiaSuccessEventRegister(register: Boolean) =
        setData(ORIGINATION_CROSSELING_EVICERTIA_CUSTOMER_SUCCESS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationCrosselingEvicertiaSuccessEventRegister(): Flow<Boolean> = getData(ORIGINATION_CROSSELING_EVICERTIA_CUSTOMER_SUCCESS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationNonPreApprovedInfoExtraEventRegister(register: Boolean) =
        setData(ORIGINATION_NON_PREAPPROVED_INFO_EXTRA_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationNonPreApprovedInfoExtraEventRegister(): Flow<Boolean> = getData(ORIGINATION_NON_PREAPPROVED_INFO_EXTRA_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationNonPreApprovedRejectedEventRegister(register: Boolean) =
        setData(ORIGINATION_NON_PREAPPROVED_REJECTED_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationNonPreApprovedRejectedEventRegister(): Flow<Boolean> = getData(ORIGINATION_NON_PREAPPROVED_REJECTED_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstOriginationNonPreApprovedApprovedEventRegister(register: Boolean) =
        setData(ORIGINATION_NON_PREAPPROVED_SUCCESS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstOriginationNonPreApprovedApprovedEventRegister(): Flow<Boolean> = getData(ORIGINATION_NON_PREAPPROVED_SUCCESS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstDisbursementEventRegister(register: Boolean) =
        setData(DISBURSEMENT_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstDisbursementEventRegister(): Flow<Boolean> = getData(DISBURSEMENT_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstDisbursementSuccessEventRegister(register: Boolean) =
        setData(DISBURSEMENT_SUCCESS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstDisbursementSuccessEventRegister(): Flow<Boolean> = getData(DISBURSEMENT_SUCCESS_FIRST_TIME_ADJUST_KEY, true)

    suspend fun isAdjustFirstPaymentSuccessEventRegister(register: Boolean) =
        setData(PAYMENT_SUCCESS_FIRST_TIME_ADJUST_KEY, register)

    fun isAdjustFirstPaymentSuccessEventRegister(): Flow<Boolean> = getData(PAYMENT_SUCCESS_FIRST_TIME_ADJUST_KEY, true)

    // Adjust Crypto events
    suspend fun setAdjustCryptoHomeFirstTime(firstTimeHomeCrypto: Boolean) =
        setData(ADJUST_CRYPTO_HOME_FIRST_TIME, firstTimeHomeCrypto)

    fun isAdjustCryptoHomeFirstTime(): Flow<Boolean> = getData(ADJUST_CRYPTO_HOME_FIRST_TIME, false)

    suspend fun setAdjustCryptoPressPurchaseFirstTime(firstTimePurchaseCrypto: Boolean) =
        setData(ADJUST_CRYPTO_PURCHASE_FIRST_TIME, firstTimePurchaseCrypto)

    fun isAdjustCryptoPressPurchaseFirstTime(): Flow<Boolean> = getData(ADJUST_CRYPTO_SUCCESS_PURCHASE_FIRST_TIME, false)

    suspend fun setAdjustCryptoSuccessPurchaseFirstTime(firstTimePurchaseCrypto: Boolean) =
        setData(ADJUST_CRYPTO_PURCHASE_FIRST_TIME, firstTimePurchaseCrypto)

    fun isAdjustCryptoSuccessPurchaseFirstTime(): Flow<Boolean> = getData(ADJUST_CRYPTO_SUCCESS_PURCHASE_FIRST_TIME, false)

    suspend fun setAdjustCryptoPressSellFirstTime(firstTimePurchaseCrypto: Boolean) =
        setData(ADJUST_CRYPTO_SELL_FIRST_TIME, firstTimePurchaseCrypto)

    fun isAdjustCryptoPressSellFirstTime(): Flow<Boolean> = getData(ADJUST_CRYPTO_SELL_FIRST_TIME, false)

    suspend fun setAdjustCryptoSuccessSellFirstTime(firstTimeSellCrypto: Boolean) =
        setData(ADJUST_CRYPTO_SUCCESS_SELL_FIRST_TIME, firstTimeSellCrypto)

    fun isAdjustCryptoSuccessSellFirstTime(): Flow<Boolean> = getData(ADJUST_CRYPTO_SUCCESS_SELL_FIRST_TIME, false)

    suspend fun setAdjustCryptoPressSendFirstTime(firstTimeSendCrypto: Boolean) =
        setData(ADJUST_CRYPTO_SEND_FIRST_TIME, firstTimeSendCrypto)

    fun isAdjustCryptoPressSendFirstTime(): Flow<Boolean> = getData(ADJUST_CRYPTO_SEND_FIRST_TIME, false)

    suspend fun setAdjustCryptoSuccessSendFirstTime(firstTimeSendCrypto: Boolean) =
        setData(ADJUST_CRYPTO_SUCCESS_SEND_FIRST_TIME, firstTimeSendCrypto)

    fun isAdjustCryptoSuccessSendFirstTime(): Flow<Boolean> = getData(ADJUST_CRYPTO_SUCCESS_SEND_FIRST_TIME, false)

    suspend fun setAdjustCryptoPressReceiveFirstTime(firstTimeReceiveCrypto: Boolean) =
        setData(ADJUST_CRYPTO_RECEIVE_FIRST_TIME, firstTimeReceiveCrypto)

    fun isAdjustCryptoPressReceiveFirstTime(): Flow<Boolean> = getData(ADJUST_CRYPTO_RECEIVE_FIRST_TIME, false)

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
        private val SPLASH_ADJUST_KEY = booleanPreferencesKey("splash_adjust_key")
        private val SIGN_UP_BUTTON_CLICKED_ADJUST_KEY = booleanPreferencesKey("sign_up_button_adjust_key")
        private val SIGN_UP_1_ADJUST_KEY = booleanPreferencesKey("sign_up_1_adjust_key")
        private val SIGN_UP_2_ADJUST_KEY = booleanPreferencesKey("sign_up_2_adjust_key")
        private val SIGN_UP_3_ADJUST_KEY = booleanPreferencesKey("sign_up_3_adjust_key")
        private val SIGN_UP_4_ADJUST_KEY = booleanPreferencesKey("sign_up_4_adjust_key")
        private val SIGN_UP_5_ADJUST_KEY = booleanPreferencesKey("sign_up_5_adjust_key")
        private val SIGN_UP_ALREADY_CUSTOMER_REGISTERED_EMAIL_ADJUST_KEY = booleanPreferencesKey("sign_up_registered_user_email_adjust_key")
        private val SIGN_UP_ALREADY_CUSTOMER_REGISTERED_OTP_ADJUST_KEY = booleanPreferencesKey("sign_up_registered_user_otp_adjust_key")
        private val SIGN_UP_ALREADY_CUSTOMER_REGISTERED_PASSWORD_ADJUST_KEY = booleanPreferencesKey("sign_up_registered_user_password_adjust_key")
        private val SIGN_IN_FIRST_LOGIN_ADJUST_KEY = booleanPreferencesKey("sign_in_first_login")
        private val HOME_SCHEDULE_PAYMENT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("home_credit_first_schedule")
        private val HOME_PAYMENT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("home_credit_first_payment")
        private val HOME_ACCOUNT_STATUS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("home_credit_first_account_status")
        private val HOME_ACTIVATE_MM_VISA_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("home_first_activate_mm_visa")
        private val HOME_LINK_MM_VISA_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("home_first_link_mm_visa")
        private val HOME_ACTIVATED_MM_VISA_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("home_first_activated_mm_visa")
        private val ORIGINATION_FIRST_SCREEN_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_first_screen")
        private val ORIGINATION_CROSSELING_FIRST_SCREEN_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_crosseling_first_screen")
        private val ORIGINATION_CHECK_TERMS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_check_terms")
        private val ORIGINATION_CROSSELING_CHECK_TERMS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_crosseling_check_terms")
        private val ORIGINATION_ENTER_AMOUNT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_enter_amount")
        private val ORIGINATION_CROSSELING_ENTER_AMOUNT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_crosseling_enter_amount")
        private val ORIGINATION_FILL_ACCOUNT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_fill_account")
        private val ORIGINATION_CROSSELING_FILL_ACCOUNT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_crosseling_fill_account")
        private val ORIGINATION_MONTHLY_INCOME_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_monthly_income")
        private val ORIGINATION_JOB_INFO_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_job_info")
        private val ORIGINATION_CROSSELING_JOB_INFO_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_crosseling_job_info")
        private val ORIGINATION_JOB_ADDRESS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_job_address")
        private val ORIGINATION_OWN_ADDRESS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_job_address")
        private val ORIGINATION_PEP_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_pep")
        private val ORIGINATION_ONFIDO_STARTS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_onfido_starts")
        private val ORIGINATION_ONFIDO_FINISH_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_onfido_finish")
        private val ORIGINATION_EVICERTIA_SIGN_DOCUMENT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_evicertia_sign_document")
        private val ORIGINATION_CROSSELING_EVICERTIA_SIGN_DOCUMENT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_crosseling_evicertia_sign_document")
        private val ORIGINATION_EVICERTIA_CUSTOMER_REJECTED_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_evicertia_customer_rejected")
        private val ORIGINATION_EVICERTIA_CUSTOMER_SUCCESS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_evicertia_customer_success")
        private val ORIGINATION_CROSSELING_EVICERTIA_CUSTOMER_SUCCESS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_crosseling_evicertia_customer_success")
        private val ORIGINATION_NON_PREAPPROVED_INFO_EXTRA_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_nonpreapproved_info_extra")
        private val ORIGINATION_NON_PREAPPROVED_REJECTED_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_nonpreapproved_rejected")
        private val ORIGINATION_NON_PREAPPROVED_SUCCESS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("origination_nonpreapproved_success")
        private val DISBURSEMENT_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("disbursement_first_time")
        private val DISBURSEMENT_SUCCESS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("disbursement_success_first_time")
        private val PAYMENT_SUCCESS_FIRST_TIME_ADJUST_KEY = booleanPreferencesKey("payment_success_first_time")
        private val ADJUST_CRYPTO_HOME_FIRST_TIME = booleanPreferencesKey("adjust_crypto_home_first_time")
        private val ADJUST_CRYPTO_PURCHASE_FIRST_TIME = booleanPreferencesKey("adjust_crypto_purchase_first_time")
        private val ADJUST_CRYPTO_SUCCESS_PURCHASE_FIRST_TIME = booleanPreferencesKey("adjust_crypto_success_purchase_first_time")
        private val ADJUST_CRYPTO_SELL_FIRST_TIME = booleanPreferencesKey("adjust_crypto_sell_first_time")
        private val ADJUST_CRYPTO_SUCCESS_SELL_FIRST_TIME = booleanPreferencesKey("adjust_crypto_success_sell_first_time")
        private val ADJUST_CRYPTO_SEND_FIRST_TIME = booleanPreferencesKey("adjust_crypto_send_first_time")
        private val ADJUST_CRYPTO_SUCCESS_SEND_FIRST_TIME = booleanPreferencesKey("adjust_crypto_success_send_first_time")
        private val ADJUST_CRYPTO_RECEIVE_FIRST_TIME = booleanPreferencesKey("adjust_crypto_receive_first_time")
    }
}
