package com.multimoney.domain.interaction.profile

import com.multimoney.domain.model.profile.CountryContact
import com.multimoney.domain.model.profile.TermsAndConditionsSigned
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryTermsAndConditionsSignedUseCase {
    suspend operator fun invoke(
        styleDark : Boolean,
        pkUser : Int,
        identification : String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<TermsAndConditionsSigned?>>
}