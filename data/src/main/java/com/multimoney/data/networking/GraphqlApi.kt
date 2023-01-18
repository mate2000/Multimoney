package com.multimoney.data.networking

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.multimoney.data.mapper.credit.mapToApolloModel
import com.multimoney.data.networking.graphql.apollomodel.AccountSmartContractEventSubscription
import com.multimoney.data.networking.graphql.apollomodel.AccountStatementQuery
import com.multimoney.data.networking.graphql.apollomodel.ActivatedCardAutomaticDebitMutation
import com.multimoney.data.networking.graphql.apollomodel.ActivatedClientAutomaticDebitMutation
import com.multimoney.data.networking.graphql.apollomodel.AddressLevel1Query
import com.multimoney.data.networking.graphql.apollomodel.AddressLevel2Query
import com.multimoney.data.networking.graphql.apollomodel.AddressLevel3Query
import com.multimoney.data.networking.graphql.apollomodel.BalanceCardInformationQuery
import com.multimoney.data.networking.graphql.apollomodel.BalanceQuery
import com.multimoney.data.networking.graphql.apollomodel.BanksAndRegularExpressionQuery
import com.multimoney.data.networking.graphql.apollomodel.CardBlockingNVMutation
import com.multimoney.data.networking.graphql.apollomodel.CardIssuanceNVQuery
import com.multimoney.data.networking.graphql.apollomodel.CardUnblockingNVMutation
import com.multimoney.data.networking.graphql.apollomodel.CatalogTypeIndentificationQuery
import com.multimoney.data.networking.graphql.apollomodel.ChangeDeviceMutation
import com.multimoney.data.networking.graphql.apollomodel.ChangeEmailMutation
import com.multimoney.data.networking.graphql.apollomodel.ChangePhoneMutation
import com.multimoney.data.networking.graphql.apollomodel.CivilStatusQuery
import com.multimoney.data.networking.graphql.apollomodel.CompanyCantonQuery
import com.multimoney.data.networking.graphql.apollomodel.CompanyDistrictQuery
import com.multimoney.data.networking.graphql.apollomodel.CompanyProvinceQuery
import com.multimoney.data.networking.graphql.apollomodel.CreditContractEventSubscription
import com.multimoney.data.networking.graphql.apollomodel.CreditExtensionAmountQuery
import com.multimoney.data.networking.graphql.apollomodel.CreditExtensionMessageQuery
import com.multimoney.data.networking.graphql.apollomodel.CreditOfferQuery
import com.multimoney.data.networking.graphql.apollomodel.DataInformationClientQuery
import com.multimoney.data.networking.graphql.apollomodel.DeactivatedCardAutomaticDebitMutation
import com.multimoney.data.networking.graphql.apollomodel.DeactivatedClientAutomaticDebitMutation
import com.multimoney.data.networking.graphql.apollomodel.DeleteCardVDMutation
import com.multimoney.data.networking.graphql.apollomodel.DeleteTokenDeviceNVMutation
import com.multimoney.data.networking.graphql.apollomodel.EmploymentSituationQuery
import com.multimoney.data.networking.graphql.apollomodel.ExchangeRateQuery
import com.multimoney.data.networking.graphql.apollomodel.GeneralEconomicActivityQuery
import com.multimoney.data.networking.graphql.apollomodel.GetAvailableListOfCryptoCoinsQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCardAutomaticDebitQuery
import com.multimoney.data.networking.graphql.apollomodel.GetClientAutomaticDebitQuery
import com.multimoney.data.networking.graphql.apollomodel.GetClientBankAccountQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCompanyNameByIdentificationQuery
import com.multimoney.data.networking.graphql.apollomodel.GetConfigurationVersionQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCoreBankMovementsQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCountryContactQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCountryQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCryptoCurrencyMovementQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCryptoCurrencyNewsQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCryptoMovementsQuery
import com.multimoney.data.networking.graphql.apollomodel.GetCryptoPriceHistoryQuery
import com.multimoney.data.networking.graphql.apollomodel.GetExchangeRateCreditQuery
import com.multimoney.data.networking.graphql.apollomodel.GetHistoricClientBalanceQuery
import com.multimoney.data.networking.graphql.apollomodel.GetHistoricalCurrencyPricesQuery
import com.multimoney.data.networking.graphql.apollomodel.GetInfoDepositQuery
import com.multimoney.data.networking.graphql.apollomodel.GetLinkCreditContractQuery
import com.multimoney.data.networking.graphql.apollomodel.GetPaymentPointsQuery
import com.multimoney.data.networking.graphql.apollomodel.GetPromissoryNoteDetailQuery
import com.multimoney.data.networking.graphql.apollomodel.GlobalRequestMutation
import com.multimoney.data.networking.graphql.apollomodel.HomeCantonQuery
import com.multimoney.data.networking.graphql.apollomodel.HomeDistrictQuery
import com.multimoney.data.networking.graphql.apollomodel.HomeProvinceQuery
import com.multimoney.data.networking.graphql.apollomodel.InitialRequestSmartAccountMutation
import com.multimoney.data.networking.graphql.apollomodel.ListCardVDQuery
import com.multimoney.data.networking.graphql.apollomodel.ListMiniCardsQuery
import com.multimoney.data.networking.graphql.apollomodel.ListSinpeAccountQuery
import com.multimoney.data.networking.graphql.apollomodel.ManageSinpeAccountSaveMutation
import com.multimoney.data.networking.graphql.apollomodel.NationalityQuery
import com.multimoney.data.networking.graphql.apollomodel.OcupationsQuery
import com.multimoney.data.networking.graphql.apollomodel.OnfidoCheckProcessMutation
import com.multimoney.data.networking.graphql.apollomodel.OnfidoIntialProcessMutation
import com.multimoney.data.networking.graphql.apollomodel.PayCreditVDMutation
import com.multimoney.data.networking.graphql.apollomodel.PaymentAmountQuery
import com.multimoney.data.networking.graphql.apollomodel.ProccessPaymentListMutation
import com.multimoney.data.networking.graphql.apollomodel.ProcessCreditExtensionDetailMutation
import com.multimoney.data.networking.graphql.apollomodel.ProcessSinpeTransferMutation
import com.multimoney.data.networking.graphql.apollomodel.ProcessTransferVisaToSmartVDMutation
import com.multimoney.data.networking.graphql.apollomodel.ProfessionSmartQuery
import com.multimoney.data.networking.graphql.apollomodel.ProfessionsQuery
import com.multimoney.data.networking.graphql.apollomodel.QuickActionsQuery
import com.multimoney.data.networking.graphql.apollomodel.RelationshipQuery
import com.multimoney.data.networking.graphql.apollomodel.RequestChangeDeviceMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveAutomatedSmartAccountMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveClientBankAccountMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveCreditApplicationMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveCreditExtensionDetailMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveCreditFlowInputMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveCreditOfferMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveCreditOperationMutation
import com.multimoney.data.networking.graphql.apollomodel.SaveTermsAndConditionsCreditMutation
import com.multimoney.data.networking.graphql.apollomodel.ScreenConfigQuery
import com.multimoney.data.networking.graphql.apollomodel.SendCreditContractEventMutation
import com.multimoney.data.networking.graphql.apollomodel.SendPinProcessMutation
import com.multimoney.data.networking.graphql.apollomodel.SmartAccountTypeQuery
import com.multimoney.data.networking.graphql.apollomodel.StepByStepQuery
import com.multimoney.data.networking.graphql.apollomodel.TermsAndConditionsQuery
import com.multimoney.data.networking.graphql.apollomodel.TermsAndConditionsSignedQuery
import com.multimoney.data.networking.graphql.apollomodel.UpdateCardVDMutation
import com.multimoney.data.networking.graphql.apollomodel.UpdateFavoriteContactSmartMutation
import com.multimoney.data.networking.graphql.apollomodel.UpdateUserRegisterMutation
import com.multimoney.data.networking.graphql.apollomodel.UserPhoneMobileSaveMutation
import com.multimoney.data.networking.graphql.apollomodel.UserValidationMutation
import com.multimoney.data.networking.graphql.apollomodel.ValidateBankAccountQuery
import com.multimoney.data.networking.graphql.apollomodel.ValidateOTPMutation
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
    ): ApolloCall<BalanceQuery.Data> = apolloAuthorizedClient.query(
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

    fun queryBalanceCardInformation(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        cardStatus: Int
    ): ApolloCall<BalanceCardInformationQuery.Data> =
        apolloAuthorizedClient.query(
            BalanceCardInformationQuery(
                user,
                identification,
                idBrand,
                idClient,
                idLoanClient,
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
    ): ApolloCall<PaymentAmountQuery.Data> = apolloAuthorizedClient.query(
        PaymentAmountQuery(
            amount,
            months ?: "",
            idProduct,
            currencySymbol,
            user,
            idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetPromissoryNoteDetail(
        idBrand: Int,
        idLoanClient: Int,
        pageNumber: Int,
        pageSize: Int,
        option: String
    ): ApolloCall<GetPromissoryNoteDetailQuery.Data> =
        apolloAuthorizedClient.query(
            GetPromissoryNoteDetailQuery(
                idBrand,
                idLoanClient,
                pageNumber,
                pageSize,
                option
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
    ): ApolloCall<SaveCreditApplicationMutation.Data> = apolloAuthorizedClient.mutation(
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
    ): ApolloCall<ScreenConfigQuery.Data> = apolloAuthorizedClient.query(
        ScreenConfigQuery(pkUser, user, idBrand, idUserRequest)
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<HomeProvinceQuery.Data> = apolloAuthorizedClient.query(
        HomeProvinceQuery(pkUser, user, idBrand, idUserRequest)
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): ApolloCall<HomeCantonQuery.Data> = apolloAuthorizedClient.query(
        HomeCantonQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryHomeDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): ApolloCall<HomeDistrictQuery.Data> = apolloAuthorizedClient.query(
        HomeDistrictQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyProvince(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<CompanyProvinceQuery.Data> = apolloAuthorizedClient.query(
        CompanyProvinceQuery(pkUser, user, idBrand, idUserRequest)
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyCanton(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): ApolloCall<CompanyCantonQuery.Data> = apolloAuthorizedClient.query(
        CompanyCantonQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCompanyDistrict(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ): ApolloCall<CompanyDistrictQuery.Data> = apolloAuthorizedClient.query(
        CompanyDistrictQuery(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditFlowStep(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): ApolloCall<SaveCreditFlowInputMutation.Data> = apolloAuthorizedClient.mutation(
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

    fun queryEmploymentSituation(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<EmploymentSituationQuery.Data> =
        apolloAuthorizedClient.query(EmploymentSituationQuery(pkUser, user, idBrand, idUserRequest))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationTermsAndConditions(
        user: String,
        idBrand: Int,
        systemInDarkTheme: Boolean
    ): ApolloCall<TermsAndConditionsQuery.Data> = apolloAuthorizedClient.query(
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
        idLoanClient: Int,
        process: String
    ): ApolloCall<GetClientBankAccountQuery.Data> = apolloAuthorizedClient.query(
        GetClientBankAccountQuery(
            user,
            idBrand,
            idClient,
            idLoanClient,
            process
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryBanksAndRegularExpression(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): ApolloCall<BanksAndRegularExpressionQuery.Data> = apolloAuthorizedClient.query(
        BanksAndRegularExpressionQuery(
            pkUser,
            user,
            idBrand,
            idUserRequest
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetPaymentPoints(
        idBrand: Int
    ): ApolloCall<GetPaymentPointsQuery.Data> = apolloAuthorizedClient.query(
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
    ): ApolloCall<GetExchangeRateCreditQuery.Data> = apolloAuthorizedClient.query(
        GetExchangeRateCreditQuery(
            idBrand,
            user,
            identification,
            idOriginCurrency,
            idDestinationCurrency,
            amount
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

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
    ): ApolloCall<ProccessPaymentListMutation.Data> = apolloAuthorizedClient.mutation(
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

    fun queryGetLinkCreditContract(
        idPrint: Long,
        idBrand: Int,
        pkUser: Long,
        user: String
    ): ApolloCall<GetLinkCreditContractQuery.Data> =
        apolloAuthorizedClient.query(
            GetLinkCreditContractQuery(
                idPrint,
                idBrand,
                pkUser,
                user
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
    ): ApolloCall<ActivatedClientAutomaticDebitMutation.Data> = apolloAuthorizedClient.mutation(
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

    fun mutationDeactivatedClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long
    ): ApolloCall<DeactivatedClientAutomaticDebitMutation.Data> =
        apolloAuthorizedClient.mutation(
            DeactivatedClientAutomaticDebitMutation(
                user,
                idBrand,
                idClient,
                idLoanClient,
                origin,
                idAccount
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationDeactivatedCardAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        idCard: Long
    ): ApolloCall<DeactivatedCardAutomaticDebitMutation.Data> =
        apolloAuthorizedClient.mutation(
            DeactivatedCardAutomaticDebitMutation(
                user,
                idBrand,
                idClient,
                idLoanClient,
                idCard
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetClientAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int
    ): ApolloCall<GetClientAutomaticDebitQuery.Data> = apolloAuthorizedClient.query(
        GetClientAutomaticDebitQuery(
            user,
            idBrand,
            idClient,
            idLoanClient
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetCardAutomaticDebit(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long
    ): ApolloCall<GetCardAutomaticDebitQuery.Data> = apolloAuthorizedClient.query(
        GetCardAutomaticDebitQuery(
            user,
            identification,
            idBrand,
            idClient,
            idLoanClient
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCreditExtensionAmount(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int
    ): ApolloCall<CreditExtensionAmountQuery.Data> =
        apolloAuthorizedClient.query(CreditExtensionAmountQuery(idClient, currency, user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCreditExtensionMessage(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int,
        amountRequest: Double,
        idLoanClient: Long,
        quotaMax: Double,
        idProductBase: Int,
        cicle: Int
    ): ApolloCall<CreditExtensionMessageQuery.Data> = apolloAuthorizedClient.query(
        CreditExtensionMessageQuery(
            idClient,
            currency,
            user,
            idBrand,
            amountRequest,
            idLoanClient,
            quotaMax,
            idProductBase,
            cicle
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditExtensionDetail(
        pkUser: Int,
        idBrand: Int,
        user: String,
        accountNumber: String,
        amount: Double,
        month: Int,
        pkPromotionMonth: Int,
        nextPaymentDate: String,
        quota: Double,
        quotaTotal: Double,
        comissionDisbursement: Double,
        rateInterestNormalLoan: Double,
        rateInterestNormalRegular: Double,
        cicle: Int,
        idProduct: Int,
        descriptionPromotionTerm: String,
        pkPromotion: Int
    ): ApolloCall<SaveCreditExtensionDetailMutation.Data> =
        apolloAuthorizedClient.mutation(
            SaveCreditExtensionDetailMutation(
                pkUser,
                Optional.Present(idBrand),
                user,
                accountNumber,
                amount,
                month,
                pkPromotionMonth,
                nextPaymentDate,
                quota,
                quotaTotal,
                comissionDisbursement,
                rateInterestNormalLoan,
                rateInterestNormalRegular,
                cicle,
                idProduct,
                descriptionPromotionTerm,
                pkPromotion
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationProcessCreditExtensionDetail(
        user: String,
        pkUser: Int,
        idBrand: Int,
        idFlowControl: Any,
        currency: String,
        accountNumber: String,
        bankAccount: String,
        idBankAccount: Any,
        idLoanForm: Any,
        loanForm: String,
        idLoanClient: Int,
        phoneNumber: String,
        userEmail: String
    ): ApolloCall<ProcessCreditExtensionDetailMutation.Data> =
        apolloAuthorizedClient.mutation(
            ProcessCreditExtensionDetailMutation(
                user,
                pkUser,
                idBrand,
                idFlowControl,
                currency,
                accountNumber,
                bankAccount,
                idBankAccount,
                idLoanForm,
                loanForm,
                idLoanClient,
                phoneNumber,
                userEmail
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
                Optional.Present(link),
                active,
                statusEvicertia,
                statusOnfido,
                currentStep
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetInfoDeposit(
        idBrand: Int,
        idPrint: Long,
        user: String
    ): ApolloCall<GetInfoDepositQuery.Data> = apolloAuthorizedClient.query(
        GetInfoDepositQuery(Optional.Present(idBrand), idPrint, Optional.Present(user))
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
            Optional.Present(secondName),
            firstSurname,
            Optional.Present(secondSurname)
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
            Optional.Present(phoneNumber),
            Optional.Present(fullName),
            Optional.Present(firstName),
            Optional.Present(secondName),
            Optional.Present(lastName),
            Optional.Present(secondLastName),
            Optional.Present(nationality),
            Optional.Present(identification),
            Optional.Present(countryCode),
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
    ): ApolloCall<ValidateUserStatusQuery.Data> = apolloAuthorizedClient.query(
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
    ): ApolloCall<DataInformationClientQuery.Data> = apolloAuthorizedClient.query(
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
            Optional.Present(appSource),
            pkUser.toInt(),
            Optional.Present(ip),
            pinSecurity,
            Optional.Present(telephone),
            Optional.Present(sendValidatePin),
            Optional.Present(flowOrigination),
            Optional.Present(userCreate)
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
        queryType: String?,
        user: String,
        idBrand: Int
    ): ApolloCall<ValidateBankAccountQuery.Data> = apolloAuthorizedClient.query(
        ValidateBankAccountQuery(
            account,
            identification,
            Optional.presentIfNotNull(queryType),
            user,
            idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetCompanyNameByIdentification(
        identification: String,
        idBrand: Int,
        user: String
    ): ApolloCall<GetCompanyNameByIdentificationQuery.Data> = apolloAuthorizedClient.query(
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
    ): ApolloCall<OnfidoCheckProcessMutation.Data> = apolloAuthorizedClient.mutation(
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

    fun queryAccountStatement(
        creditNumber: String,
        user: String,
        idBrand: Int
    ): ApolloCall<AccountStatementQuery.Data> = apolloAuthorizedClient.query(
        AccountStatementQuery(
            creditNumber = creditNumber,
            user = user,
            idBrand = idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationUserPhoneMobileSave(
        idBrand: Int,
        idWalletCard: String,
        manufacture: String,
        pkUser: Long,
        serialNumber: String,
        user: String
    ): ApolloCall<UserPhoneMobileSaveMutation.Data> =
        apolloAuthorizedClient.mutation(
            UserPhoneMobileSaveMutation(
                idBrand,
                idWalletCard,
                manufacture,
                pkUser,
                serialNumber,
                user
            )
        )

    // SmartAccount
    fun queryGetCoreBankMovements(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        accountToken: Long,
        pageNumber: Int,
        pageSize: Int,
        monthDate: String?
    ): ApolloCall<GetCoreBankMovementsQuery.Data> =
        apolloAuthorizedClient.query(
            GetCoreBankMovementsQuery(
                user,
                idBrand,
                identificationNumber,
                accountToken,
                pageNumber,
                pageSize,
                Optional.Present(monthDate)
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

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
    ): ApolloCall<AddressLevel3Query.Data> = apolloAuthorizedClient.query(
        AddressLevel3Query(
            user,
            idBrand,
            idAddressLevel1,
            idAddressLevel2
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryNationality(
        user: String,
        idBrand: Int
    ): ApolloCall<NationalityQuery.Data> =
        apolloAuthorizedClient.query(NationalityQuery(user, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryStepByStep(
        user: String,
        idBrand: Int,
        idRequest: Long
    ): ApolloCall<StepByStepQuery.Data> =
        apolloAuthorizedClient.query(StepByStepQuery(user, idBrand, idRequest))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationGlobalRequest(
        pkUser: Int,
        status: Int,
        user: String,
        idBrand: Int,
        idGlobalRequest: Long,
        idProfessionType: Int?,
        idCivilStatusType: Long?,
        birthday: String?,
        expirationDate: String?,
        idGender: Long?,
        companyName: String?,
        aboutCompany: String?,
        idAddressLevel1: Long?,
        idAddressLevel2: Long?,
        idAddressLevel3: Long?,
        positionJob: String?,
        idEconomicActivity: Long?,
        income: Double?,
        addressDetail: String?,
        fullJobAddress: String?,
        currentStep: String?,
        institutionPension: String?,
        specifiesIncomeSource: String?,
        entrepreneurship: String?,
        legalID: String?,
        isActivityOfArt15: Boolean?,
        isUsCitizen: Boolean?,
        isPEP: Boolean?,
        isUSTaxPayer: Boolean?,
        isTaxPayer: Boolean?,
        beneficiaries: List<Beneficiary>?,
        idJobLevel1: Long?,
        idJobLevel2: Long?,
        idJobLevel3: Long?
    ): ApolloCall<GlobalRequestMutation.Data> =
        apolloAuthorizedClient.mutation(
            GlobalRequestMutation(
                pkUser,
                status,
                user,
                idBrand,
                idGlobalRequest,
                Optional.presentIfNotNull(idProfessionType),
                Optional.presentIfNotNull(idAddressLevel1),
                Optional.presentIfNotNull(idAddressLevel2),
                Optional.presentIfNotNull(birthday),
                Optional.presentIfNotNull(expirationDate),
                Optional.presentIfNotNull(idGender),
                Optional.presentIfNotNull(idCivilStatusType),
                Optional.presentIfNotNull(companyName),
                Optional.presentIfNotNull(aboutCompany),
                Optional.presentIfNotNull(institutionPension),
                Optional.presentIfNotNull(idAddressLevel3),
                Optional.presentIfNotNull(positionJob),
                Optional.presentIfNotNull(idEconomicActivity),
                Optional.presentIfNotNull(income),
                Optional.presentIfNotNull(addressDetail),
                Optional.presentIfNotNull(fullJobAddress),
                Optional.presentIfNotNull(currentStep),
                Optional.presentIfNotNull(specifiesIncomeSource),
                Optional.presentIfNotNull(entrepreneurship),
                Optional.presentIfNotNull(legalID),
                Optional.presentIfNotNull(isActivityOfArt15),
                Optional.presentIfNotNull(isUsCitizen),
                Optional.presentIfNotNull(isPEP),
                Optional.presentIfNotNull(isUSTaxPayer),
                Optional.presentIfNotNull(isTaxPayer),
                Optional.Present(
                    beneficiaries?.map { beneficiary ->
                        BeneficiaryRequestDtoInput(
                            fullName = Optional.presentIfNotNull(beneficiary.fullName),
                            relationship = Optional.presentIfNotNull(beneficiary.relationship.toString()),
                            allocationPercentage = Optional.presentIfNotNull(beneficiary.allocationPercentage)
                        )
                    }
                ),
                Optional.presentIfNotNull(idJobLevel1),
                Optional.presentIfNotNull(idJobLevel2),
                Optional.presentIfNotNull(idJobLevel3)
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveAutomatedSmartAccount(
        user: String,
        idBrand: Int,
        identificationNumber: String,
        idRequest: Long
    ): ApolloCall<SaveAutomatedSmartAccountMutation.Data> =
        apolloAuthorizedClient.mutation(
            SaveAutomatedSmartAccountMutation(
                user,
                idBrand,
                identificationNumber,
                idRequest
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationProcessTransferVisaToSmartVD(
        idCard: Long,
        tokenNumber: Long,
        identification: String,
        amount: String,
        currency: Int,
        description: String,
        cardMasked: String,
        user: String,
        idBrand: Int
    ): ApolloCall<ProcessTransferVisaToSmartVDMutation.Data> =
        apolloAuthorizedClient.mutation(
            ProcessTransferVisaToSmartVDMutation(
                idCard,
                tokenNumber,
                identification,
                amount,
                currency,
                description,
                cardMasked,
                user,
                idBrand
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

    fun mutationInitialRequestSmartAccount(
        pkUser: Long,
        idBrand: Int,
        user: String
    ): ApolloCall<InitialRequestSmartAccountMutation.Data> =
        apolloAuthorizedClient.mutation(InitialRequestSmartAccountMutation(pkUser, idBrand, user))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun subscriptionAccountSmartContractEvent(
        idBrand: Int,
        idRequestSys: Long
    ): ApolloCall<AccountSmartContractEventSubscription.Data> = apolloAuthorizedClient.subscription(
        AccountSmartContractEventSubscription(
            idBrand,
            idRequestSys
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetQuickActions(
        idBrand: Int,
        pkUser: Int,
        identification: String,
        infoCreditStatus: Int,
        infoVirtualCardStatus: Int,
        infoBankAccountStatus: Int,
        infoCriptoStatus: Int
    ): ApolloCall<QuickActionsQuery.Data> =
        apolloAuthorizedClient.query(
            QuickActionsQuery(
                idBrand,
                pkUser,
                identification,
                infoCreditStatus,
                infoVirtualCardStatus,
                infoBankAccountStatus,
                infoCriptoStatus
            )
        )
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationValidateOTP(
        email: String,
        otp: String
    ): ApolloCall<ValidateOTPMutation.Data> =
        apolloAuthorizedClient.mutation(ValidateOTPMutation(email, otp))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationChangePhone(
        identification: String,
        phone: String,
        pkUser: String,
        idBrand: Int
    ): ApolloCall<ChangePhoneMutation.Data> =
        apolloAuthorizedClient.mutation(ChangePhoneMutation(identification, phone, pkUser, idBrand))
            .fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationChangeEmail(
        idClient: Int,
        pkUser: Int,
        identification: String,
        email: String,
        registerId: Int,
        changeUser: Boolean,
        user: String,
        idBrand: Int
    ): ApolloCall<ChangeEmailMutation.Data> =
        apolloAuthorizedClient.mutation(
            ChangeEmailMutation(
                idClient = idClient,
                pkUser = pkUser,
                identification = identification,
                email = email,
                changeUser = changeUser,
                idBrand = idBrand,
                user = user,
                registerId = registerId
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryMiniCards(
        infoCreditStatus: Boolean,
        infoVirtualCardStatus: Boolean,
        infoBankAccountStatus: Boolean,
        infoCrypto: Boolean,
        userEmail: String,
        idBrand: Int
    ): ApolloCall<ListMiniCardsQuery.Data> =
        apolloAuthorizedClient.query(
            ListMiniCardsQuery(
                infoCreditStatus,
                infoVirtualCardStatus,
                infoBankAccountStatus,
                infoCrypto,
                userEmail,
                idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveClientBankAccount(
        idClient: Long,
        idBank: Int,
        accountNumber: String,
        idCurrency: Int,
        idAccountType: Int?,
        idLoanClient: Long,
        user: String,
        idBrand: Int
    ): ApolloCall<SaveClientBankAccountMutation.Data> =
        apolloAuthorizedClient.mutation(
            SaveClientBankAccountMutation(
                idClient = idClient,
                id_Banco = idBank,
                numeroCuenta = accountNumber,
                id_Moneda = idCurrency,
                id_Tipo_Cuenta = Optional.presentIfNotNull(idAccountType),
                idLoanClient = idLoanClient,
                user = user,
                idBrand = idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationManageSinpeAccountSave(
        user: String,
        idBrand: Int,
        identification: String,
        accountNumber: String,
        idCurrency: Long,
        nameAccount: String,
        country: String,
        idAccount: Long?,
        option: String?
    ): ApolloCall<ManageSinpeAccountSaveMutation.Data> =
        apolloAuthorizedClient.mutation(
            ManageSinpeAccountSaveMutation(
                user = Optional.presentIfNotNull(user),
                idBrand = Optional.presentIfNotNull(idBrand),
                identification = Optional.presentIfNotNull(identification),
                account_number = Optional.presentIfNotNull(accountNumber),
                id_Currency = Optional.presentIfNotNull(idCurrency),
                nameAccount = Optional.presentIfNotNull(nameAccount),
                country = Optional.presentIfNotNull(country),
                id_account = Optional.presentIfNotNull(idAccount),
                option = Optional.presentIfNotNull(option)
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    // Multimoney Visa

    fun queryCardIssuanceNV(
        idClient: Long,
        requestType: String,
        identification: String,
        idLoanClient: Int,
        user: String,
        idBrand: Int
    ): ApolloCall<CardIssuanceNVQuery.Data> =
        apolloAuthorizedClient.query(
            CardIssuanceNVQuery(
                idClient,
                requestType,
                identification,
                idLoanClient,
                Optional.Present(idBrand),
                Optional.Present(user)
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationDeleteTokenDeviceNV(
        identification: String,
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        idDevice: String
    ): ApolloCall<DeleteTokenDeviceNVMutation.Data> =
        apolloAuthorizedClient.mutation(
            DeleteTokenDeviceNVMutation(
                identification = identification,
                user = user,
                idBrand = idBrand,
                idClient = idClient,
                idLoanClient = idLoanClient,
                idDevice = idDevice
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryListSinpeAccount(
        user: String,
        idBrand: Int,
        identification: String,
        country: String,
        idAccount: Long,
        accountNumber: String
    ): ApolloCall<ListSinpeAccountQuery.Data> =
        apolloAuthorizedClient.query(
            ListSinpeAccountQuery(
                user,
                idBrand,
                identification,
                country,
                idAccount,
                accountNumber
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    // Crypto

    fun queryGetHistoricalClientBalance(
        user: String,
        idBrand: Int,
        identification: String,
        baseAsset: String,
        startDate: String,
        endDate: String
    ): ApolloCall<GetHistoricClientBalanceQuery.Data> =
        apolloAuthorizedClient.query(
            GetHistoricClientBalanceQuery(
                user,
                idBrand,
                identification,
                baseAsset,
                startDate,
                endDate
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetAvailableListOfCryptoCoins(
        user: String,
        idBrand: Int
    ): ApolloCall<GetAvailableListOfCryptoCoinsQuery.Data> =
        apolloAuthorizedClient.query(
            GetAvailableListOfCryptoCoinsQuery(
                user,
                idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryGetCryptoCurrencyMovements(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        order_time_begin: Any,
        order_time_end: Any,
        pagination_limit: Int,
        pagination_offset: Int
    ): ApolloCall<GetCryptoMovementsQuery.Data> =
        apolloAuthorizedClient.query(
            GetCryptoMovementsQuery(
                user,
                idBrand,
                identification,
                market,
                order_time_begin,
                order_time_end,
                pagination_limit,
                pagination_offset
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCurrencyHistoricalPrices(
        market: String,
        max_data_points: Long,
        range_begin: String,
        range_end: String,
        pagination_limit: Int,
        pagination_offset: Int,
        user: String,
        idBrand: Int
    ): ApolloCall<GetHistoricalCurrencyPricesQuery.Data> =
        apolloAuthorizedClient.query(
            GetHistoricalCurrencyPricesQuery(
                market,
                max_data_points,
                range_begin,
                range_end,
                pagination_limit,
                pagination_offset,
                user,
                idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCurrencyNews(
        baseAsset: String,
        user: String,
        idBrand: Int
    ): ApolloCall<GetCryptoCurrencyNewsQuery.Data> =
        apolloAuthorizedClient.query(
            GetCryptoCurrencyNewsQuery(
                baseAsset,
                user,
                idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    // Virtual Card

    fun queryListCardsVD(
        user: String,
        idBrand: Int,
        identification: String
    ): ApolloCall<ListCardVDQuery.Data> = apolloAuthorizedClient.query(
        ListCardVDQuery(
            identification,
            user,
            idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationUpdateCardVD(
        idCard: Long,
        identification: String,
        cardDescription: String,
        cardMasked: String,
        expirationMonth: String,
        expirationYear: String,
        verificationValue: String,
        default: Boolean,
        user: String,
        idBrand: Int
    ): ApolloCall<UpdateCardVDMutation.Data> = apolloAuthorizedClient.mutation(
        UpdateCardVDMutation(
            idCard = idCard,
            identification = identification,
            cardDescription = cardDescription,
            cardMasked = cardMasked,
            expirationMonth = expirationMonth,
            expirationYear = expirationYear,
            verificationValue = verificationValue,
            default = default,
            user = user,
            idBrand = idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationPayCreditVD(
        identification: String,
        currency: String,
        paymentAmount: Double,
        operationNumber: String,
        reference: String,
        comment: String,
        cardMasked: String,
        idCard: Long,
        idBrand: Int
    ): ApolloCall<PayCreditVDMutation.Data> = apolloAuthorizedClient.mutation(
        PayCreditVDMutation(
            identification = identification,
            currency = currency,
            paymentAmount = paymentAmount,
            operationNumber = operationNumber,
            reference = reference,
            comment = comment,
            cardMasked = cardMasked,
            idCard = idCard,
            idBrand = idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationDeleteCardVD(
        identification: String,
        user: String,
        idBrand: Int,
        idCard: Long,
    ): ApolloCall<DeleteCardVDMutation.Data> = apolloAuthorizedClient.mutation(
        DeleteCardVDMutation(
            identification = identification,
            user = user,
            idBrand = idBrand,
            idCard = idCard
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationActivatedCardAutomaticDebit(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        idCard: Long,
        cardMasked: String
    ): ApolloCall<ActivatedCardAutomaticDebitMutation.Data> = apolloAuthorizedClient.mutation(
        ActivatedCardAutomaticDebitMutation(
            user = user,
            idBrand = idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            idCard = idCard,
            cardMasked = cardMasked
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationCardBlocking(
        blockType: String,
        observations: String,
        clientId: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): ApolloCall<CardBlockingNVMutation.Data> = apolloAuthorizedClient.mutation(
        CardBlockingNVMutation(
            blockType = blockType,
            observations = observations,
            clientId = clientId,
            userApp = userApp,
            cardToken = cardToken,
            source = source,
            idLoan = idLoan,
            user = user,
            idBrand = idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationCardUnblocking(
        observations: String,
        clientId: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): ApolloCall<CardUnblockingNVMutation.Data> = apolloAuthorizedClient.mutation(
        CardUnblockingNVMutation(
            observations = observations,
            clientId = clientId,
            userApp = userApp,
            cardToken = cardToken,
            source = source,
            idLoan = idLoan,
            user = user,
            idBrand = idBrand
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    // Profile sections

    fun queryCountryContact(
        user: String,
        idBrand: Int
    ): ApolloCall<GetCountryContactQuery.Data> =
        apolloAuthorizedClient.query(
            GetCountryContactQuery(user, idBrand)
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryTermsAndConditionsSigned(
        styleDark: Boolean,
        pkUser: Int,
        identification: String,
        user: String,
        idBrand: Int
    ): ApolloCall<TermsAndConditionsSignedQuery.Data> =
        apolloAuthorizedClient.query(
            TermsAndConditionsSignedQuery(
                styleDark = styleDark,
                userId = pkUser,
                identification = identification,
                user = user,
                idBrand = idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveCreditOffer(
        pkUser: Long,
        idUserRequest: Long,
        idBrand: Int
    ): ApolloCall<SaveCreditOfferMutation.Data> =
        apolloAuthorizedClient.mutation(
            SaveCreditOfferMutation(
                pkUser = pkUser,
                idUserRequest = idUserRequest,
                idBrand = idBrand
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationResquestChangeDevice(
        email: String
    ): ApolloCall<RequestChangeDeviceMutation.Data> =
        apolloAuthorizedClient.mutation(
            RequestChangeDeviceMutation(
                email = email
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationChangeDevice(
        email: String,
        otp: String
    ): ApolloCall<ChangeDeviceMutation.Data> =
        apolloAuthorizedClient.mutation(
            ChangeDeviceMutation(
                email = email,
                otp = otp
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun querySmartExchangeRate(
        user: String,
        identification: String,
        idBrand: Int,
        abbreviation: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): ApolloCall<ExchangeRateQuery.Data> =
        apolloAuthorizedClient.query(
            ExchangeRateQuery(
                user = user,
                identification = identification,
                idOriginCurrency = idOriginCurrency,
                idDestinationCurrency = idDestinationCurrency,
                idBrand = idBrand,
                abbreviation = abbreviation,
                amount = amount
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationSaveTermsAndConditionsCredit(
        user: String,
        idBrand: Int,
        pkUser: Long,
        currentFlow: String,
        identification: String
    ): ApolloCall<SaveTermsAndConditionsCreditMutation.Data> =
        apolloAuthorizedClient.mutation(
            SaveTermsAndConditionsCreditMutation(
                user = user,
                idBrand = idBrand,
                pkUser = pkUser,
                currentFlow = currentFlow,
                identification = identification

            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun mutationProcessSinpeTransfer(
        pkUser: Int,
        identification: String,
        originCustomerIdentification: String,
        ibanAccountOrigin: String,
        originCustomerName: String,
        idCurrencyOrigin: String,
        ibanAccountDestination: String,
        destinationCustomerIdentification: String,
        destinationCustomerName: String,
        idCurrencyDestination: String,
        reasonOfTransfer: String,
        type: String,
        amountToTransfer: Double,
        exchangeRate: Double,
        idBrand: Int,
        user: String
    ): ApolloCall<ProcessSinpeTransferMutation.Data> =
        apolloAuthorizedClient.mutation(
            ProcessSinpeTransferMutation(
                pkUser = pkUser,
                identification = identification,
                originCustomerIdentification = originCustomerIdentification,
                ibanAccountOrigin = ibanAccountOrigin,
                originCustomerName = originCustomerName,
                idCurrencyOrigin = idCurrencyOrigin,
                ibanAccountDestination = ibanAccountDestination,
                destinationCustomerIdentification = destinationCustomerIdentification,
                destinationCustomerName = destinationCustomerName,
                idCurrencyDestination = idCurrencyDestination,
                reasonOfTransfer = reasonOfTransfer,
                type = type,
                amountToTransfer = amountToTransfer,
                exchangeRate = exchangeRate,
                idBrand = idBrand,
                user = user
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun querySmartAccountType(
        idBrand: Int,
        user: String
    ): ApolloCall<SmartAccountTypeQuery.Data> = apolloAuthorizedClient.query(
        SmartAccountTypeQuery(idBrand = idBrand, user = user)
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    /*
    To select as favorite send active as true, to unselect send active as false,
    To add a new favorite account send idFavorite as null
     */
    fun mutationUpdateSmartFavoriteContact(
        idFavorite: Long?,
        idAccountType: Int,
        idCustomer: Long,
        accountNumber: String,
        accountName: String?,
        email: String,
        active: Boolean,
        phoneNumber: String?,
        idCurrencyAccount: Int,
        idBrand: Int,
        user: String
    ): ApolloCall<UpdateFavoriteContactSmartMutation.Data> = apolloAuthorizedClient.mutation(
        UpdateFavoriteContactSmartMutation(
            idBrand = idBrand,
            user = user,
            idFavorite = Optional.presentIfNotNull(idFavorite),
            idAccountType = idAccountType,
            idCustomer = idCustomer,
            accountNumber = accountNumber,
            accountName = Optional.presentIfNotNull(accountName),
            phoneNumber = Optional.presentIfNotNull(phoneNumber),
            email = email,
            active = active,
            idCurrencyAccount = Optional.presentIfNotNull(idCurrencyAccount)
        )
    ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCryptoCurrencyMovements(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        startDate: String,
        endDate: String
    ): ApolloCall<GetCryptoCurrencyMovementQuery.Data> =
        apolloAuthorizedClient.query(
            GetCryptoCurrencyMovementQuery(
                user,
                idBrand,
                identification,
                market,
                startDate,
                endDate
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)

    fun queryCryptoPriceHistory(
        market: String,
        user: String,
        idBrand: Int,
        startDate: String,
        endDate: String,
        maxPoints: Long,
        paginationLimit: Int,
        paginationOffset: Int,
    ): ApolloCall<GetCryptoPriceHistoryQuery.Data> =
        apolloAuthorizedClient.query(
            GetCryptoPriceHistoryQuery(
                market,
                user,
                idBrand,
                startDate,
                endDate,
                maxPoints,
                paginationLimit,
                paginationOffset
            )
        ).fetchPolicy(FetchPolicy.NetworkOnly)
}
