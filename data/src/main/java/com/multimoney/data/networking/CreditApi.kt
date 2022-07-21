package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloClient
import javax.inject.Inject

class CreditApi @Inject constructor(
    private val apolloClient: ApolloClient
) {
}