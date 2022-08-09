package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.credit.mapToDomainModel
import com.multimoney.data.networking.CreditApi
import com.multimoney.domain.model.credit.CompanyCanton
import com.multimoney.domain.model.credit.CompanyDistrict
import com.multimoney.domain.model.credit.CompanyProvince
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.credit.PaymentAmount
import com.multimoney.domain.model.util.MultimoneyResult
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

    override suspend fun queryCompanyProvince(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyProvince?>?>> = fetchData(
        apolloCall = creditApi.queryCompanyProvince(pkUser, user, idBrand),
        apolloCallMapper = { data ->
            MultimoneyResult.Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCompanyCanton(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyCanton?>?>> = fetchData(
        apolloCall = creditApi.queryCompanyCanton(pkUser, user, idBrand),
        apolloCallMapper =
        { data ->
            MultimoneyResult.Success(data.mapToDomainModel())
        }
    )

    override suspend fun queryCompanyDistrict(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyDistrict?>?>> = fetchData(
        apolloCall = creditApi.queryCompanyDistrict(pkUser, user, idBrand),
        apolloCallMapper =
        { data ->
            MultimoneyResult.Success(data.mapToDomainModel())
        }
    )
}
