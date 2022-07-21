package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.networking.CreditApi
import com.multimoney.domain.repository.CreditRepository
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(
    private val creditApi: CreditApi
) : BaseRepository(),
    CreditRepository {
}
