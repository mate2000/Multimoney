package com.multimoney.data.mapper.smartaccount

import com.multimoney.data.networking.graphql.apollomodel.RelationshipQuery
import com.multimoney.domain.model.accountsmart.Relationship
import com.multimoney.domain.model.accountsmart.RelationshipData

private fun RelationshipQuery.Relationhip.mapToDomain() =
    Relationship(relationshipId, code, description)

fun RelationshipQuery.Data.mapToDomain() =
    RelationshipData(data = relationhip?.map { it.mapToDomain() } ?: listOf())