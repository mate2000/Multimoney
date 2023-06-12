package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidatePasswordStructure
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryValidatePasswordStructure {

    suspend operator fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidatePasswordStructure>>
}