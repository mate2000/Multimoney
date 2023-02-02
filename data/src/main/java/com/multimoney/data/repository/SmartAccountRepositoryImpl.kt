package com.multimoney.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.smartaccount.mapToDomain
import com.multimoney.data.mapper.smartaccount.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.paging.SmartMovementsPagingSource
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto
import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.accountsmart.BankListTransfer365
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.CivilStatusResult
import com.multimoney.domain.model.accountsmart.ExchangeRateResult
import com.multimoney.domain.model.accountsmart.FavoriteACHResult
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivityResult
import com.multimoney.domain.model.accountsmart.GlobalRequest
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
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.domain.repository.SmartAccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

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
        idRequest: Long
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
    ): Flow<MultimoneyResult<GlobalRequest?>> = fetchData(
        apolloCall = graphqlApi.mutationGlobalRequest(
            pkUser = pkUser,
            status = status,
            user = user,
            idBrand = idBrand,
            idGlobalRequest = idGlobalRequest,
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
            idJobLevel1 = idJobLevel1,
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

    override suspend fun mutationProcessTransferVisaToSmartVD(
        idCard: Long,
        tokenNumber: Long,
        identification: String,
        amount: String,
        currency: Int,
        description: String,
        cardMasked: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<VisaSmartPayment?>> =
        fetchData(
            apolloCall = graphqlApi.mutationProcessTransferVisaToSmartVD(
                idCard,
                tokenNumber,
                identification,
                amount,
                currency,
                description,
                cardMasked,
                user,
                idBrand
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

    override suspend fun subscriptionAccountContractEvent(
        idBrand: Int,
        idRequestSys: Long
    ): Flow<MultimoneyResult<AccountSmartContractResult?>> {
        return fetchSubscription(
            apolloCall = graphqlApi.subscriptionAccountSmartContractEvent(
                idBrand,
                idRequestSys
            ),
            apolloCallMapper = { data -> Success(data?.mapToDomain()) }
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
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
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
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun querySmartExchangeRate(
        user: String,
        identification: String,
        idBrand: Int,
        abbreviation: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): Flow<MultimoneyResult<ExchangeRateResult?>> {
        return fetchData(
            apolloCall = graphqlApi.querySmartExchangeRate(
                user = user,
                identification = identification,
                idBrand = idBrand,
                abbreviation = abbreviation,
                idOriginCurrency = idOriginCurrency,
                idDestinationCurrency = idDestinationCurrency,
                amount = amount
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    // Todo Remember to include Email in the Apollo model when the backend adds the field
    override suspend fun mutationManageSinpeAccountSave(
        user: String,
        idBrand: Int,
        identification: String,
        accountNumber: String,
        idCurrency: Long,
        nameAccount: String,
        country: String,
        idAccount: Long?,
        option: String?,
        email: String?
    ): Flow<MultimoneyResult<SaveSinpeAccount?>> = fetchData(
        apolloCall = graphqlApi.mutationManageSinpeAccountSave(
            user,
            idBrand,
            identification,
            accountNumber,
            idCurrency,
            nameAccount,
            country,
            idAccount,
            option
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationProcessSinpeTransfer(
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
    ): Flow<MultimoneyResult<SinpeTransferResult?>> {
        return fetchData(
            apolloCall = graphqlApi.mutationProcessSinpeTransfer(
                pkUser = pkUser,
                identification = identification,
                originCustomerIdentification = originCustomerIdentification,
                ibanAccountOrigin = ibanAccountOrigin,
                originCustomerName = originCustomerName,
                idCurrencyOrigin = idCurrencyOrigin,
                ibanAccountDestination = ibanAccountDestination,
                destinationCustomerIdentification = destinationCustomerIdentification,
                destinationCustomerName = destinationCustomerName,
                idCurrencyDestination = idCurrencyDestination,
                reasonOfTransfer = reasonOfTransfer,
                type = transferType.type,
                amountToTransfer = amountToTransfer,
                exchangeRate = exchangeRate,
                idBrand = idBrand,
                user = user
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun queryRelatedContactsByPhone(
        user: String,
        idBrand: Int,
        contacts: List<RelatedContact>
    ): Flow<MultimoneyResult<PhonesResult?>> {
        return fetchData(
            apolloCall = graphqlApi.queryRelatedContactsByPhone(
                user = user,
                idBrand = idBrand,
               contacts = contacts
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun querySmartAccountType(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SmartAccountTypeResult?>> {
        return fetchData(graphqlApi.querySmartAccountType(
            idBrand = idBrand,
            user = user
        ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun queryBankListTransfer365(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<BankListTransfer365?>> {
        return fetchData(graphqlApi.queryBankListTransfer365(
            idBrand = idBrand,
            user = user
        ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun mutationAddACHAccount(
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
    ): Flow<MultimoneyResult<ACHAccount?>> {
        return fetchData(graphqlApi.mutationAddACHAccount(
            idBrand = idBrand,
            user = user,
            accountNumber = accountNumber,
            titularName = titularName,
            isFavorite = isFavorite,
            typeAccountId = typeAccountId,
            destinationBankId = destinationBankId,
            description = description,
            identificationNumber = identificationNumber,
            identificationTypeAccount = identificationTypeAccount
        ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun mutationUpdateFavoriteContactSmart(
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
        idCurrencyAccount: Int?
    ): Flow<MultimoneyResult<SmartFavoriteResult?>> {
        return fetchData(graphqlApi.mutationUpdateSmartFavoriteContact(
            idBrand = idBrand,
            user = user,
            idFavorite = idFavorite,
            idAccountType = idAccountType,
            idCustomer = idCustomer,
            accountNumber = accountNumber,
            accountName = accountName,
            phoneNumber = phoneNumber,
            email = email,
            active = active,
            isFavorite = isFavorite,
            idCurrencyAccount = idCurrencyAccount
        ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun queryACHTransferFavoriteList(
        user: String,
        idBrand: Int,
        isFavorite: Boolean,
        identification: String
    ): Flow<MultimoneyResult<FavoriteACHResult?>> {
        return fetchData(
            apolloCall = graphqlApi.queryACHTransferFavoriteList(
                user,
                idBrand,
                isFavorite,
                identification,
            ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun querySmartAccounts(
        user: String,
        identification: String,
        idBrand: Int,
        accountStatus: Int
    ): Flow<MultimoneyResult<List<AccountSmartForBuyCrypto>?>> {
        return fetchData(graphqlApi.querySmartAccounts(
            user,
            identification,
            idBrand,
            accountStatus
        ),
            apolloCallMapper = { data ->
                Success(data.mapToDomainModel())
            }
        )
    }

    override suspend fun mutationUpdateSmartAccountStatus(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        newState: String,
        typeState: String,
        idAccountSysde: Long,
        idAccountRequest: Long
    ): Flow<MultimoneyResult<SmartAccountStatusResult?>> {
        return fetchData(graphqlApi.mutationUpdateSmartAccountStatus(
            user,
            idBrand,
            identificationNumber,
            newState,
            typeState,
            idAccountSysde,
            idAccountRequest
        ), apolloCallMapper = { data -> Success(data.mapToDomain()) })
    }
}