package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.MiniCards
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryMiniCardsUseCaseImpl(
    val securityRepository: SecurityRepository
) : QueryMiniCardsUseCase {
    override suspend fun invoke(
        infoCreditStatus: Boolean,
        infoVirtualCardStatus: Boolean,
        infoBankAccountStatus: Boolean,
        infoCrypto: Boolean,
        userEmail: String,
        idBrand: Int
    ): Flow<MultimoneyResult<MiniCards>> = securityRepository.queryHomeMiniCards(
        infoCreditStatus,
        infoVirtualCardStatus,
        infoBankAccountStatus,
        infoCrypto,
        userEmail,
        idBrand
    )
}