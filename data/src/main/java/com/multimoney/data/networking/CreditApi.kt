package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.credit.apollomodel.CompanyCantonQuery
import com.multimoney.data.networking.credit.apollomodel.CompanyDistrictQuery
import com.multimoney.data.networking.credit.apollomodel.CompanyProvinceQuery
import com.multimoney.data.networking.credit.apollomodel.CreditOfferQuery
import com.multimoney.data.networking.credit.apollomodel.HomeCantonQuery
import com.multimoney.data.networking.credit.apollomodel.HomeDistrictQuery
import com.multimoney.data.networking.credit.apollomodel.HomeProvinceQuery
import com.multimoney.data.networking.credit.apollomodel.PaymentAmountQuery
import com.multimoney.data.networking.credit.apollomodel.SaveCreditApplicationMutation
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

    fun queryHomeProvince(pkUser: Int, user: String, idBrand: Int): ApolloCall<HomeProvinceQuery.Data> =
        apolloClient.query(
            HomeProvinceQuery(pkUser, user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String
    ): ApolloCall<HomeCantonQuery.Data> =
        apolloClient.query(
            HomeCantonQuery(pkUser, user, idBrand, fkCatalogIdentifier)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String
    ): ApolloCall<HomeDistrictQuery.Data> =
        apolloClient.query(
            HomeDistrictQuery(pkUser, user, idBrand, fkCatalogIdentifier)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyProvince(pkUser: Int, user: String, idBrand: Int): ApolloCall<CompanyProvinceQuery.Data> =
        apolloClient.query(
            CompanyProvinceQuery(pkUser, user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String
    ): ApolloCall<CompanyCantonQuery.Data> =
        apolloClient.query(
            CompanyCantonQuery(pkUser, user, idBrand, fkCatalogIdentifier)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String
    ): ApolloCall<CompanyDistrictQuery.Data> =
        apolloClient.query(
            CompanyDistrictQuery(pkUser, user, idBrand, fkCatalogIdentifier)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

}