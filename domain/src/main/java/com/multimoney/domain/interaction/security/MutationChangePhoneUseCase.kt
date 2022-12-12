package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ChangePhone
import com.multimoney.domain.model.security.OnfidoCheckProcess
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationChangePhoneUseCase {
    suspend operator fun invoke(
        identification: String,
        phone : String,
        pkUser : String,
        idBrand : Int

    ): Flow<MultimoneyResult<ChangePhone>>
}