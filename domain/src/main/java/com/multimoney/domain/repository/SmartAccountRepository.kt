package com.multimoney.domain.repository

import androidx.paging.PagingData
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.accountsmart.SmartAccountSmall
import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.accountsmart.BankListTransfer365
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.CivilStatusResult
import com.multimoney.domain.model.accountsmart.ExchangeRateResult
import com.multimoney.domain.model.accountsmart.FavoriteACHResult
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivityResult
import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.accountsmart.LocalTransferResult
import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.accountsmart.PhonesResult
import com.multimoney.domain.model.accountsmart.Professions
import com.multimoney.domain.model.accountsmart.RelatedContact
import com.multimoney.domain.model.accountsmart.RelationshipData
import com.multimoney.domain.model.accountsmart.SaveSinpeAccount
import com.multimoney.domain.model.accountsmart.SaveSmartAccount
import com.multimoney.domain.model.accountsmart.SinpeAccountResult
import com.multimoney.domain.model.accountsmart.SinpeTransferResult
import com.multimoney.domain.model.accountsmart.SmartAccountStatusResult
import com.multimoney.domain.model.accountsmart.SmartAccountTypeResult
import com.multimoney.domain.model.accountsmart.SmartFavoriteResult
import com.multimoney.domain.model.accountsmart.SmartMovement
import com.multimoney.domain.model.accountsmart.SmartMovementsResult
import com.multimoney.domain.model.accountsmart.StepByStep
import com.multimoney.domain.model.accountsmart.VisaSmartPayment
import com.multimoney.domain.model.balance.Account
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
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
        idRequest: Long
    ): Flow<MultimoneyResult<StepByStep?>>

    suspend fun mutationGlobalRequest(
        pkUser: Int,
        status: Int,
        user: String,
        idBrand: Int,
        idGlobalRequest: Long,
        idProfessionType: Int?,
        idCivilStatusType: Long?,
        birthday: String?,
        expirationDate: String?,
        idGender: Long?,
        companyName: String?,
        aboutCompany: String?,
        idAddressLevel1: Long?,
        idAddressLevel2: Long?,
        idAddressLevel3: Long?,
        positionJob: String?,
        idEconomicActivity: Long?,
        income: Double?,
        addressDetail: String?,
        fullJobAddress: String?,
        currentStep: String?,
        institutionPension: String?,
        specifiesIncomeSource: String?,
        beneficiaries: List<Beneficiary>?,
        entrepreneurship: String?,
        legalID: String?,
        isActivityOfArt15: Boolean?,
        isUSCitizen: Boolean?,
        isPEP: Boolean?,
        isUSTaxPayer: Boolean?,
        isTaxPayer: Boolean?,
        idJobLevel1: Long?,
        idJobLevel2: Long?,
        idJobLevel3: Long?
    ): Flow<MultimoneyResult<GlobalRequest?>>

    suspend fun mutationSaveAutomatedSmartAccount(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        idRequest: Long
    ): Flow<MultimoneyResult<SaveSmartAccount?>>

    suspend fun mutationProcessTransferVisaToSmartVD(
        idCard: Long,
        tokenNumber: Long,
        identification: String,
        amount: String,
        currency: Int,
        description: String,
        cardMasked: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<VisaSmartPayment?>>

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
        accountNumber: String,
        isFavorite: Boolean?
    ): Flow<MultimoneyResult<SinpeAccountResult?>>

    suspend fun querySmartExchangeRate(
        user: String,
        identification: String,
        idBrand: Int,
        abbreviation: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): Flow<MultimoneyResult<ExchangeRateResult?>>

    suspend fun mutationManageSinpeAccountSave(
        user: String,
        idBrand: Int,
        identification: String,
        accountNumber: String,
        idCurrency: Long,
        nameAccount: String,
        country: String,
        idAccount: Long?,
        option: String?,
        email: String?,
        isFavorite: Boolean?
    ): Flow<MultimoneyResult<SaveSinpeAccount?>>

    suspend fun mutationManageSinpeAccountUpdate(
        user: String,
        idBrand: Int,
        identification: String,
        accountNumber: String,
        idCurrency: Long,
        nameAccount: String,
        idAccount: Int?,
        isFavorite: Boolean,
        idBank: Long,
        typeAccount: Long
    ): Flow<MultimoneyResult<SaveSinpeAccount?>>

    suspend fun mutationManageSinpeAccountDelete(
        user: String,
        idBrand: Int,
        identification: String,
        idAccount: Int?,
    ): Flow<MultimoneyResult<SaveSinpeAccount?>>

    suspend fun mutationProcessSinpeTransfer(
        pkUser: Int,
        identification: String,
        originCustomerIdentification: String,
        ibanAccountOrigin: String,
        originCustomerName: String,
        idCurrencyOrigin: String,
        ibanAccountDestination: String,
        destinationCustomerIdentification: String,
        destinationCustomerName: String,
        idCurrencyDestination: String,
        reasonOfTransfer: String,
        transferType: SmartSinpeTransferType,
        amountToTransfer: Double,
        exchangeRate: Double,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SinpeTransferResult?>>

    suspend fun mutationProcessLocalTransfer(
        pkUsuario: Int,
        user: String,
        idBrand: Int,
        originIdentification: String,
        idCurrencyOrigin: String,
        destinationIdentification: String,
        idCurrencyDestination: String,
        destinationAccountNumber: String,
        amount: Double,
        reason: String,
        accountToken: Long,
        exchangeRate: Double
    ): Flow<MultimoneyResult<LocalTransferResult?>>

    suspend fun queryRelatedContactsByPhone(
        user: String,
        idBrand: Int,
        contacts: List<RelatedContact>
    ): Flow<MultimoneyResult<PhonesResult?>>

    suspend fun querySmartAccountType(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SmartAccountTypeResult?>>

    suspend fun queryBankListTransfer365(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<BankListTransfer365?>>

    suspend fun mutationAddACHAccount(
        idBrand: Int,
        user: String,
        accountNumber: String,
        titularName: String,
        isFavorite: Boolean,
        typeAccountId: Int,
        destinationBankId: Int,
        description: String,
        identificationNumber: String,
        identificationTypeAccount: Int
    ): Flow<MultimoneyResult<ACHAccount?>>

    suspend fun mutationUpdateFavoriteContactSmart(
        idBrand: Int,
        user: String,
        idFavorite: Long?,
        idAccountType: Int?,
        idCustomer: Long,
        accountNumber: String,
        accountName: String?,
        email: String,
        active: Boolean,
        isFavorite: Boolean,
        phoneNumber: String?,
        idCurrencyAccount: Int?,
    ): Flow<MultimoneyResult<SmartFavoriteResult?>>


    suspend fun queryACHTransferFavoriteList(
        user: String,
        idBrand: Int,
        isFavorite: Boolean,
        identification: String
    ): Flow<MultimoneyResult<FavoriteACHResult?>>

    suspend fun querySmartAccounts(
        user: String,
        identification: String,
        idBrand: Int,
        accountStatus: Int
    ): Flow<MultimoneyResult<List<SmartAccountSmall>?>>

    suspend fun mutationUpdateSmartAccountStatus(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        newState: String,
        typeState: String,
        idAccountSysde: Long,
        idAccountRequest: Long
    ): Flow<MultimoneyResult<SmartAccountStatusResult?>>
}