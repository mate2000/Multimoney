package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.networking.accountsmart.apollomodel.AddressLevel2Query
import com.multimoney.data.networking.accountsmart.apollomodel.CivilStatusQuery
import com.multimoney.data.networking.accountsmart.apollomodel.GeneralEconomicActivityQuery
import com.multimoney.data.networking.accountsmart.apollomodel.GlobalRequestMutation
import com.multimoney.data.networking.accountsmart.apollomodel.NationalityQuery
import com.multimoney.data.networking.accountsmart.apollomodel.ProfessionQuery
import com.multimoney.data.networking.accountsmart.apollomodel.RelationshipQuery
import com.multimoney.data.networking.accountsmart.apollomodel.StepByStepQuery
import com.multimoney.data.networking.accountsmart.apollomodel.type.BeneficiarieList
import com.multimoney.domain.model.accountsmart.Beneficiary
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
        isPEP: Boolean,
        user: String,
        idBrand: Int,
        currentStep: String,
        institutionPension: String,
        specifiesIncomeSource: String,
        beneficiaries: List<Beneficiary>,
    ): ApolloCall<GlobalRequestMutation.Data> =
        apolloClient.mutation(
            GlobalRequestMutation(
                pkUser,
                status,
                idProfessionType,
                idAddressLevel1,
                idAddressLevel2,
                birthday,
                expirationDate,
                idGender,
                idCivilStatusType,
                companyName,
                aboutCompany,
                institutionPension,
                idAddressLevel3,
                idEconomicActivity,
                income,
                addressDetail,
                isPEP,
                user,
                idBrand,
                currentStep,
                specifiesIncomeSource,
                beneficiaries.map { beneficiary ->
                    BeneficiarieList(Optional.presentIfNotNull(beneficiary.fullName),
                        Optional.presentIfNotNull(beneficiary.relationship.toString()),
                        Optional.presentIfNotNull(beneficiary.allocationPercentage))
                }
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGeneralEconomicActivity(
        user: String,
        idBrand: Int,
    ): ApolloCall<GeneralEconomicActivityQuery.Data> =
        apolloClient.query(GeneralEconomicActivityQuery(user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryRelationship(
        user: String,
        idBrand: Int,
        option: Int,
    ): ApolloCall<RelationshipQuery.Data> =
        apolloClient.query(RelationshipQuery(user, idBrand, option))
            .fetchPolicy(FetchPolicy.NetworkOnly)
}
