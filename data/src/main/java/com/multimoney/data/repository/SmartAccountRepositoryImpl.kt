package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.smartaccount.mapToDomain
import com.multimoney.data.networking.SmartAccountApi
import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.accountsmart.CivilStatusResult
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivityResult
import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.accountsmart.Professions
import com.multimoney.domain.model.accountsmart.StepByStep
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SmartAccountRepositoryImpl @Inject constructor(
    private val smartApi: SmartAccountApi,
) : BaseRepository(),
    SmartAccountRepository {

    override suspend fun queryCivilStatus(
        pkUser: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<CivilStatusResult?>> =
        fetchData(apolloCall = smartApi.queryCivilStatus(pkUser, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomain())
            })

    override suspend fun queryProfessions(
        pkUser: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<Professions?>> =
        fetchData(apolloCall = smartApi.queryProfession(pkUser, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomain())
            })

    override suspend fun queryAddressLevelOne(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        fetchData(apolloCall = smartApi.queryAddressLevelOne(user, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomain())
            })

    override suspend fun queryAddressLevelTwo(
        user: String,
        idBrand: Int,
        idAddressLevel1: String,
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        fetchData(apolloCall = smartApi.queryAddressLevelTwo(user, idBrand, idAddressLevel1),
            apolloCallMapper = { data ->
                Success(data.mapToDomain())
            })

    override suspend fun queryAddressLevelThree(
        user: String,
        idBrand: Int,
        idAddressLevel1: String,
        idAddressLevel2: String,
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        fetchData(apolloCall = smartApi.queryAddressLevelThree(user, idBrand, idAddressLevel1, idAddressLevel2),
            apolloCallMapper = { data ->
                Success(data.mapToDomain())
            })

    override suspend fun queryNationality(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<Nationalities?>> =
        fetchData(apolloCall = smartApi.queryNationality(user, idBrand),
            apolloCallMapper = { data ->
                Success(data.mapToDomain())
            })

    override suspend fun queryStepByStep(
        user: String,
        idBrand: Int,
        idRequest: Int,
    ): Flow<MultimoneyResult<StepByStep?>> =
        fetchData(apolloCall = smartApi.queryStepByStep(user, idBrand, idRequest),
            apolloCallMapper = { data ->
                Success(data.mapToDomain())
            })

    override suspend fun mutationGlobalRequest(
        pkUser: Int,
        status: Int,
        idProfessionType: Int,
        idCivilStatusType: Long,
        birthday: String,
        expirationDate: String,
        idGender: Long,
        idAddressLevel1: Long,
        idAddressLevel2: Long,
        idAddressLevel3: Long,
        idEconomicActivity: Long,
        income: Int,
        addressDetail: String,
        isPEP: Boolean,
        user: String,
        idBrand: Int,
        currentStep: String,
    ): Flow<MultimoneyResult<GlobalRequest?>> =
        fetchData(apolloCall = smartApi.mutationGlobalRequest(
            pkUser,
            status,
            idProfessionType,
            idCivilStatusType,
            birthday,
            expirationDate,
            idGender,
            idAddressLevel1,
            idAddressLevel2,
            idAddressLevel3,
            idEconomicActivity,
            income,
            addressDetail,
            isPEP,
            user,
            idBrand,
            currentStep
        ), apolloCallMapper = { data -> Success(data.mapToDomain()) })

    /**
     * fetch the list of the source of income catalog for the account smart flow
     */
    override suspend fun queryGeneralEconomicActivity(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GeneralEconomicActivityResult?>> {
        return fetchData(
            apolloCall = smartApi.queryGeneralEconomicActivity(user, idBrand),
            apolloCallMapper = { data -> Success(data.mapToDomain()) }
        )
    }
}
