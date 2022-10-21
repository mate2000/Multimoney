package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.credit.apollomodel.CreditOfferQuery
import javax.inject.Inject

class SmartAccountApi @Inject constructor(
    private val apolloClient: ApolloClient,
) {
    fun queryCivilStatus(
        pkUser: Int,
        idBrand: Int,
    ): ApolloCall<CivilStatusQuery.Data> =
        apolloClient.query(CreditOfferQuery(pkUser, idBrand)).fetchPolicy(FetchPolicy.NetworkOnly)
}