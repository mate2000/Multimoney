package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.QuickActionsQuery
import com.multimoney.domain.model.security.QuickAction
import com.multimoney.domain.model.security.QuickActions

fun QuickActionsQuery.Data.mapToDomainModel() = QuickActions(quickActions = quickActions.map { it.mapToDomainModel() })

fun QuickActionsQuery.QuickAction.mapToDomainModel() = QuickAction (
    iconId = idIcono,
    name = nombre,
    flow = flujo,
    productType = tipoProducto
)