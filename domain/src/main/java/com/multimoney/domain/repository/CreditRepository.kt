package com.multimoney.domain.repository

import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.credit.BanksAndRegularExpression
import com.multimoney.domain.model.credit.CardVisaDirect
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.credit.CreditApplication
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.credit.CreditExtensionAmount
import com.multimoney.domain.model.credit.CreditExtensionDetail
import com.multimoney.domain.model.credit.CreditExtensionMessage
import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.DestinyAccount
import com.multimoney.domain.model.credit.ExchangeRate
import com.multimoney.domain.model.credit.PaymentAmount
import com.multimoney.domain.model.credit.PaymentPoint
import com.multimoney.domain.model.credit.ProcessPaymentList
import com.multimoney.domain.model.credit.SaveClientBankAccount
import com.multimoney.domain.model.credit.SaveCreditFlowStep
import com.multimoney.domain.model.credit.SaveCreditOperation
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface CreditRepository {
    suspend fun queryCreditOffer(
        pkUser: Int,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditOffer?>>

    suspend fun queryPaymentAmount(
        amount: Int,
        months: String?,
        idProduct: String,
        currencySymbol: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<PaymentAmount?>>

    suspend fun mutationSaveCreditApplication(
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
    ): Flow<MultimoneyResult<CreditApplication?>>

    suspend fun queryScreenConfig(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryHomeProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryHomeCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryHomeDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryCompanyProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryCompanyCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryCompanyDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryProfession(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryOccupation(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun mutationSaveCreditFlowStep(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): Flow<MultimoneyResult<SaveCreditFlowStep?>>

    suspend fun mutationTermsAndConditions(
        user: String,
        idBrand: Int,
        systemInDarkTheme: Boolean
    ): Flow<MultimoneyResult<String>>

    suspend fun queryGetClientBankAccount(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int
    ): Flow<MultimoneyResult<List<ClientBankAccount?>?>>

    suspend fun queryListCardVD(
        user: String,
        idBrand: Int,
        identification: String
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>>

    suspend fun queryBanksAndRegularExpression(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<BanksAndRegularExpression>>

    suspend fun queryGetPaymentPoints(
        idBrand: Int
    ): Flow<MultimoneyResult<List<PaymentPoint?>?>>

    suspend fun queryGetExchangeRateCredit(
        idBrand: Int,
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): Flow<MultimoneyResult<ExchangeRate?>>

    suspend fun mutationProcessPaymentList(
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
    ): Flow<MultimoneyResult<ProcessPaymentList?>>

    suspend fun subscriptionCreditContractEvent(
        idPrint: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditContractEvent?>>

    suspend fun mutationSaveCreditOperation(
        idUserRequest: Long,
        pkUser: Long,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveCreditOperation>>

    suspend fun mutationActivateClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long,
        idCurrency: Int
    ): Flow<MultimoneyResult<AutomaticDebit?>>

    suspend fun mutationDeactivateClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long
    ): Flow<MultimoneyResult<AutomaticDebit?>>

    suspend fun queryGetClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int
    ): Flow<MultimoneyResult<List<ClientBankAccount?>?>>

    suspend fun queryCreditExtensionAmount(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditExtensionAmount?>>

    suspend fun queryCreditExtensionMessage(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int,
        amountRequest: Double,
        idLoanClient: Long,
        quotaMax: Double,
        idProductBase: Int,
        cicle: Int
    ): Flow<MultimoneyResult<CreditExtensionMessage?>>

    suspend fun mutationSaveCreditExtensionDetail(
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
    ): Flow<MultimoneyResult<CreditExtensionDetail?>>

    suspend fun mutationSendCreditContractEvent(
        idImpresion: Long,
        idBrand: Int,
        link: String,
        active: Boolean,
        statusEvicertia: String,
        statusOnfido: String,
        currentStep: String
    ): Flow<MultimoneyResult<CreditContractEvent?>>

    suspend fun mutationSaveClientBankAccount(
        idClient: Long,
        idBank: Int,
        accountNumber: String,
        idCurrency: Int,
        idAccountType: Int?,
        idLoanClient: Long,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveClientBankAccount?>>
}
