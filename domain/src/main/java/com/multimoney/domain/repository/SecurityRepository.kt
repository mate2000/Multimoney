package com.multimoney.domain.repository

import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {

    suspend fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int
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
        ip: String,
        pinSecurity: String,
        telephone: String,
        sendValidatePin: String,
        flowOrigination: String,
        userCreate: String
    ): Flow<MultimoneyResult<ValidatePin?>>
}