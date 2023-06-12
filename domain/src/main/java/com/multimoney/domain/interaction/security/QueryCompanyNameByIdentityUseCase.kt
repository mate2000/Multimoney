package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.Company
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCompanyNameByIdentityUseCase {
    suspend operator fun invoke(
        identification: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<Company?>>
}
