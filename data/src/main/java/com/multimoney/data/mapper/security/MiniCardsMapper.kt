package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ListMiniCardsQuery
import com.multimoney.domain.model.security.MiniCards
import com.multimoney.domain.model.security.MiniCardsItem

fun ListMiniCardsQuery.Data.mapToDomainModel() = MiniCards(
    miniCardsList = minicards.map {
        MiniCardsItem(
            priority = it.prioridad,
            type = it.tipo,
            imageUrl = it.imagenUrl ?: "",
            deepLink = it.deeplink  ?: ""
        )
    }
)