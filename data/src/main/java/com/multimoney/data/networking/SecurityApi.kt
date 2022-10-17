package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.security.apollomodel.CatalogTypeIndentificationQuery
import com.multimoney.data.networking.security.apollomodel.DataInformationClientQuery
import com.multimoney.data.networking.security.apollomodel.GetCountryQuery
import com.multimoney.data.networking.security.apollomodel.OnfidoIntialProcessMutation
import com.multimoney.data.networking.security.apollomodel.SendPinProcessMutation
import com.multimoney.data.networking.security.apollomodel.UpdateUserRegisterMutation
import com.multimoney.data.networking.security.apollomodel.UserValidationMutation
import com.multimoney.data.networking.security.apollomodel.ValidateBankAccountQuery
import com.multimoney.data.networking.security.apollomodel.ValidatePinQuery
import com.multimoney.data.networking.security.apollomodel.ValidateUserExistsQuery
import com.multimoney.data.networking.security.apollomodel.ValidateUserStatusQuery
import com.multimoney.data.networking.security.apollomodel.ValidationSecurityQuery
import javax.inject.Inject

class SecurityApi @Inject constructor(
    private val apolloAuthorizedClient: ApolloClient
) {
    fun queryValidateUserExists(
        email: String
    ): ApolloCall<ValidateUserExistsQuery.Data> =
        apolloAuthorizedClient.query(ValidateUserExistsQuery(email)).fetchPolicy(
            FetchPolicy.NetworkOnly
        )

    fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int,
        idDocument: Int,
        identification: String,
        firstName: String,
        secondName: String,
        firstSurname: String,
        secondSurname: String
    ): ApolloCall<UserValidationMutation.Data> =
        apolloAuthorizedClient.mutation(
            UserValidationMutation(
                email,
                currentStep,
                idBrand,
                idDocument,
                identification,
                firstName,
                secondName,
                firstSurname,
                secondSurname
            )
        ).fetchPolicy(
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
        apolloAuthorizedClient.mutation(
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
        apolloAuthorizedClient.query(ValidationSecurityQuery(pkUser, password, user, idBrand)).fetchPolicy(
            FetchPolicy.NetworkOnly
        )

    fun queryValidateUserStatus(
        pkUser: Int,
        identification: String,
        email: String,
        idBrand: Int
    ): ApolloCall<ValidateUserStatusQuery.Data> =
        apolloAuthorizedClient.query(ValidateUserStatusQuery(pkUser, identification, email, idBrand))
            .fetchPolicy(
                FetchPolicy.NetworkOnly
            )

    fun queryDataInformationClient(
        identification: String,
        idBrand: Int,
        user: String
    ): ApolloCall<DataInformationClientQuery.Data> =
        apolloAuthorizedClient.query(
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
        apolloAuthorizedClient.mutation(
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
    ): ApolloCall<OnfidoIntialProcessMutation.Data> = apolloAuthorizedClient.mutation(
        OnfidoIntialProcessMutation(
            names,
            lastNames,
            identification,
            applicationId,
            idBrand,
            user
        )
    )

    fun queryValidationPin(
        idBrand: Int,
        appSource: Int,
        pkUser: String,
        ip: String,
        pinSecurity: String,
        telephone: String,
        sendValidatePin: String,
        flowOrigination: String,
        userCreate: String
    ): ApolloCall<ValidatePinQuery.Data> = apolloAuthorizedClient.query(
        ValidatePinQuery(
            idBrand,
            appSource,
            pkUser.toInt(),
            ip,
            pinSecurity,
            telephone,
            sendValidatePin,
            flowOrigination,
            userCreate
        )
    )

    fun queryCatalogIdentification(
        idBrand: Int,
        user: String
    ): ApolloCall<CatalogTypeIndentificationQuery.Data> =
        apolloAuthorizedClient.query(CatalogTypeIndentificationQuery(user, idBrand))

    fun queryGetCountry(user: String): ApolloCall<GetCountryQuery.Data> =
        apolloAuthorizedClient.query(GetCountryQuery(user))

    fun queryValidateAccount(
        account: String,
        identification: String,
        user: String,
        idBrand: Int
    ): ApolloCall<ValidateBankAccountQuery.Data> =
        apolloAuthorizedClient.query(ValidateBankAccountQuery(account, identification, user, idBrand))
}