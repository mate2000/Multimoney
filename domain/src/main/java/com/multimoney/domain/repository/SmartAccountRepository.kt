package com.multimoney.domain.repository

import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import androidx.paging.PagingData
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
import kotlinx.coroutines.flow.Flow

interface SmartAccountRepository {

    suspend fun queryGetCoreBankMovements(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        pageNumber: Int,
        pageSize: Int,
        monthDate: String?
    ): Flow<MultimoneyResult<SmartMovementsResult?>>

    suspend fun getPagedMovements(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        pageSize: Int,
        monthDate: String?
    ): Flow<PagingData<SmartMovement>>

    suspend fun queryCivilStatus(
        pkUser: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CivilStatusResult?>>

    suspend fun queryProfessionsSmart(
        pkUser: String,
        idBrand: Int
    ): Flow<MultimoneyResult<Professions?>>

    suspend fun queryAddressLevelOne(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<AddressesLevel?>>

    suspend fun queryAddressLevelTwo(
        user: String,
        idBrand: Int,
        idAddressLevel1: String
    ): Flow<MultimoneyResult<AddressesLevel?>>

    suspend fun queryAddressLevelThree(
        user: String,
        idBrand: Int,
        idAddressLevel1: String,
        idAddressLevel2: String
    ): Flow<MultimoneyResult<AddressesLevel?>>

    suspend fun queryNationality(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<Nationalities?>>

    suspend fun queryStepByStep(
        user: String,
        idBrand: Int,
        idRequest: Int
    ): Flow<MultimoneyResult<StepByStep?>>

    suspend fun mutationGlobalRequest(
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
    ): Flow<MultimoneyResult<GlobalRequest?>>

    suspend fun mutationSaveAutomatedSmartAccount(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        idRequest: Long
    ): Flow<MultimoneyResult<SaveSmartAccount?>>

    suspend fun queryGeneralEconomicActivity(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GeneralEconomicActivityResult?>>

    suspend fun queryRelationship(
        user: String,
        idBrand: Int,
        option: Int
    ): Flow<MultimoneyResult<RelationshipData>>

    suspend fun subscriptionAccountContractEvent(
        idBrand: Int,
        idRequestSys: Long
    ): Flow<MultimoneyResult<AccountSmartContractResult?>>

    suspend fun mutationInitialRequestSmartAccount(
        pkUser: Long,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<GlobalRequest?>>

    suspend fun querySinpeAccount(
        user: String,
        idBrand: Int,
        identification: String,
        country: String,
        idAccount: Long,
        accountNumber: String
    ): Flow<MultimoneyResult<SinpeAccountResult?>>
}
