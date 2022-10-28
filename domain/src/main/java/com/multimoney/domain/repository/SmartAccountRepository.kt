package com.multimoney.domain.repository

import com.multimoney.domain.model.accountsmart.AddressesLevelTwo
import com.multimoney.domain.model.accountsmart.CivilStatusResult
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivityResult
import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.accountsmart.Professions
import com.multimoney.domain.model.accountsmart.StepByStep
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SmartAccountRepository {

    suspend fun queryCivilStatus(
        pkUser: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<CivilStatusResult?>>

    suspend fun queryProfessions(pkUser: String, idBrand: Int): Flow<MultimoneyResult<Professions?>>

    suspend fun queryAddressLevelTwo(
        user: String,
        idBrand: Int,
        idAddressLevel1: String,
    ): Flow<MultimoneyResult<AddressesLevelTwo?>>

    suspend fun queryNationality(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<Nationalities?>>

    suspend fun queryStepByStep(
        user: String,
        idBrand: Int,
        idRequest: Int,
    ): Flow<MultimoneyResult<StepByStep?>>

    suspend fun mutationGlobalRequest(
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
        specifiesIncomeSource: String
    ): Flow<MultimoneyResult<GlobalRequest?>>

    suspend fun queryGeneralEconomicActivity(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<GeneralEconomicActivityResult?>>
}
