package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.QuickActions
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetQuickActionsUseCase {
    suspend operator fun invoke(
        idBrand : Int,
        pkUser : Int,
        identification : String,
        infoCreditStatus : Int,
        infoVirtualCardStatus : Int,
        infoBankAccountStatus : Int,
        infoCriptoStatus : Int
    ): Flow<MultimoneyResult<QuickActions?>>
}