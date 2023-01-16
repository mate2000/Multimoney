package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.PhonesResult
import com.multimoney.domain.model.accountsmart.RelatedContact
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryRelatedContactsByPhoneUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        contacts: List<RelatedContact>?
    ): Flow<MultimoneyResult<PhonesResult?>>
}