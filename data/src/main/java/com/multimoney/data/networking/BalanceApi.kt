package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.balances.apollomodel.BalanceQuery
import javax.inject.Inject

class BalanceApi @Inject constructor(
    private val apolloClient: ApolloClient
) {

    fun queryBalance(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: String,
        idLoanClient: Int
    ): ApolloCall<BalanceQuery.Data> =
        apolloClient.query(
            BalanceQuery(
                user, identification, idBrand, idClient, idLoanClient
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)
}