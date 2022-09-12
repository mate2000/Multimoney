package com.multimoney.domain.repository

import com.multimoney.domain.model.credit.CreditApplication
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.PaymentAmount
import com.multimoney.domain.model.credit.SaveCreditFlowStep
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface CreditRepository {
    suspend fun queryCreditOffer(
        pkUser: Int,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditOffer?>>

    suspend fun queryPaymentAmount(
        amount: Int,
        months: String,
        idProduct: String,
        currencySymbol: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<PaymentAmount?>>

    suspend fun mutationSaveCreditApplication(
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
    ): Flow<MultimoneyResult<CreditApplication?>>

    suspend fun queryScreenConfig(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryHomeProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryHomeCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryHomeDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryCompanyProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryCompanyCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun queryCompanyDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>

    suspend fun mutationSaveCreditFlowStep(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion?>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): Flow<MultimoneyResult<SaveCreditFlowStep?>>
}