package com.multimoney.domain.repository

import com.multimoney.domain.model.security.CatalogType
import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.security.CountryList
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {

    suspend fun queryValidateUserExists(
        email: String
    ):Flow<MultimoneyResult<UserData?>>

    suspend fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int,
        idDocument:Int,
        identification:String,
        firstName: String,
        secondName:String,
        firstSurname:String,
        secondSurname:String
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
        user: String
    ): Flow<MultimoneyResult<CatalogType?>>

    suspend fun queryGetCountry(user: String): Flow<MultimoneyResult<CountryList?>>

    suspend fun queryValidateBankAccount(
        account: String,
        identification: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateAccount?>>
}