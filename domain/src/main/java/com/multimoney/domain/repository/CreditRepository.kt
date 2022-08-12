package com.multimoney.domain.repository

import com.multimoney.domain.model.credit.CompanyAddress
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

    suspend fun queryCompanyAddress(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CompanyAddress?>>

    suspend fun queryCompanyAddressSV(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CompanyAddress?>>
}