package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.mapper.credit.mapToApolloModel
import com.multimoney.data.networking.credit.apollomodel.CompanyCantonQuery
import com.multimoney.data.networking.credit.apollomodel.CompanyDistrictQuery
import com.multimoney.data.networking.credit.apollomodel.CompanyProvinceQuery
import com.multimoney.data.networking.credit.apollomodel.CreditOfferQuery
import com.multimoney.data.networking.credit.apollomodel.GetClientBankAccountQuery
import com.multimoney.data.networking.credit.apollomodel.HomeCantonQuery
import com.multimoney.data.networking.credit.apollomodel.HomeDistrictQuery
import com.multimoney.data.networking.credit.apollomodel.HomeProvinceQuery
import com.multimoney.data.networking.credit.apollomodel.PaymentAmountQuery
import com.multimoney.data.networking.credit.apollomodel.SaveCreditApplicationMutation
import com.multimoney.data.networking.credit.apollomodel.SaveCreditFlowInputMutation
import com.multimoney.data.networking.credit.apollomodel.ScreenConfigQuery
import com.multimoney.data.networking.credit.apollomodel.TermsAndConditionsQuery
import com.multimoney.domain.model.credit.CreditInfoQuestion
import javax.inject.Inject

class CreditApi @Inject constructor(
    private val apolloClient: ApolloClient
) {
    fun queryCreditOffer(
        pkUser: Int,
        idBrand: Int
    ): ApolloCall<CreditOfferQuery.Data> =
        apolloClient.query(CreditOfferQuery(pkUser, idBrand)).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryPaymentAmount(
        amount: Int,
        months: String,
        idProduct: String,
        currencySymbol: String,
        user: String,
        idBrand: Int
    ): ApolloCall<PaymentAmountQuery.Data> =
        apolloClient.query(PaymentAmountQuery(amount, months, idProduct, currencySymbol, user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditApplication(
        idUserRequest: Int,
        pkUser: Int,
        descPromotion: String,
        interestRate: String,
        symbolCurrency: String,
        descCurrency: String,
        idProduct: Int,
        idPromotion: Int,
        months: String,
        commissionPercentage: String,
        paymentDate: String,
        paymentAmount: String,
        user: String,
        idBrand: Int,
        selectedAmount: Double,
        minimumAmount: Double,
        creditLimit: Double,
        tractAmount: Double,
        currentStep: String
    ): ApolloCall<SaveCreditApplicationMutation.Data> =
        apolloClient.mutation(
            SaveCreditApplicationMutation(
                idUserRequest,
                pkUser,
                descPromotion,
                interestRate,
                symbolCurrency,
                descCurrency,
                idProduct,
                idPromotion,
                months,
                commissionPercentage,
                paymentDate,
                paymentAmount,
                user,
                idBrand,
                selectedAmount,
                minimumAmount,
                creditLimit,
                tractAmount,
                currentStep
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryScreenConfig(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): ApolloCall<ScreenConfigQuery.Data> =
        apolloClient.query(
            ScreenConfigQuery(pkUser, user, idBrand, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): ApolloCall<HomeProvinceQuery.Data> =
        apolloClient.query(
            HomeProvinceQuery(pkUser, user, idBrand, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): ApolloCall<HomeCantonQuery.Data> =
        apolloClient.query(
            HomeCantonQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): ApolloCall<HomeDistrictQuery.Data> =
        apolloClient.query(
            HomeDistrictQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): ApolloCall<CompanyProvinceQuery.Data> =
        apolloClient.query(
            CompanyProvinceQuery(pkUser, user, idBrand, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): ApolloCall<CompanyCantonQuery.Data> =
        apolloClient.query(
            CompanyCantonQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ): ApolloCall<CompanyDistrictQuery.Data> =
        apolloClient.query(
            CompanyDistrictQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditFlowStep(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion?>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): ApolloCall<SaveCreditFlowInputMutation.Data> =
        apolloClient.mutation(
            SaveCreditFlowInputMutation(
                user,
                idBrand,
                infoQuestion.map { it?.mapToApolloModel() },
                idLogUserRequest,
                idUser,
                currentStep
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationTermsAndConditions(
        user: String,
        idBrand: Int,
        systemInDarkTheme: Boolean
    ): ApolloCall<TermsAndConditionsQuery.Data> =
        apolloClient.query(
            TermsAndConditionsQuery(
                user,
                idBrand,
                systemInDarkTheme
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetClientBankAccount(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoan: Int
    ): ApolloCall<GetClientBankAccountQuery.Data> =
        apolloClient.query(
            GetClientBankAccountQuery(
                user,
                idBrand,
                idClient,
                idLoan
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)
}
