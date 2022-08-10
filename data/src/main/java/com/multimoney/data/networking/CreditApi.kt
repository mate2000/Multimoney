package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.credit.apollomodel.CompanyCantonQuery
import com.multimoney.data.networking.credit.apollomodel.CompanyDistrictQuery
import com.multimoney.data.networking.credit.apollomodel.CompanyProvinceQuery
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

    fun queryCompanyProvince(pkUser: String, user: String, idBrand: Int): ApolloCall<CompanyProvinceQuery.Data> =
        apolloClient.query(
            CompanyProvinceQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyCanton(pkUser: String, user: String, idBrand: Int): ApolloCall<CompanyCantonQuery.Data> =
        apolloClient.query(
            CompanyCantonQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyDistrict(pkUser: String, user: String, idBrand: Int): ApolloCall<CompanyDistrictQuery.Data> =
        apolloClient.query(
            CompanyDistrictQuery(pkUser.toInt(), user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)
}