package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.mapper.credit.mapToApolloModel
import com.multimoney.data.networking.graphql.apollomodel.ActivatedClientAutomaticDebitMutation
import com.multimoney.data.networking.graphql.apollomodel.AddressLevel1Query
import com.multimoney.data.networking.graphql.apollomodel.AddressLevel2Query
import com.multimoney.data.networking.graphql.apollomodel.AddressLevel3Query
import com.multimoney.data.networking.graphql.apollomodel.BalanceQuery
import com.multimoney.data.networking.graphql.apollomodel.BanksAndRegularExpressionQuery
import com.multimoney.data.networking.graphql.apollomodel.CatalogTypeIndentificationQuery
import com.multimoney.data.networking.graphql.apollomodel.CivilStatusQuery
import com.multimoney.data.networking.graphql.apollomodel.CompanyCantonQuery
import com.multimoney.data.networking.graphql.apollomodel.CompanyDistrictQuery
import com.multimoney.data.networking.graphql.apollomodel.CompanyProvinceQuery
import com.multimoney.data.networking.graphql.apollomodel.CreditContractEventSubscription
import com.multimoney.data.networking.graphql.apollomodel.CreditOfferQuery
import com.multimoney.data.networking.graphql.apollomodel.DataInformationClientQuery
import com.multimoney.data.networking.graphql.apollomodel.GeneralEconomicActivityQuery
import com.multimoney.data.networking.graphql.apollomodel.GetClientAutomaticDebitQuery
import com.multimoney.data.networking.graphql.apollomodel.GetClientBankAccountQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCompanyNameByIdentificationQuery
import com.multimoney.data.networking.graphql.apollomodel.GetConfigurationVersionQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCountryQuery
import com.multimoney.data.networking.graphql.apollomodel.GetExchangeRateCreditQuery
import com.multimoney.data.networking.graphql.apollomodel.GetPaymentPointsQuery
import com.multimoney.data.networking.graphql.apollomodel.GlobalRequestMutation
import com.multimoney.data.networking.graphql.apollomodel.HomeCantonQuery
import com.multimoney.data.networking.graphql.apollomodel.HomeDistrictQuery
import com.multimoney.data.networking.graphql.apollomodel.HomeProvinceQuery
import com.multimoney.data.networking.graphql.apollomodel.ListCardVDQuery
import com.multimoney.data.networking.graphql.apollomodel.NationalityQuery
import com.multimoney.data.networking.graphql.apollomodel.OcupationsQuery
import com.multimoney.data.networking.graphql.apollomodel.OnfidoCheckProcessMutation
import com.multimoney.data.networking.graphql.apollomodel.OnfidoIntialProcessMutation
import com.multimoney.data.networking.graphql.apollomodel.PaymentAmountQuery
import com.multimoney.data.networking.graphql.apollomodel.ProccessPaymentListMutation
import com.multimoney.data.networking.graphql.apollomodel.ProfessionSmartQuery
import com.multimoney.data.networking.graphql.apollomodel.ProfessionsQuery
import com.multimoney.data.networking.graphql.apollomodel.RelationshipQuery
import com.multimoney.data.networking.graphql.apollomodel.SaveCreditApplicationMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveCreditFlowInputMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveCreditOperationMutation
import com.multimoney.data.networking.graphql.apollomodel.ScreenConfigQuery
import com.multimoney.data.networking.graphql.apollomodel.SendCreditContractEventMutation
import com.multimoney.data.networking.graphql.apollomodel.SendPinProcessMutation
import com.multimoney.data.networking.graphql.apollomodel.StepByStepQuery
import com.multimoney.data.networking.graphql.apollomodel.TermsAndConditionsQuery
import com.multimoney.data.networking.graphql.apollomodel.UpdateUserRegisterMutation
import com.multimoney.data.networking.graphql.apollomodel.UserValidationMutation
import com.multimoney.data.networking.graphql.apollomodel.ValidateBankAccountQuery
import com.multimoney.data.networking.graphql.apollomodel.ValidatePinQuery
import com.multimoney.data.networking.graphql.apollomodel.ValidateUserExistsQuery
import com.multimoney.data.networking.graphql.apollomodel.ValidateUserStatusQuery
import com.multimoney.data.networking.graphql.apollomodel.ValidationSecurityQuery
import com.multimoney.data.networking.graphql.apollomodel.type.BeneficiaryRequestDtoInput
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.DestinyAccount
import javax.inject.Inject

class GraphqlApi @Inject constructor(
    private val apolloAuthorizedClient: ApolloClient
) {

    // Balance
    fun queryBalance(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        creditStatus: Int,
        accountStatus: Int,
        cryptoStatus: Int,
        cardStatus: Int
    ): ApolloCall<BalanceQuery.Data> =
        apolloAuthorizedClient.query(
            BalanceQuery(
                user,
                identification,
                idBrand,
                idClient,
                idLoanClient,
                creditStatus,
                accountStatus,
                cryptoStatus,
                cardStatus
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    // Credit
    fun queryCreditOffer(
        pkUser: Int,
        idBrand: Int
    ): ApolloCall<CreditOfferQuery.Data> =
        apolloAuthorizedClient.query(CreditOfferQuery(pkUser, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryPaymentAmount(
        amount: Int,
        months: String?,
        idProduct: String,
        currencySymbol: String,
        user: String,
        idBrand: Int
    ): ApolloCall<PaymentAmountQuery.Data> =
        apolloAuthorizedClient.query(
            PaymentAmountQuery(
                amount,
                months ?: "",
                idProduct,
                currencySymbol,
                user,
                idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditApplication(
        idUserRequest: Int,
        pkUser: Int,
        descPromotion: String,
        interestRate: String?,
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
        apolloAuthorizedClient.mutation(
            SaveCreditApplicationMutation(
                idUserRequest,
                pkUser,
                descPromotion,
                interestRate ?: "",
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
        idUserRequest: Int
    ): ApolloCall<ScreenConfigQuery.Data> =
        apolloAuthorizedClient.query(
            ScreenConfigQuery(pkUser, user, idBrand, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<HomeProvinceQuery.Data> =
        apolloAuthorizedClient.query(
            HomeProvinceQuery(pkUser, user, idBrand, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): ApolloCall<HomeCantonQuery.Data> =
        apolloAuthorizedClient.query(
            HomeCantonQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): ApolloCall<HomeDistrictQuery.Data> =
        apolloAuthorizedClient.query(
            HomeDistrictQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<CompanyProvinceQuery.Data> =
        apolloAuthorizedClient.query(
            CompanyProvinceQuery(pkUser, user, idBrand, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): ApolloCall<CompanyCantonQuery.Data> =
        apolloAuthorizedClient.query(
            CompanyCantonQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): ApolloCall<CompanyDistrictQuery.Data> =
        apolloAuthorizedClient.query(
            CompanyDistrictQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditFlowStep(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): ApolloCall<SaveCreditFlowInputMutation.Data> =
        apolloAuthorizedClient.mutation(
            SaveCreditFlowInputMutation(
                user,
                idBrand,
                infoQuestion.map { it.mapToApolloModel() },
                idLogUserRequest,
                idUser,
                currentStep
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryProfession(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<ProfessionsQuery.Data> =
        apolloAuthorizedClient.query(ProfessionsQuery(pkUser, user, idBrand, idUserRequest))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryOccupation(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<OcupationsQuery.Data> =
        apolloAuthorizedClient.query(OcupationsQuery(pkUser, user, idBrand, idUserRequest))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationTermsAndConditions(
        user: String,
        idBrand: Int,
        systemInDarkTheme: Boolean
    ): ApolloCall<TermsAndConditionsQuery.Data> =
        apolloAuthorizedClient.query(
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
        idLoanClient: Int
    ): ApolloCall<GetClientBankAccountQuery.Data> =
        apolloAuthorizedClient.query(
            GetClientBankAccountQuery(
                user,
                idBrand,
                idClient,
                idLoanClient
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryListCardsVD(
        user: String,
        idBrand: Int,
        identification: String
    ): ApolloCall<ListCardVDQuery.Data> =
        apolloAuthorizedClient.query(
            ListCardVDQuery(
                identification,
                user,
                idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryBanksAndRegularExpression(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<BanksAndRegularExpressionQuery.Data> =
        apolloAuthorizedClient.query(
            BanksAndRegularExpressionQuery(
                pkUser,
                user,
                idBrand,
                idUserRequest
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetPaymentPoints(
        idBrand: Int
    ): ApolloCall<GetPaymentPointsQuery.Data> =
        apolloAuthorizedClient.query(
            GetPaymentPointsQuery(
                idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetExchangeCreditRate(
        idBrand: Int,
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): ApolloCall<GetExchangeRateCreditQuery.Data> =
        apolloAuthorizedClient.query(
            GetExchangeRateCreditQuery(
                idBrand,
                user,
                identification,
                idOriginCurrency,
                idDestinationCurrency,
                amount
            )
        )
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationProcessPaymentList(
        user: String,
        idBrand: Int,
        customerId: Int,
        identification: String,
        originAccountNumber: String,
        destinyAccountNumber: String,
        currencyId: String,
        customerName: String,
        description: String,
        destinyAccount: List<DestinyAccount>,
        amount: Any
    ): ApolloCall<ProccessPaymentListMutation.Data> =
        apolloAuthorizedClient.mutation(
            ProccessPaymentListMutation(
                user,
                idBrand,
                customerId,
                identification,
                originAccountNumber,
                destinyAccountNumber,
                currencyId,
                customerName,
                description,
                destinyAccount.map { it.mapToApolloModel() },
                amount
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun subscriptionCreditContractEvent(
        idPrint: Long,
        idBrand: Int
    ): ApolloCall<CreditContractEventSubscription.Data> = apolloAuthorizedClient.subscription(
        CreditContractEventSubscription(
            idPrint,
            idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditOperation(
        idUserRequest: Long,
        pkUser: Long,
        user: String,
        idBrand: Int
    ): ApolloCall<SaveCreditOperationMutation.Data> = apolloAuthorizedClient.mutation(
        SaveCreditOperationMutation(
            idUserRequest,
            pkUser,
            user,
            idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationActivatedClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long,
        idCurrency: Int
    ): ApolloCall<ActivatedClientAutomaticDebitMutation.Data> =
        apolloAuthorizedClient.mutation(
            ActivatedClientAutomaticDebitMutation(
                user,
                idBrand,
                idClient,
                idLoanClient,
                origin,
                idAccount,
                idCurrency
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int
    ): ApolloCall<GetClientAutomaticDebitQuery.Data> =
        apolloAuthorizedClient.query(
            GetClientAutomaticDebitQuery(
                user,
                idBrand,
                idClient,
                idLoanClient
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSendCreditContractEvent(
        idImpresion: Long,
        idBrand: Int,
        link: String,
        active: Boolean,
        statusEvicertia: String,
        statusOnfido: String,
        currentStep: String
    ): ApolloCall<SendCreditContractEventMutation.Data> =
        apolloAuthorizedClient.mutation(
            SendCreditContractEventMutation(
                idImpresion,
                idBrand,
                link,
                active,
                statusEvicertia,
                statusOnfido,
                currentStep
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    // Security
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
    ): ApolloCall<UserValidationMutation.Data> = apolloAuthorizedClient.mutation(
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
    ): ApolloCall<UpdateUserRegisterMutation.Data> = apolloAuthorizedClient.mutation(
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
        apolloAuthorizedClient.query(ValidationSecurityQuery(pkUser, password, user, idBrand))
            .fetchPolicy(
                FetchPolicy.NetworkOnly
            )

    fun queryValidateUserStatus(
        pkUser: Int,
        identification: String,
        email: String,
        idBrand: Int
    ): ApolloCall<ValidateUserStatusQuery.Data> =
        apolloAuthorizedClient.query(
            ValidateUserStatusQuery(
                pkUser,
                identification,
                email,
                idBrand
            )
        ).fetchPolicy(
            FetchPolicy.NetworkOnly
        )

    fun queryDataInformationClient(
        identification: String,
        idBrand: Int,
        user: String
    ): ApolloCall<DataInformationClientQuery.Data> =
        apolloAuthorizedClient.query(
            DataInformationClientQuery(
                identification,
                idBrand,
                user
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
    ): ApolloCall<SendPinProcessMutation.Data> = apolloAuthorizedClient.mutation(
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
    ).fetchPolicy(FetchPolicy.NetworkOnly)

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
    ).fetchPolicy(FetchPolicy.NetworkOnly)

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
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCatalogIdentification(
        idBrand: Int,
        user: String
    ): ApolloCall<CatalogTypeIndentificationQuery.Data> =
        apolloAuthorizedClient.query(CatalogTypeIndentificationQuery(user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetCountry(user: String): ApolloCall<GetCountryQuery.Data> =
        apolloAuthorizedClient.query(GetCountryQuery(user)).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetConfigurationVersion(
        platform: String,
        appVersion: String,
        idBrand: Int
    ): ApolloCall<GetConfigurationVersionQuery.Data> =
        apolloAuthorizedClient.query(GetConfigurationVersionQuery(platform, appVersion, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryValidateAccount(
        account: String,
        identification: String,
        user: String,
        idBrand: Int
    ): ApolloCall<ValidateBankAccountQuery.Data> =
        apolloAuthorizedClient.query(
            ValidateBankAccountQuery(
                account,
                identification,
                user,
                idBrand
            )
        )
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetCompanyNameByIdentification(
        identification: String,
        idBrand: Int,
        user: String
    ): ApolloCall<GetCompanyNameByIdentificationQuery.Data> =
        apolloAuthorizedClient.query(
            GetCompanyNameByIdentificationQuery(identification, idBrand, user)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationOnfidoCheckProcess(
        identification: String,
        applicantId: String,
        currentFlow: String,
        pkUser: Long,
        userRequestId: Long,
        idBrand: Int,
        user: String
    ): ApolloCall<OnfidoCheckProcessMutation.Data> =
        apolloAuthorizedClient.mutation(
            OnfidoCheckProcessMutation(
                identification,
                applicantId,
                currentFlow,
                pkUser,
                userRequestId,
                idBrand,
                user
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    // SmartAccount
    fun queryCivilStatus(
        pkUser: String,
        idBrand: Int
    ): ApolloCall<CivilStatusQuery.Data> =
        apolloAuthorizedClient.query(CivilStatusQuery(pkUser, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryProfessionSmart(
        pkUser: String,
        idBrand: Int
    ): ApolloCall<ProfessionSmartQuery.Data> =
        apolloAuthorizedClient.query(ProfessionSmartQuery(pkUser, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryAddressLevelOne(
        user: String,
        idBrand: Int
    ): ApolloCall<AddressLevel1Query.Data> =
        apolloAuthorizedClient.query(AddressLevel1Query(user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryAddressLevelTwo(
        user: String,
        idBrand: Int,
        idAddressLevel1: String
    ): ApolloCall<AddressLevel2Query.Data> =
        apolloAuthorizedClient.query(AddressLevel2Query(user, idBrand, idAddressLevel1))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryAddressLevelThree(
        user: String,
        idBrand: Int,
        idAddressLevel1: String,
        idAddressLevel2: String
    ): ApolloCall<AddressLevel3Query.Data> =
        apolloAuthorizedClient.query(
            AddressLevel3Query(
                user,
                idBrand,
                idAddressLevel1,
                idAddressLevel2
            )
        )
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryNationality(
        user: String,
        idBrand: Int
    ): ApolloCall<NationalityQuery.Data> =
        apolloAuthorizedClient.query(NationalityQuery(user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryStepByStep(
        user: String,
        idBrand: Int,
        idRequest: Int
    ): ApolloCall<StepByStepQuery.Data> =
        apolloAuthorizedClient.query(StepByStepQuery(user, idBrand, idRequest))
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
        positionJob: String,
        idEconomicActivity: Long,
        income: Double,
        addressDetail: String,
        user: String,
        idBrand: Int,
        currentStep: String,
        institutionPension: String,
        specifiesIncomeSource: String,
        entrepreneurship: String,
        legalID: String,
        isActivityOfArt15: Boolean,
        isUsCitizen: Boolean,
        isPEP: Boolean,
        isUSTaxPayer: Boolean,
        isTaxPayer: Boolean,
        beneficiaries: List<Beneficiary>,
        idJobLevel2: Long,
        idJobLevel3: Long
    ): ApolloCall<GlobalRequestMutation.Data> =
        apolloAuthorizedClient.mutation(
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
                positionJob,
                idEconomicActivity,
                income,
                addressDetail,
                user,
                idBrand,
                currentStep,
                specifiesIncomeSource,
                entrepreneurship,
                legalID,
                isActivityOfArt15,
                isUsCitizen,
                isPEP,
                isUSTaxPayer,
                isTaxPayer,
                beneficiaries.map { beneficiary ->
                    BeneficiaryRequestDtoInput(
                        fullName = Optional.presentIfNotNull(beneficiary.fullName),
                        relationship = Optional.presentIfNotNull(beneficiary.relationship.toString()),
                        allocationPercentage = Optional.presentIfNotNull(beneficiary.allocationPercentage)
                    )
                },
                idJobLevel2,
                idJobLevel3
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGeneralEconomicActivity(
        user: String,
        idBrand: Int
    ): ApolloCall<GeneralEconomicActivityQuery.Data> =
        apolloAuthorizedClient.query(GeneralEconomicActivityQuery(user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryRelationship(
        user: String,
        idBrand: Int,
        option: Int
    ): ApolloCall<RelationshipQuery.Data> =
        apolloAuthorizedClient.query(RelationshipQuery(user, idBrand, option))
            .fetchPolicy(FetchPolicy.NetworkOnly)
}
