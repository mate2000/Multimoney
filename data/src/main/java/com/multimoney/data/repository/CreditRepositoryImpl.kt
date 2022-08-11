package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.credit.mapToDomainModel
import com.multimoney.data.networking.CreditApi
import com.multimoney.domain.model.credit.CreditApplication
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.PaymentAmount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Message
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(
    private val creditApi: CreditApi
) : BaseRepository(),
    CreditRepository {
    override suspend fun queryCreditOffer(pkUser: Int, idBrand: Int): Flow<MultimoneyResult<CreditOffer?>> = fetchData(
        apolloCall = creditApi.queryCreditOffer(pkUser, idBrand),
        apolloCallMapper = { data ->
            MultimoneyResult.Success(data.mapToDomainModel())
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
            MultimoneyResult.Success(data.mapToDomainModel())
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
        tractAmount: Double
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
            tractAmount
        ),
        apolloCallMapper = { data ->
            if (data.saveCreditApplication?.status == null || data.saveCreditApplication.status == 0) {
                Success(data.mapToDomainModel())
            } else {
                Message(data.mapToDomainModel())
            }
        }
    )
}
