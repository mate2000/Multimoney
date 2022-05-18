package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.security.apollomodel.UserValidationMutation
import com.multimoney.data.networking.security.apollomodel.ValidationSecurityQuery
import com.multimoney.domain.model.security.ValidateSecurity
import javax.inject.Inject

class SecurityApi @Inject constructor(
    private val apolloClient: ApolloClient
) {
    fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int
    ): ApolloCall<UserValidationMutation.Data> =
        apolloClient.mutation(UserValidationMutation(email, currentStep, idBrand)).fetchPolicy(
            FetchPolicy.NetworkOnly
        )

    fun queryValidationSecurity(
        pkUser: Int,
        password: String,
        user: String,
        idBrand: Int
    ): ApolloCall<ValidationSecurityQuery.Data> =
        apolloClient.query(ValidationSecurityQuery(pkUser, password, user, idBrand)).fetchPolicy(
            FetchPolicy.NetworkOnly
        )
}