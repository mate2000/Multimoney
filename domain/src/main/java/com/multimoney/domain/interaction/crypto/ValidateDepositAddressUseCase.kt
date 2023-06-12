package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.ValidateDepositAddressResponse
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface ValidateDepositAddressUseCase {

    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        address: String
    ): Flow<MultimoneyResult<ValidateDepositAddressResponse>>
}
