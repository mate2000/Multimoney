package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.security.mapToDomainModel
import com.multimoney.data.networking.SecurityApi
import com.multimoney.domain.model.security.CatalogType
import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.security.CountryList
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.security.ValidatePin
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Message
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow
import java.io.Serializable
import javax.inject.Inject

class SecurityRepositoryImpl @Inject constructor(
    private val securityApi: SecurityApi
) : BaseRepository(), SecurityRepository, Serializable {

    override suspend fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int
    ): Flow<MultimoneyResult<UserData?>> = fetchData(
        apolloCall = securityApi.mutationUserValidation(email, currentStep, idBrand),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationUpdateUserRegister(
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
    ): Flow<MultimoneyResult<UserData?>> = fetchData(
        apolloCall = securityApi.mutationUpdateUserRegister(
            pkUser,
            user,
            email,
            phoneNumber,
            fullName,
            firstName,
            secondName,
            lastName,
            secondLastName,
            nationality,
            identification,
            countryCode,
            currentStep,
            idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryValidationSecurity(
        pkIUser: String,
        password: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateSecurity?>> = fetchData(
        apolloCall = securityApi.queryValidationSecurity(pkIUser.toInt(), password, user, idBrand),
        apolloCallMapper = { data ->
            if (data.validateSecurity?.status == null || data.validateSecurity.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun queryDataInformationClient(
        identification: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<ClientInfoCr?>> = fetchData(
        apolloCall = securityApi.queryDataInformationClient(identification, idBrand, user),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryValidateUserStatus(
        pkUser: Int,
        identification: String,
        email: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateUserStatus?>> =
        fetchData(
            apolloCall = securityApi.queryValidateUserStatus(
                pkUser,
                identification,
                email,
                idBrand
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )


    override suspend fun mutationSendPinProcess(
        identification: String,
        firstName: String,
        email: String,
        cellPhone: String,
        sendMethod: String,
        pkUser: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SendPinProcess?>> = fetchData(
        apolloCall = securityApi.mutationSendPinProcess(
            identification,
            firstName,
            email,
            cellPhone,
            sendMethod,
            pkUser.toInt(),
            idBrand,
            user
        ),
        apolloCallMapper = { data ->
            if (data.sendPinProccess?.status == null || data.sendPinProccess.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun mutationOnFidoInitialProcess(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoToken?>> = fetchData(
        apolloCall = securityApi.mutationOnFidoInitialProcess(
            names,
            lastNames,
            identification,
            applicationId,
            idBrand,
            user
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryValidatePin(
        idBrand: Int,
        appSource: Int,
        pkUser: String,
        ip: String?,
        pinSecurity: String,
        telephone: String?,
        sendValidatePin: String,
        flowOrigination: String,
        userCreate: String
    ): Flow<MultimoneyResult<ValidatePin?>> = fetchData(
        apolloCall = securityApi.queryValidationPin(
            idBrand,
            appSource,
            pkUser,
            ip ?: "",
            pinSecurity,
            telephone ?: "",
            sendValidatePin,
            flowOrigination,
            userCreate
        ),
        apolloCallMapper = { data ->
            if (data.validatePin?.status == null || data.validatePin.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun queryCatalog(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<CatalogType?>> = fetchData(
        apolloCall = securityApi.queryCatalogIdentification(idBrand, user),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryGetCountry(user: String): Flow<MultimoneyResult<CountryList?>> =
        fetchData(
            apolloCall = securityApi.queryGetCountry(user),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
}
