package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.security.apollomodel.UpdateUserRegisterMutation
import com.multimoney.data.networking.security.apollomodel.UserValidationMutation
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

    fun mutationUpdateUserRegister(
        pkUser: String,
        user: String,
        email: String,
        phoneNumber: String?,
        fullName: String?,
        firstName: String?,
        secondName: String?,
        lastName: String?,
        secondLastName: String?,
        contactMeans: String?,
        nationality: String?,
        identification: String?,
        countryCode: String?,
        currentStep: String,
        idBrand: Int
    ): ApolloCall<UpdateUserRegisterMutation.Data> =
        apolloClient.mutation(
            UpdateUserRegisterMutation(
                pkUser,
                user,
                email,
                phoneNumber,
                fullName,
                firstName,
                secondName,
                lastName,
                secondLastName,
                contactMeans,
                nationality,
                identification,
                countryCode,
                currentStep,
                idBrand
            )
        ).fetchPolicy(
            FetchPolicy.NetworkOnly
        )
}