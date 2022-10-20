package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.networking.SmartAccountApi
import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SmartAccountRepositoryImpl @Inject constructor(
    private val smartApi: SmartAccountApi,
) : BaseRepository()
    /*SmartAccountRepository {
    override suspend fun queryCivilStatus(
        pkUser: Int,
        idBrand: Int,
    ): Flow<MultimoneyResult<CreditOffer?>> {
        smartApi.queryCivilStatus(pkUser, idBrand)
    }
}*/