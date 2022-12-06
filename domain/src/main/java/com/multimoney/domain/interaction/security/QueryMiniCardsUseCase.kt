package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.MiniCards
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryMiniCardsUseCase {
    suspend operator fun invoke(
        infoCreditStatus: Boolean,
        infoVirtualCardStatus: Boolean,
        infoBankAccountStatus: Boolean,
        infoCrypto: Boolean,
        userEmail: String,
        idBrand: Int
    ) : Flow<MultimoneyResult<MiniCards>>
}