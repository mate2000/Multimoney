package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.CatalogType
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryCatalogDocumentTypeUseCaseImpl(private val serviceRepository: SecurityRepository) :
    QueryCatalogDocumentTypeUseCase {
    override suspend fun invoke(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<CatalogType?>> = serviceRepository.queryCatalog(idBrand, user)
}