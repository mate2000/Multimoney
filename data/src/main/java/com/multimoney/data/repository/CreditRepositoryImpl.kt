package com.multimoney.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.credit.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.paging.CreditMovementsPagingSource
import com.multimoney.domain.model.credit.AccountStatement
import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.credit.BanksAndRegularExpression
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.credit.CreditApplication
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.credit.CreditExtensionAmount
import com.multimoney.domain.model.credit.CreditExtensionDetail
import com.multimoney.domain.model.credit.CreditExtensionMessage
import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.CreditMovement
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.DestinyAccount
import com.multimoney.domain.model.credit.ExchangeRate
import com.multimoney.domain.model.credit.GetInfoDeposit
import com.multimoney.domain.model.credit.PaymentAmount
import com.multimoney.domain.model.credit.PaymentPoint
import com.multimoney.domain.model.credit.ProcessCreditExtensionDetail
import com.multimoney.domain.model.credit.ProcessPaymentList
import com.multimoney.domain.model.credit.PromissoryNoteDetail
import com.multimoney.domain.model.credit.SaveClientBankAccount
import com.multimoney.domain.model.credit.SaveCreditFlowStep
import com.multimoney.domain.model.credit.SaveCreditOffer
import com.multimoney.domain.model.credit.SaveCreditOperation
import com.multimoney.domain.model.credit.SaveTermsAndConditionsCredit
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Message
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(),
    CreditRepository {
    override suspend fun queryCreditOffer(pkUser: Int, idBrand: Int): Flow<MultimoneyResult<CreditOffer?>> = fetchData(
        apolloCall = graphqlApi.queryCreditOffer(pkUser, idBrand),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryPaymentAmount(
        amount: Int,
        months: String?,
        idProduct: String,
        currencySymbol: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<PaymentAmount?>> = fetchData(
        apolloCall = graphqlApi.queryPaymentAmount(amount, months, idProduct, currencySymbol, user, idBrand),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryGetPromissoryNoteDetail(
        idBrand: Int,
        idLoanClient: Int,
        pageNumber: Int,
        pageSize: Int,
        option: String
    ): Flow<MultimoneyResult<PromissoryNoteDetail?>> = fetchData(
        apolloCall = graphqlApi.queryGetPromissoryNoteDetail(idBrand, idLoanClient, pageNumber, pageSize, option),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun getPagedCreditMovements(
        idBrand: Int,
        idLoanClient: Int,
        pageSize: Int,
        option: String
    ): Flow<PagingData<CreditMovement>> {
        return Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = {
                CreditMovementsPagingSource(
                    graphqlApi,
                    idBrand,
                    idLoanClient,
                    option,
                    pageSize
                )
            }
        ).flow
    }

    override suspend fun mutationSaveCreditApplication(
        idUserRequest: Int,
        pkUser: Int,
        descPromotion: String,
        interestRate: String?,
        symbolCurrency: String,
        descCurrency: String,
        idProduct: Int,
        idPromotion: Int,
        months: String?,
        commissionPercentage: String?,
        paymentDate: String,
        paymentAmount: String,
        user: String,
        idBrand: Int,
        selectedAmount: Double,
        minimumAmount: Double?,
        creditLimit: Double?,
        tractAmount: Double?,
        currentStep: String
    ): Flow<MultimoneyResult<CreditApplication?>> = fetchData(
        apolloCall = graphqlApi.mutationSaveCreditApplication(
            idUserRequest,
            pkUser,
            descPromotion,
            interestRate,
            symbolCurrency,
            descCurrency,
            idProduct,
            idPromotion,
            months ?: "",
            commissionPercentage ?: "",
            paymentDate,
            paymentAmount,
            user,
            idBrand,
            selectedAmount,
            minimumAmount ?: 0.0,
            creditLimit ?: 0.0,
            tractAmount ?: 0.0,
            currentStep
        ),
        apolloCallMapper = { data ->
            if (data.saveCreditApplication.status == null || data.saveCreditApplication.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun queryScreenConfig(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryScreenConfig(pkUser.toInt(), user, idBrand, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryHomeProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryHomeProvince(pkUser, user, idBrand, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryHomeCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryHomeCanton(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryHomeDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryHomeDistrict(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCompanyProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryCompanyProvince(pkUser, user, idBrand, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCompanyCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryCompanyCanton(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCompanyDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryCompanyDistrict(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryProfession(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryProfession(pkUser, user, idBrand, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryOccupation(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryOccupation(pkUser, user, idBrand, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryEmploymentSituation(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = graphqlApi.queryEmploymentSituation(
            pkUser = pkUser,
            user = user,
            idBrand = idBrand,
            idUserRequest = idUserRequest
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationSaveCreditFlowStep(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): Flow<MultimoneyResult<SaveCreditFlowStep?>> = fetchData(
        apolloCall = graphqlApi.mutationSaveCreditFlowStep(
            user,
            idBrand,
            infoQuestion,
            idLogUserRequest,
            idUser,
            currentStep
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationSaveCreditOffer(
        pkUser: Long,
        idUserRequest: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveCreditOffer>> = fetchData(
        apolloCall = graphqlApi.mutationSaveCreditOffer(
            pkUser = pkUser,
            idUserRequest = idUserRequest,
            idBrand = idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationSaveTermsAndConditionsCredit(
        user: String,
        idBrand: Int,
        pkUser: Long,
        currentFlow: String,
        identification: String
    ): Flow<MultimoneyResult<SaveTermsAndConditionsCredit>> = fetchData(
        apolloCall = graphqlApi.mutationSaveTermsAndConditionsCredit(
            user = user,
            idBrand = idBrand,
            pkUser = pkUser,
            currentFlow = currentFlow,
            identification = identification
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationTermsAndConditions(
        user: String,
        idBrand: Int,
        systemInDarkTheme: Boolean
    ): Flow<MultimoneyResult<String>> = fetchData(
        apolloCall = graphqlApi.mutationTermsAndConditions(
            user,
            idBrand,
            systemInDarkTheme
        ),
        apolloCallMapper = { data ->
            Success(data.terminsAndConditions.terminsAndConditionsHtml)
        }
    )

    override suspend fun queryGetClientBankAccount(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        process: String
    ): Flow<MultimoneyResult<List<ClientBankAccount?>?>> = fetchData(
        apolloCall = graphqlApi.queryGetClientBankAccount(
            user,
            idBrand,
            idClient,
            idLoanClient,
            process
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryBanksAndRegularExpression(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<BanksAndRegularExpression>> = fetchData(
        apolloCall = graphqlApi.queryBanksAndRegularExpression(
            pkUser,
            user,
            idBrand,
            idUserRequest
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryGetPaymentPoints(
        idBrand: Int
    ): Flow<MultimoneyResult<List<PaymentPoint?>?>> = fetchData(
        apolloCall = graphqlApi.queryGetPaymentPoints(
            idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryGetExchangeRateCredit(
        idBrand: Int,
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): Flow<MultimoneyResult<ExchangeRate?>> = fetchData(
        apolloCall = graphqlApi.queryGetExchangeCreditRate(
            idBrand,
            user,
            identification,
            idOriginCurrency,
            idDestinationCurrency,
            amount
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationProcessPaymentList(
        user: String,
        idBrand: Int,
        customerId: Int,
        identification: String,
        originAccountNumber: String,
        destinyAccountNumber: String,
        currencyId: String,
        customerName: String,
        description: String,
        destinyAccount: List<DestinyAccount>,
        amount: Any
    ): Flow<MultimoneyResult<ProcessPaymentList?>> = fetchData(
        apolloCall = graphqlApi.mutationProcessPaymentList(
            user = user,
            idBrand = idBrand,
            customerId = customerId,
            identification = identification,
            originAccountNumber = originAccountNumber,
            destinyAccountNumber = destinyAccountNumber,
            currencyId = currencyId,
            customerName = customerName,
            description = description,
            destinyAccount = destinyAccount,
            amount = amount
        ),
        apolloCallMapper = { data ->
            if (data.proccessPaymentList?.status == null || data.proccessPaymentList.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun subscriptionCreditContractEvent(
        idPrint: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditContractEvent?>> = fetchSubscription(
        apolloCall = graphqlApi.subscriptionCreditContractEvent(idPrint, idBrand),
        apolloCallMapper = { data ->
            Success(data?.mapToDomainModel())
        }
    )

    override suspend fun mutationSaveCreditOperation(
        idUserRequest: Long,
        pkUser: Long,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveCreditOperation>> = fetchData(
        apolloCall = graphqlApi.mutationSaveCreditOperation(
            idUserRequest,
            pkUser,
            user,
            idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationActivateClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long,
        idCurrency: Int
    ): Flow<MultimoneyResult<AutomaticDebit?>> = fetchData(
        apolloCall = graphqlApi.mutationActivatedClientAutomaticDebit(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            origin = origin,
            idAccount = idAccount,
            idCurrency = idCurrency
        ),
        apolloCallMapper = { data ->
            if (data.activatedClientAutomaticDebit.status == null || data.activatedClientAutomaticDebit.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun mutationDeactivateClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long
    ): Flow<MultimoneyResult<AutomaticDebit?>> = fetchData(
        apolloCall = graphqlApi.mutationDeactivatedClientAutomaticDebit(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            origin = origin,
            idAccount = idAccount
        ),
        apolloCallMapper = { data ->
            if (data.deactivatedClientAutomaticDebit.status == null || data.deactivatedClientAutomaticDebit.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun mutationDeactivateCardAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        idCard: Long
    ): Flow<MultimoneyResult<AutomaticDebit?>> = fetchData(
        apolloCall = graphqlApi.mutationDeactivatedCardAutomaticDebit(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            idCard = idCard
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryGetClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int
    ): Flow<MultimoneyResult<List<ClientBankAccount?>?>> = fetchData(
        apolloCall = graphqlApi.queryGetClientAutomaticDebit(
            user,
            idBrand,
            idClient,
            idLoanClient
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryGetCardAutomaticDebit(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>> = fetchData(
        apolloCall = graphqlApi.queryGetCardAutomaticDebit(
            user,
            identification,
            idBrand,
            idClient,
            idLoanClient
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCreditExtensionAmount(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditExtensionAmount?>> = fetchData(
        apolloCall = graphqlApi.queryCreditExtensionAmount(idClient, currency, user, idBrand),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCreditExtensionMessage(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int,
        amountRequest: Double,
        idLoanClient: Long,
        quotaMax: Double,
        idProductBase: Int,
        cicle: Int
    ): Flow<MultimoneyResult<CreditExtensionMessage?>> = fetchData(
        apolloCall = graphqlApi.queryCreditExtensionMessage(
            idClient,
            currency,
            user,
            idBrand,
            amountRequest,
            idLoanClient,
            quotaMax,
            idProductBase,
            cicle
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationSendCreditContractEvent(
        idImpresion: Long,
        idBrand: Int,
        link: String,
        active: Boolean,
        statusEvicertia: String,
        statusOnfido: String,
        currentStep: String
    ): Flow<MultimoneyResult<CreditContractEvent?>> = fetchData(
        apolloCall = graphqlApi.mutationSendCreditContractEvent(
            idImpresion,
            idBrand,
            link,
            active,
            statusEvicertia,
            statusOnfido,
            currentStep
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationSaveClientBankAccount(
        idClient: Long,
        idBank: Int,
        accountNumber: String,
        idCurrency: Int,
        idAccountType: Int?,
        idLoanClient: Long,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveClientBankAccount?>> = fetchData(
        apolloCall = graphqlApi.mutationSaveClientBankAccount(
            idClient = idClient,
            idBank = idBank,
            accountNumber = accountNumber,
            idCurrency = idCurrency,
            idAccountType = idAccountType,
            idLoanClient = idLoanClient,
            user = user,
            idBrand = idBrand
        ),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }

    )

    override suspend fun mutationSaveCreditExtensionDetail(
        pkUser: Int,
        idBrand: Int,
        user: String,
        accountNumber: String,
        amount: Double,
        month: Int,
        pkPromotionMonth: Int,
        nextPaymentDate: String,
        quota: Double,
        quotaTotal: Double,
        comissionDisbursement: Double,
        rateInterestNormalLoan: Double,
        rateInterestNormalRegular: Double,
        cicle: Int,
        idProduct: Int,
        descriptionPromotionTerm: String,
        pkPromotion: Int
    ): Flow<MultimoneyResult<CreditExtensionDetail?>> = fetchData(
        apolloCall = graphqlApi.mutationSaveCreditExtensionDetail(
            pkUser,
            idBrand,
            user,
            accountNumber,
            amount,
            month,
            pkPromotionMonth,
            nextPaymentDate,
            quota,
            quotaTotal,
            comissionDisbursement,
            rateInterestNormalLoan,
            rateInterestNormalRegular,
            cicle,
            idProduct,
            descriptionPromotionTerm,
            pkPromotion
        ),
        apolloCallMapper = { data ->
            if (data.saveCreditExtensionDetail?.status == null || data.saveCreditExtensionDetail.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun mutationProcessCreditExtensionDetail(
        user: String,
        pkUser: Int,
        idBrand: Int,
        idFlowControl: Any,
        currency: String,
        accountNumber: String,
        bankAccount: String,
        idBankAccount: Any,
        idLoanForm: Any,
        loanForm: String,
        idLoanClient: Int,
        phoneNumber: String,
        userEmail: String
    ): Flow<MultimoneyResult<ProcessCreditExtensionDetail?>> = fetchData(
        apolloCall = graphqlApi.mutationProcessCreditExtensionDetail(
            user,
            pkUser,
            idBrand,
            idFlowControl,
            currency,
            accountNumber,
            bankAccount,
            idBankAccount,
            idLoanForm,
            loanForm,
            idLoanClient,
            phoneNumber,
            userEmail
        ),
        apolloCallMapper = { data ->
            if (data.processCreditExtensionDetail?.status == null || data.processCreditExtensionDetail.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun queryGetInfoDeposit(
        idBrand: Int,
        idPrint: Long,
        user: String
    ): Flow<MultimoneyResult<GetInfoDeposit?>> = fetchData(
        apolloCall = graphqlApi.queryGetInfoDeposit(idBrand, idPrint, user),
        apolloCallMapper = { data ->
            if (data.getInfoDeposit.status == null || data.getInfoDeposit.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )

    override suspend fun queryAccountStatement(
        creditNumber: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<AccountStatement?>> = fetchData(
        apolloCall = graphqlApi.queryAccountStatement(creditNumber = creditNumber, user = user, idBrand = idBrand),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )
}
