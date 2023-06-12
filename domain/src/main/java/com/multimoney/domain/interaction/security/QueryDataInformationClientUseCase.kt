package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryDataInformationClientUseCase {
    suspend operator fun invoke(
        identification: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<ClientInfoCr?>>
}