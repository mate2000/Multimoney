package com.multimoney.domain.repository

import com.multimoney.domain.model.credit.CompanyCanton
import com.multimoney.domain.model.credit.CompanyDistrict
import com.multimoney.domain.model.credit.CompanyProvince
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.PaymentAmount
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

    suspend fun queryCompanyProvince(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyProvince?>?>>

    suspend fun queryCompanyCanton(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyCanton?>?>>

    suspend fun queryCompanyDistrict(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyDistrict?>?>>
}