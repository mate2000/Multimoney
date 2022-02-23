package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.apollomodel.LaunchListQuery
import javax.inject.Inject

class MultimoneyApi @Inject constructor(
    private val apolloClient: ApolloClient
) {
    fun getLaunchList(): ApolloCall<LaunchListQuery.Data> =
        apolloClient.query(LaunchListQuery()).fetchPolicy(
            FetchPolicy.NetworkFirst
        )
}