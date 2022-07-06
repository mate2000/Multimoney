package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.security.apollomodel.DataInformationClientQuery
import com.multimoney.data.networking.security.apollomodel.OnfidoIntialProcessMutation
import com.multimoney.data.networking.security.apollomodel.SendPinProcessMutation
import com.multimoney.data.networking.security.apollomodel.UpdateUserRegisterMutation
import com.multimoney.data.networking.security.apollomodel.UserValidationMutation
import com.multimoney.data.networking.security.apollomodel.ValidationSecurityQuery
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
                nationality,
                identification,
                countryCode,
                currentStep,
                idBrand
            )
        ).fetchPolicy(
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

    fun queryDataInformationClient(
        identification: String,
        idBrand: Int,
        user: String
    ): ApolloCall<DataInformationClientQuery.Data> =
        apolloClient.query(
            DataInformationClientQuery(
                identification, idBrand, user
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSendPinProcess(
        identification: String,
        firstName: String,
        email: String,
        cellPhone: String,
        sendMethod: String,
        pkUser: Int,
        idBrand: Int,
        user: String
    ): ApolloCall<SendPinProcessMutation.Data> =
        apolloClient.mutation(
            SendPinProcessMutation(
                identification,
                firstName,
                email,
                cellPhone,
                sendMethod,
                pkUser,
                idBrand,
                user
            )
        )

    fun mutationOnFidoInitialProcess(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String
    ): ApolloCall<OnfidoIntialProcessMutation.Data> = apolloClient.mutation(
        OnfidoIntialProcessMutation(
            names,
            lastNames,
            identification,
            applicationId,
            idBrand,
            user
        )
    )
}