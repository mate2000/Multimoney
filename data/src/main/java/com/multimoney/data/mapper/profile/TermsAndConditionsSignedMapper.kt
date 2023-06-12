package com.multimoney.data.mapper.profile

import com.multimoney.data.networking.graphql.apollomodel.TermsAndConditionsSignedQuery
import com.multimoney.domain.model.profile.TermsAndConditionsSigned
import com.multimoney.domain.model.profile.TermsAndConditionsSignedItem

fun TermsAndConditionsSignedQuery.Data.mapToDomainModel() = TermsAndConditionsSigned(
    items = termsConditionsSigned.map {
        TermsAndConditionsSignedItem(
            type = it.tipo,
            dateSigned = it.fechaFirma.toString(),
            version = it.version,
            html = it.html
        )
    }
)