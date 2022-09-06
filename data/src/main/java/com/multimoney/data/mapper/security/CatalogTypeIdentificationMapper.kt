package com.multimoney.data.mapper.security

import com.multimoney.data.networking.security.apollomodel.CatalogTypeIndentificationQuery
import com.multimoney.domain.model.security.CatalogDocument
import com.multimoney.domain.model.security.CatalogType

private fun CatalogTypeIndentificationQuery.CatalogTypeIndentification.mapToDomainModel() =
    CatalogDocument(
        description = descripcion ?: "",
        format = formato ?: "",
        idDocument = pk_Suv_Cat_Catalogo.toString().toInt()
    )

fun CatalogTypeIndentificationQuery.Data.mapToDomainModel(): CatalogType {
    val domainCatalogType = arrayListOf<CatalogDocument>()
    this.catalogTypeIndentification?.forEach { documentType ->
        documentType?.let {
            domainCatalogType.add(it.mapToDomainModel())
        }
    }
    return CatalogType(domainCatalogType)
}