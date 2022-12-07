package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.security.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.security.CatalogType
import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.security.Company
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.CountryList
import com.multimoney.domain.model.security.MiniCards
import com.multimoney.domain.model.security.MiniCardsItem
import com.multimoney.domain.model.security.OnfidoCheckProcess
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.security.QuickActions
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
    private val graphqlApi: GraphqlApi
) : BaseRepository(), SecurityRepository, Serializable {

    override suspend fun queryGetCompanyNameByIdentification(
        identification: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<Company?>> =
        fetchData(
            apolloCall = graphqlApi.queryGetCompanyNameByIdentification(
                identification,
                idBrand,
                user
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryValidateUserExists(
        email: String
    ): Flow<MultimoneyResult<UserData?>> = fetchData(
        apolloCall = graphqlApi.queryValidateUserExists(email),
        apolloCallMapper = { data ->
            if (data.validateUserExists.status == null || data.validateUserExists.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int,
        idDocument: Int,
        identification: String,
        firstName: String,
        secondName: String,
        firstSurname: String,
        secondSurname: String
    ): Flow<MultimoneyResult<UserData?>> = fetchData(
        apolloCall = graphqlApi.mutationUserValidation(
            email,
            currentStep,
            idBrand,
            idDocument,
            identification,
            firstName,
            secondName,
            firstSurname,
            secondSurname
        ),
        apolloCallMapper = { data ->
            if (data.userValidation.status == null || data.userValidation.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
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
        apolloCall = graphqlApi.mutationUpdateUserRegister(
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
        apolloCall = graphqlApi.queryValidationSecurity(pkIUser.toInt(), password, user, idBrand),
        apolloCallMapper = { data ->
            if (data.validateSecurity.status == null || data.validateSecurity.status == 0) {
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
        apolloCall = graphqlApi.queryDataInformationClient(identification, idBrand, user),
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
            apolloCall = graphqlApi.queryValidateUserStatus(
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
        apolloCall = graphqlApi.mutationSendPinProcess(
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
            if (data.sendPinProccess.status == null || data.sendPinProccess.status == 0) {
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
        apolloCall = graphqlApi.mutationOnFidoInitialProcess(
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

    override suspend fun mutationOnFidoCheckProcess(
        identification: String,
        applicantId: String,
        currentFlow: String,
        pkUser: Long,
        userRequestId: Long,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoCheckProcess>> = fetchData(
        apolloCall = graphqlApi.mutationOnfidoCheckProcess(
            identification,
            applicantId,
            currentFlow,
            pkUser,
            userRequestId,
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
        apolloCall = graphqlApi.queryValidationPin(
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
            if (data.validatePin.status == null || data.validatePin.status == 0) {
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
        apolloCall = graphqlApi.queryCatalogIdentification(idBrand, user),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryGetCountry(user: String): Flow<MultimoneyResult<CountryList?>> =
        fetchData(
            apolloCall = graphqlApi.queryGetCountry(user),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryGetConfigurationVersion(
        platform: String,
        appVersion: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ConfigurationVersion?>> =
        fetchData(
            apolloCall = graphqlApi.queryGetConfigurationVersion(platform, appVersion, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryValidateBankAccount(
        account: String,
        identification: String,
        user: String,
        idBrand: Int
    ) = fetchData(
        apolloCall = graphqlApi.queryValidateAccount(account, identification, user, idBrand),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryGetQuickActions(
        idBrand: Int,
        pkUser: Int,
        identification: String,
        infoCreditStatus: Int,
        infoVirtualCardStatus: Int,
        infoBankAccountStatus: Int,
        infoCriptoStatus: Int
    ): Flow<MultimoneyResult<QuickActions?>> =
        fetchData(
            apolloCall = graphqlApi.queryGetQuickActions(
                idBrand,
                pkUser,
                identification,
                infoCreditStatus,
                infoVirtualCardStatus,
                infoBankAccountStatus,
                infoCriptoStatus
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryHomeMiniCards(
        infoCreditStatus: Boolean,
        infoVirtualCardStatus: Boolean,
        infoBankAccountStatus: Boolean,
        infoCripto: Boolean,
        userEmail: String,
        idBrand: Int
    ): Flow<MultimoneyResult<MiniCards>> =
        fetchData(
            apolloCall = graphqlApi.queryMiniCards(
                infoCreditStatus,
                infoVirtualCardStatus,
                infoBankAccountStatus,
                infoCripto,
                userEmail,
                idBrand
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
}
