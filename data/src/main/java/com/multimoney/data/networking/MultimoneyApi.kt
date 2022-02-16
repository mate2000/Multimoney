package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.multimoney.data.networking.MultimoneyApiClient.apolloClient
import com.multimoney.data.networking.apollomodel.LaunchListQuery

object MultimoneyApi {
    fun getLaunchList(): ApolloCall<LaunchListQuery.Data> = apolloClient().query(LaunchListQuery())
}