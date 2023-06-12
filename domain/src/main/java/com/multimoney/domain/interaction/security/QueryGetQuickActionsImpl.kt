package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.QuickActions
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryGetQuickActionsImpl(val securityRepository: SecurityRepository) :
    QueryGetQuickActionsUseCase {
    override suspend fun invoke(
        idBrand : Int,
        pkUser: Int,
        identification : String,
        infoCreditStatus: Int,
        infoVirtualCardStatus: Int,
        infoBankAccountStatus: Int,
        infoCriptoStatus: Int
    ): Flow<MultimoneyResult<QuickActions?>> =
        securityRepository.queryGetQuickActions(idBrand, pkUser, identification, infoCreditStatus, infoVirtualCardStatus, infoBankAccountStatus, infoCriptoStatus)
}

