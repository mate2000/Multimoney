package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.GetParametersMobileByCategory
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryGetParametersMobileByCategoryUseCaseImpl @Inject constructor(private val virtualCardRepository: VirtualCardRepository) :
    QueryGetParametersMobileByCategoryUseCase {
    override suspend fun invoke(
        idBrand: Int,
        category: String
    ): Flow<MultimoneyResult<List<GetParametersMobileByCategory?>?>> =
        virtualCardRepository.queryGetParametersMobileByCategory(
            idBrand = idBrand,
            category = category
        )
}