package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.credit.apollomodel.CompanyAddressQuery
import com.multimoney.data.networking.credit.apollomodel.CompanyAddressSVQuery
import com.multimoney.data.networking.credit.apollomodel.CreditOfferQuery
import com.multimoney.data.networking.credit.apollomodel.PaymentAmountQuery
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

    fun queryCompanyAddress(pkUser: String, user: String, idBrand: Int): ApolloCall<CompanyAddressQuery.Data> =
        apolloClient.query(
            CompanyAddressQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyAddressSV(pkUser: String, user: String, idBrand: Int): ApolloCall<CompanyAddressSVQuery.Data> =
        apolloClient.query(
            CompanyAddressSVQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)
}