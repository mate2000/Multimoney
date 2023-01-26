package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.PhonesResult
import com.multimoney.domain.model.accountsmart.RelatedContact
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryRelatedContactsByPhoneUseCaseImpl(private val smartAccountRepository: SmartAccountRepository) :
    QueryRelatedContactsByPhoneUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        contacts: List<RelatedContact>
    ): Flow<MultimoneyResult<PhonesResult?>> =
        smartAccountRepository.queryRelatedContactsByPhone(user, idBrand, contacts)
}
