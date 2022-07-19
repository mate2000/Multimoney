package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.SendPinProcess
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSendPinProcessUseCase {
    suspend operator fun invoke(
        identification: String,
        firstName: String,
        email: String,
        cellPhone: String,
        sendMethod: String,
        pkUser: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SendPinProcess?>>
}