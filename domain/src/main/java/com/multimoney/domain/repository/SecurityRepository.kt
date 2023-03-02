package com.multimoney.domain.repository

import com.multimoney.domain.model.security.CatalogType
import com.multimoney.domain.model.security.ChangeDevice
import com.multimoney.domain.model.security.ChangeEmail
import com.multimoney.domain.model.security.ChangePhone
import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.security.Company
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.CountryList
import com.multimoney.domain.model.security.CountryPhoneCodes
import com.multimoney.domain.model.security.MiniCards
import com.multimoney.domain.model.security.OnfidoCheckProcess
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.security.QuickActions
import com.multimoney.domain.model.security.RequestChangeDevice
import com.multimoney.domain.model.security.SaveLogTracking
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.security.UserPhoneMobileSave
import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.security.ValidateOTP
import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {

    suspend fun queryGetCompanyNameByIdentification(
        identification: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<Company?>>

    suspend fun queryValidateUserExists(
        email: String,
        deviceId: String
    ): Flow<MultimoneyResult<UserData?>>

    suspend fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int,
        idDocument: Int,
        identification: String,
        firstName: String,
        secondName: String,
        firstSurname: String,
        secondSurname: String,
        deviceId: String
    ): Flow<MultimoneyResult<UserData?>>

    suspend fun mutationUpdateUserRegister(
        pkUser: String,
        user: String,
        email: String,
        phoneNumber: String?,
        fullName: String?,
        firstName: String?,
        secondName: String?,
        lastName: String?,
        secondLastName: String?,
        nationality: String?,
        identification: String?,
        countryCode: String?,
        currentStep: String,
        idBrand: Int
    ): Flow<MultimoneyResult<UserData?>>

    suspend fun queryValidationSecurity(
        pkIUser: String,
        password: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateSecurity?>>

    suspend fun queryDataInformationClient(
        identification: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<ClientInfoCr?>>

    suspend fun queryValidateUserStatus(
        pkUser: Int,
        identification: String,
        email: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateUserStatus?>>

    suspend fun mutationSendPinProcess(
        identification: String,
        firstName: String,
        email: String,
        cellPhone: String,
        sendMethod: String,
        pkUser: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SendPinProcess?>>

    suspend fun mutationOnFidoInitialProcess(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoToken?>>

    suspend fun mutationOnFidoCheckProcess(
        identification: String,
        applicantId: String,
        currentFlow: String,
        pkUser: Long,
        userRequestId: Long,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoCheckProcess>>

    suspend fun queryValidatePin(
        idBrand: Int,
        appSource: Int,
        pkUser: String,
        ip: String?,
        pinSecurity: String,
        telephone: String?,
        sendValidatePin: String,
        flowOrigination: String,
        userCreate: String
    ): Flow<MultimoneyResult<ValidatePin?>>

    suspend fun queryCatalog(
        idBrand: Int,
        user: String,
        isTransferIdentification: Int?
    ): Flow<MultimoneyResult<CatalogType?>>

    suspend fun queryGetCountry(user: String): Flow<MultimoneyResult<CountryList?>>

    suspend fun queryGetConfigurationVersion(
        platform: String,
        appVersion: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ConfigurationVersion?>>

    suspend fun queryValidateBankAccount(
        account: String,
        identification: String,
        queryType: String?,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateAccount?>>

    suspend fun queryGetQuickActions(
        idBrand: Int,
        pkUser: Int,
        identification: String,
        infoCreditStatus: Int,
        infoVirtualCardStatus: Int,
        infoBankAccountStatus: Int,
        infoCriptoStatus: Int
    ): Flow<MultimoneyResult<QuickActions?>>

    suspend fun queryGetCountryPhoneCodes(
        idBrand: Int
    ): Flow<MultimoneyResult<CountryPhoneCodes>>

    suspend fun mutationValidateOTP(
        email: String,
        otp: String
    ): Flow<MultimoneyResult<ValidateOTP>>

    suspend fun mutationChangePhone(
        identification: String,
        phone: String,
        pkUser: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ChangePhone>>

    suspend fun mutationChangeEmail(
        idClient: Int,
        pkUser: Int,
        identification: String,
        email: String,
        registerId: Int,
        changeUser: Boolean,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ChangeEmail>>

    suspend fun queryHomeMiniCards(
        infoCreditStatus: Boolean,
        infoVirtualCardStatus: Boolean,
        infoBankAccountStatus: Boolean,
        infoCripto: Boolean,
        userEmail: String,
        idBrand: Int
    ): Flow<MultimoneyResult<MiniCards>>

    suspend fun mutationUserPhoneMobileSave(
        idBrand: Int,
        idWalletCard: String,
        manufacture: String,
        pkUser: Long,
        serialNumber: String,
        user: String
    ): Flow<MultimoneyResult<UserPhoneMobileSave>>

    suspend fun mutationRequestChangeDevice(
        email: String
    ): Flow<MultimoneyResult<RequestChangeDevice>>

    suspend fun mutationChangeDevice(
        email: String,
        otp: String
    ): Flow<MultimoneyResult<ChangeDevice>>

    suspend fun mutationSaveLogTracking(
        identification: String,
        pkUser: Int?,
        keySearch: String,
        data: String,
        idBrand: Int?
    ): Flow<MultimoneyResult<SaveLogTracking>>
}
