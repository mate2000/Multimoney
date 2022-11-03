package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.accountsmart.apollomodel.AddressLevel2Query
import com.multimoney.data.networking.accountsmart.apollomodel.CivilStatusQuery
import com.multimoney.data.networking.accountsmart.apollomodel.GeneralEconomicActivityQuery
import com.multimoney.data.networking.accountsmart.apollomodel.GlobalRequestMutation
import com.multimoney.data.networking.accountsmart.apollomodel.NationalityQuery
import com.multimoney.data.networking.accountsmart.apollomodel.ProfessionQuery
import com.multimoney.data.networking.accountsmart.apollomodel.StepByStepQuery
import javax.inject.Inject

class SmartAccountApi @Inject constructor(
    private val apolloClient: ApolloClient,
) {
    fun queryCivilStatus(
        pkUser: String,
        idBrand: Int,
    ): ApolloCall<CivilStatusQuery.Data> =
        apolloClient.query(CivilStatusQuery(pkUser, idBrand)).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryProfession(
        pkUser: String,
        idBrand: Int,
    ): ApolloCall<ProfessionQuery.Data> =
        apolloClient.query(ProfessionQuery(pkUser, idBrand)).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryAddressLevelTwo(
        user: String,
        idBrand: Int,
        idAddressLevel1: String,
    ): ApolloCall<AddressLevel2Query.Data> =
        apolloClient.query(AddressLevel2Query(user, idBrand, idAddressLevel1))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryNationality(
        user: String,
        idBrand: Int,
    ): ApolloCall<NationalityQuery.Data> =
        apolloClient.query(NationalityQuery(user, idBrand)).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryStepByStep(
        user: String,
        idBrand: Int,
        idRequest: Int,
    ): ApolloCall<StepByStepQuery.Data> =
        apolloClient.query(StepByStepQuery(user, idBrand, idRequest))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationGlobalRequest(
        pkUser: Int,
        status: Int,
        idProfessionType: Int,
        idCivilStatusType: Long,
        birthday: String,
        expirationDate: String,
        idGender: Long,
        companyName: String,
        aboutCompany: String,
        idAddressLevel1: Long,
        idAddressLevel2: Long,
        idAddressLevel3: Long,
        idEconomicActivity: Long,
        income: Int,
        addressDetail: String,
        user: String,
        idBrand: Int,
        currentStep: String,
        institutionPension: String,
        specifiesIncomeSource: String,
        isActivityOfArt15: Boolean,
        isUSCitizen: Boolean,
        isPEP: Boolean,
        isUSTaxPayer: Boolean,
        isTaxPayer: Boolean
    ): ApolloCall<GlobalRequestMutation.Data> =
        apolloClient.mutation(
            GlobalRequestMutation(
                pkUser = pkUser,
                status = status,
                idProfessionType = idProfessionType,
                idAddressLevel1 = idAddressLevel1,
                idAddressLevel2 = idAddressLevel2,
                birthdate = birthday,
                expirationDate = expirationDate,
                idGenre = idGender,
                idMaritalStatus = idCivilStatusType,
                nameCompany = companyName,
                aboutCompany = aboutCompany,
                institutionPension = institutionPension,
                idAddressLevel3 = idAddressLevel3,
                idEconomicActivity = idEconomicActivity,
                income = income,
                addressDetail = addressDetail,
                user = user,
                idBrand = idBrand,
                currentStep = currentStep,
                specifiesIncomeSource = specifiesIncomeSource,
                isActivityOfArt15 = isActivityOfArt15,
                isUSCitizen = isUSCitizen,
                isPEP = isPEP,
                isUSTaxPayer = isUSTaxPayer,
                isTaxPayer = isTaxPayer
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGeneralEconomicActivity(
        user: String,
        idBrand: Int,
    ): ApolloCall<GeneralEconomicActivityQuery.Data> =
        apolloClient.query(GeneralEconomicActivityQuery(user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)
}
