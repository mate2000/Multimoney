package com.multimoney.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.smartaccount.mapToDomain
import com.multimoney.data.mapper.smartaccount.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.data.paging.SmartMovementsPagingSource
import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.CivilStatusResult
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivityResult
import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.accountsmart.Professions
import com.multimoney.domain.model.accountsmart.RelationshipData
import com.multimoney.domain.model.accountsmart.SaveSmartAccount
import com.multimoney.domain.model.accountsmart.SinpeAccountResult
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.accountsmart.StepByStep
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SmartAccountRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(), SmartAccountRepository {

    override suspend fun queryGetCoreBankMovements(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        pageNumber: Int,
        pageSize: Int,
        monthDate: String?
    ): Flow<MultimoneyResult<SmartMovementsResult?>> =
        fetchData(
            apolloCall = graphqlApi.queryGetCoreBankMovements(
                user,
                idBrand,
                identificationNumber,
                accountToken,
                pageNumber,
                pageSize,
                monthDate
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun getPagedMovements(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        pageSize: Int,
        monthDate: String?
    ): Flow<PagingData<SmartMovement>> {
        return Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = {
                SmartMovementsPagingSource(
                    graphqlApi,
                    user,
                    idBrand,
                    identificationNumber,
                    accountToken,
                    pageSize,
                    monthDate
                )
            }
        ).flow
    }

    override suspend fun queryCivilStatus(
        pkUser: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CivilStatusResult?>> =
        fetchData(
            apolloCall = graphqlApi.queryCivilStatus(pkUser, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryProfessionsSmart(
        pkUser: String,
        idBrand: Int
    ): Flow<MultimoneyResult<Professions?>> =
        fetchData(
            apolloCall = graphqlApi.queryProfessionSmart(pkUser, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryAddressLevelOne(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        fetchData(
            apolloCall = graphqlApi.queryAddressLevelOne(user, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryAddressLevelTwo(
        user: String,
        idBrand: Int,
        idAddressLevel1: String
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        fetchData(
            apolloCall = graphqlApi.queryAddressLevelTwo(user, idBrand, idAddressLevel1),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryAddressLevelThree(
        user: String,
        idBrand: Int,
        idAddressLevel1: String,
        idAddressLevel2: String
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        fetchData(
            apolloCall = graphqlApi.queryAddressLevelThree(
                user,
                idBrand,
                idAddressLevel1,
                idAddressLevel2
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryNationality(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<Nationalities?>> =
        fetchData(
            apolloCall = graphqlApi.queryNationality(user, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun queryStepByStep(
        user: String,
        idBrand: Int,
        idRequest: Int
    ): Flow<MultimoneyResult<StepByStep?>> =
        fetchData(
            apolloCall = graphqlApi.queryStepByStep(user, idBrand, idRequest),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )

    override suspend fun mutationGlobalRequest(
        pkUser: Int,
        status: Int,
        idProfessionType: Int,
        idCivilStatusType: Long,
        birthday: String,
        expirationDate: String,
        idGender: Long,
        companyName: String,
        aboutCompany: String,
        idAddressLevel1: Long,
        idAddressLevel2: Long,
        idAddressLevel3: Long,
        positionJob: String,
        idEconomicActivity: Long,
        income: Double,
        addressDetail: String,
        fullJobAddress: String,
        user: String,
        idBrand: Int,
        currentStep: String,
        institutionPension: String,
        specifiesIncomeSource: String,
        beneficiaries: List<Beneficiary>,
        entrepreneurship: String,
        legalID: String,
        isActivityOfArt15: Boolean,
        isUSCitizen: Boolean,
        isPEP: Boolean,
        isUSTaxPayer: Boolean,
        isTaxPayer: Boolean,
        idJobLevel2: Long,
        idJobLevel3: Long
    ): Flow<MultimoneyResult<GlobalRequest?>> = fetchData(
        apolloCall = graphqlApi.mutationGlobalRequest(
            pkUser = pkUser,
            status = status,
            idProfessionType = idProfessionType,
            idCivilStatusType = idCivilStatusType,
            birthday = birthday,
            expirationDate = expirationDate,
            idGender = idGender,
            companyName = companyName,
            aboutCompany = aboutCompany,
            idAddressLevel1 = idAddressLevel1,
            idAddressLevel2 = idAddressLevel2,
            idAddressLevel3 = idAddressLevel3,
            positionJob = positionJob,
            idEconomicActivity = idEconomicActivity,
            income = income,
            addressDetail = addressDetail,
            fullJobAddress = fullJobAddress,
            user = user,
            idBrand = idBrand,
            currentStep = currentStep,
            institutionPension = institutionPension,
            specifiesIncomeSource = specifiesIncomeSource,
            beneficiaries = beneficiaries,
            entrepreneurship = entrepreneurship,
            legalID = legalID,
            isActivityOfArt15 = isActivityOfArt15,
            isUsCitizen = isUSCitizen,
            isPEP = isPEP,
            isUSTaxPayer = isUSTaxPayer,
            isTaxPayer = isTaxPayer,
            idJobLevel2 = idJobLevel2,
            idJobLevel3 = idJobLevel3
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun mutationSaveAutomatedSmartAccount(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        idRequest: Long
    ): Flow<MultimoneyResult<SaveSmartAccount?>> {
        return fetchData(
            apolloCall = graphqlApi.mutationSaveAutomatedSmartAccount(
                user,
                idBrand,
                identificationNumber,
                idRequest
            ),
            apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
        )
    }

    /**
     * fetch the list of the source of income catalog for the account smart flow
     */
    override suspend fun queryGeneralEconomicActivity(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GeneralEconomicActivityResult?>> {
        return fetchData(
            apolloCall = graphqlApi.queryGeneralEconomicActivity(user, idBrand),
            apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
        )
    }

    override suspend fun queryRelationship(
        user: String,
        idBrand: Int,
        option: Int
    ): Flow<MultimoneyResult<RelationshipData>> {
        return fetchData(
            apolloCall = graphqlApi.queryRelationship(user, idBrand, option),
            apolloCallMapper = { data -> Success(data.mapToDomain()) }
        )
    }

    override suspend fun subscriptionAccountContractEvent(
        idBrand: Int,
        idRequestSys: Long
    ): Flow<MultimoneyResult<AccountSmartContractResult?>> {
        return fetchSubscription(
            apolloCall = graphqlApi.subscriptionAccountSmartContractEvent(
                idBrand,
                idRequestSys
            ), apolloCallMapper = { data -> Success(data?.mapToDomain()) })
    }

    override suspend fun mutationInitialRequestSmartAccount(
        pkUser: Long,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<GlobalRequest?>> {
        return fetchData(
            apolloCall = graphqlApi.mutationInitialRequestSmartAccount(
                pkUser,
                idBrand,
                user
            ), apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            })
    }

    override suspend fun querySinpeAccount(
        user: String,
        idBrand: Int,
        identification: String,
        country: String,
        idAccount: Long,
        accountNumber: String
    ): Flow<MultimoneyResult<SinpeAccountResult?>> {
        return fetchData(
            apolloCall = graphqlApi.queryListSinpeAccount(
                user,
                idBrand,
                identification,
                country,
                idAccount,
                accountNumber
            ), apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            })
    }
}
