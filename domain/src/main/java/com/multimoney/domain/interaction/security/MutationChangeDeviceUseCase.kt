package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ChangeDevice
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow


interface MutationChangeDeviceUseCase {
    suspend operator fun invoke(
        email: String,
        otp: String
    ): Flow<MultimoneyResult<ChangeDevice>>
}
