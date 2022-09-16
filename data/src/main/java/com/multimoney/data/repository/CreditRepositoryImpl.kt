package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.credit.mapToDomainModel
import com.multimoney.data.networking.CreditApi
import com.multimoney.domain.model.credit.CreditApplication
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.PaymentAmount
import com.multimoney.domain.model.credit.SaveCreditFlowStep
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Message
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.CreditRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreditRepositoryImpl @Inject constructor(
    private val creditApi: CreditApi
) : BaseRepository(),
    CreditRepository {
    override suspend fun queryCreditOffer(pkUser: Int, idBrand: Int): Flow<MultimoneyResult<CreditOffer?>> = fetchData(
        apolloCall = creditApi.queryCreditOffer(pkUser, idBrand),
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
        apolloCall = creditApi.queryPaymentAmount(amount, months, idProduct, currencySymbol, user, idBrand),
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
        apolloCall = creditApi.mutationSaveCreditApplication(
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
            if (data.saveCreditApplication?.status == null || data.saveCreditApplication.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        })

    override suspend fun queryScreenConfig(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = creditApi.queryScreenConfig(pkUser.toInt(), user, idBrand, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryHomeProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = creditApi.queryHomeProvince(pkUser, user, idBrand, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryHomeCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = creditApi.queryHomeCanton(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryHomeDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = creditApi.queryHomeDistrict(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCompanyProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = creditApi.queryCompanyProvince(pkUser, user, idBrand, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCompanyCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = creditApi.queryCompanyCanton(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCompanyDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> = fetchData(
        apolloCall = creditApi.queryCompanyDistrict(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest),
        apolloCallMapper = { data ->
            Success(data.mapToDomainModel())
        }
    )

    override suspend fun mutationSaveCreditFlowStep(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion?>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): Flow<MultimoneyResult<SaveCreditFlowStep?>> = fetchData(
        apolloCall = creditApi.mutationSaveCreditFlowStep(
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
    ) :Flow<MultimoneyResult<String>> = fetchData(
        apolloCall = creditApi.mutationTermsAndConditions(
            user, idBrand, systemInDarkTheme
        ),
        apolloCallMapper = { data ->
            Success(data.terminsAndConditions?.terminsAndConditionsHtml ?: "")
        }
    )
}
