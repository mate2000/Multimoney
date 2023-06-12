package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class TermsAndConditionsUseCaseImpl(val creditRepository: CreditRepository) : TermsAndConditionsUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        systemInDarkTheme: Boolean
    ): Flow<MultimoneyResult<String>> =
        creditRepository.mutationTermsAndConditions(user, idBrand, systemInDarkTheme)
}