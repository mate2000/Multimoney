package com.multimoney.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.crypto.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.data.paging.CryptoMovementsPagingSource
import com.multimoney.domain.model.crypto.BuyCryptoCurrencyData
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.CryptoCurrencyNews
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.crypto.GetHistoricalCurrencyPrices
import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.crypto.PricesQuoteAndCommissionData
import com.multimoney.domain.model.crypto.ValidateDepositAddressResponse
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.MultimoneyResult.Success
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CryptoRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(), CryptoRepository {

    override suspend fun getHistoricalClientBalance(
        user: String,
        idBrand: Int,
        identification: String,
        baseAsset: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>> = fetchData(
        apolloCall = graphqlApi.queryGetHistoricalClientBalance(
            user,
            idBrand,
            identification,
            baseAsset,
            startDate,
            endDate
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun getAvailableListOfCryptoCoins(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GetListOfAvailableCryptoCoins?>> = fetchData(
        apolloCall = graphqlApi.queryGetAvailableListOfCryptoCoins(user, idBrand),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun getCryptoCurrencyMovements(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        order_time_begin: Any,
        order_time_end: Any,
        pagination_limit: Int
    ): Flow<PagingData<CryptoCurrencyMovement>> {
        return Pager(
            config = PagingConfig(pagination_limit),
            pagingSourceFactory = {
                CryptoMovementsPagingSource(
                    graphqlApi,
                    user,
                    idBrand,
                    identification,
                    market,
                    order_time_begin,
                    order_time_end,
                    pagination_limit
                )
            }
        ).flow
    }

    override suspend fun getCurrencyHistoricalPrices(
        market: String,
        max_data_points: Long,
        range_begin: String,
        range_end: String,
        pagination_limit: Int,
        pagination_offset: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GetHistoricalCurrencyPrices>> = fetchData(
        apolloCall = graphqlApi.queryCurrencyHistoricalPrices(
            market,
            max_data_points,
            range_begin,
            range_end,
            pagination_limit,
            pagination_offset,
            user,
            idBrand
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun getCurrencyNews(
        baseAsset: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CryptoCurrencyNews>> = fetchData(
        apolloCall = graphqlApi.queryCurrencyNews(baseAsset, user, idBrand),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun getPriceQuoteAndCommission(
        asset: String,
        crypto_network: String,
        idBrand: Int,
        user: String,
        market: String,
        identification: String,
        quote_amount: Double,
        base_amount: Double,
        side: String
    ): Flow<MultimoneyResult<PricesQuoteAndCommissionData>> = fetchData(
        apolloCall = graphqlApi.queryGetPriceQuoteAndCommission(
            asset,
            crypto_network,
            idBrand,
            user,
            market,
            identification,
            quote_amount,
            base_amount,
            side
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun buyCryptoCurrency(
        pkUser: Int,
        identification: String,
        market: String,
        commissionAmount: Double,
        taxAmount: Double,
        accountToken: Double,
        exchangeRate: Double,
        idBrand: Int,
        user: String,
        quoteId: String,
        quoteAmount: Double,
        fee: Double,
        internalFee: Double,
        totalFee: Double
    ): Flow<MultimoneyResult<BuyCryptoCurrencyData>> = fetchData(
        apolloCall = graphqlApi.mutationBuyCryptoCurrency(
            pkUser,
            identification,
            market,
            commissionAmount,
            taxAmount,
            accountToken,
            exchangeRate,
            idBrand,
            user,
            quoteId,
            quoteAmount,
            fee,
            internalFee,
            totalFee
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun getBalanceCryptoAccount(
        user: String,
        idBrand: Int,
        identification: String,
    ): Flow<MultimoneyResult<BalanceCryptoAccount>> = fetchData(
        apolloCall = graphqlApi.queryGetBalanceCryptoAccount(
            user,
            idBrand,
            identification,
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )

    override suspend fun validateDepositAddress(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        address: String
    ): Flow<MultimoneyResult<ValidateDepositAddressResponse>> = fetchData(
        apolloCall = graphqlApi.mutationValidateDepositAddress(
            user,
            idBrand,
            identification,
            market,
            address
        ),
        apolloCallMapper = { data -> Success(data.mapToDomainModel()) }
    )
}
