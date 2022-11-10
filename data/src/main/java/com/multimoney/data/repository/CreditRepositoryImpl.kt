package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.credit.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.credit.BanksAndRegularExpression
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.credit.CreditApplication
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.DestinyAccount
import com.multimoney.domain.model.credit.ExchangeRate
import com.multimoney.domain.model.credit.PaymentAmount
import com.multimoney.domain.model.credit.PaymentPoint
import com.multimoney.domain.model.credit.ProcessPaymentList
import com.multimoney.domain.model.credit.SaveCreditFlowStep
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Message
import com.multimoney.domain.model.util.MultimoneyResult.Success
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
        months: String,
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

    override suspend fun mutationSaveCreditApplication(
        idUserRequest: Int,
        pkUser: Int,
        descPromotion: String,
        interestRate: String,
        symbolCurrency: String,
        descCurrency: String,
        idProduct: Int,
        idPromotion: Int,
        months: String,
        commissionPercentage: String,
        paymentDate: String,
        paymentAmount: String,
        user: String,
        idBrand: Int,
        selectedAmount: Double,
        minimumAmount: Double,
        creditLimit: Double,
        tractAmount: Double,
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
            months,
            commissionPercentage,
            paymentDate,
            paymentAmount,
            user,
            idBrand,
            selectedAmount,
            minimumAmount,
            creditLimit,
            tractAmount,
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
            Success(data.terminsAndConditions.terminsAndConditionsHtml ?: "")
        }
    )

    override suspend fun queryGetClientBankAccount(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int
    ): Flow<MultimoneyResult<List<ClientBankAccount?>?>> = fetchData(
        apolloCall = graphqlApi.queryGetClientBankAccount(
            user,
            idBrand,
            idClient,
            idLoanClient
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
}
