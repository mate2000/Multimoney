package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.ListMiniCardsQuery
import com.multimoney.domain.model.security.MiniCards
import com.multimoney.domain.model.security.MiniCardsItem

fun ListMiniCardsQuery.Data.mapToDomainModel() = MiniCards(
    miniCardsList = minicards.map {
        MiniCardsItem(
            priority = it.prioridad,
            type = it.tipo,
            //todo uncomment this
            imageUrl = /*it.imagenUrl ?: ""*/ "https://images.prismic.io/mmsv/89928082-6f31-4943-8d38-8e619aaca600_Nuevo+blog+mm+smart-01.png?auto=compress,format",
            deepLink = it.deeplink  ?: ""
        )
    }
)