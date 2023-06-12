package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.Token
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetTokenUseCase {
    suspend operator fun invoke(): Flow<MultimoneyResult<Token>>
}
