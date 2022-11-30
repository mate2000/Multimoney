package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.smartaccount.mapToDomain
import com.multimoney.data.mapper.smartaccount.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.CivilStatusResult
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivityResult
import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.accountsmart.Professions
import com.multimoney.domain.model.accountsmart.RelationshipData
import com.multimoney.domain.model.accountsmart.StepByStep
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SmartAccountRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(), SmartAccountRepository {

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
}
