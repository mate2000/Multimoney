package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.CatalogType
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCatalogDocumentTypeUseCase {
    suspend operator fun invoke(idBrand: Int, user: String): Flow<MultimoneyResult<CatalogType?>>
}