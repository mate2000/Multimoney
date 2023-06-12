package com.multimoney.domain.interaction.profile

import com.multimoney.domain.model.profile.TermsAndConditionsSigned
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class QueryTermsAndConditionsSignedUseCaseImpl(val repository: ProfileRepository) :
    QueryTermsAndConditionsSignedUseCase {
    override suspend fun invoke(
        styleDark: Boolean,
        pkUser: Int,
        identification: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<TermsAndConditionsSigned?>> = repository.queryTermsAndConditionsSigned(
        styleDark = styleDark,
        pkUser = pkUser,
        identification = identification,
        user = user,
        idBrand = idBrand
    )
}