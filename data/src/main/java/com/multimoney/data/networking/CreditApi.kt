package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.mapper.credit.mapToApolloModel
import com.multimoney.data.networking.credit.apollomodel.CompanyAddressQuery
import com.multimoney.data.networking.credit.apollomodel.CompanyAddressSVQuery
import com.multimoney.data.networking.credit.apollomodel.CreditOfferQuery
import com.multimoney.data.networking.credit.apollomodel.HomeAddressQuery
import com.multimoney.data.networking.credit.apollomodel.HomeAddressSVQuery
import com.multimoney.data.networking.credit.apollomodel.PaymentAmountQuery
import com.multimoney.data.networking.credit.apollomodel.SaveCreditApplicationMutation
import com.multimoney.data.networking.credit.apollomodel.SaveCreditFlowInputMutation
import com.multimoney.data.networking.credit.apollomodel.ScreenConfigQuery
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

    fun queryCompanyAddress(pkUser: String, user: String, idBrand: Int): ApolloCall<CompanyAddressQuery.Data> =
        apolloClient.query(
            CompanyAddressQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyAddressSV(pkUser: String, user: String, idBrand: Int): ApolloCall<CompanyAddressSVQuery.Data> =
        apolloClient.query(
            CompanyAddressSVQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeAddress(pkUser: String, user: String, idBrand: Int): ApolloCall<HomeAddressQuery.Data> =
        apolloClient.query(
            HomeAddressQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeAddressSV(pkUser: String, user: String, idBrand: Int): ApolloCall<HomeAddressSVQuery.Data> =
        apolloClient.query(
            HomeAddressSVQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditFlowStep(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion?>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String,
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
}