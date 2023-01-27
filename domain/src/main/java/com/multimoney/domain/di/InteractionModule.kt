package com.multimoney.domain.di

import com.multimoney.domain.interaction.accountsmart.MutationAddACHAccountUseCase
import com.multimoney.domain.interaction.accountsmart.MutationAddACHAccountUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.MutationGlobalRequestUseCase
import com.multimoney.domain.interaction.accountsmart.MutationGlobalRequestUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.MutationInitialRequestUseCase
import com.multimoney.domain.interaction.accountsmart.MutationInitialUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.MutationProcessSinpeTransferUseCase
import com.multimoney.domain.interaction.accountsmart.MutationProcessSinpeTransferUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransferVisaToSmartVDUseCase
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransferVisaToSmartVDUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.MutationSaveAutomatedSmartAccountUseCase
import com.multimoney.domain.interaction.accountsmart.MutationSaveAutomatedSmartAccountUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.MutationSaveSinpeAccountUseCase
import com.multimoney.domain.interaction.accountsmart.MutationSaveSinpeAccountUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.MutationUpdateFavoriteSmartUseCase
import com.multimoney.domain.interaction.accountsmart.MutationUpdateFavoriteSmartUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelOneUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelOneUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelThreeUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelThreeUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryBankListTransfer365UseCase
import com.multimoney.domain.interaction.accountsmart.QueryBankListTransfer365UseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryCivilStatusUseCase
import com.multimoney.domain.interaction.accountsmart.QueryCivilStatusUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryGeneralEconomicActivityUseCase
import com.multimoney.domain.interaction.accountsmart.QueryGeneralEconomicActivityUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryGetCoreBankMovementsUseCase
import com.multimoney.domain.interaction.accountsmart.QueryGetCoreBankMovementsUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryGetPagedSmartMovementsUseCase
import com.multimoney.domain.interaction.accountsmart.QueryGetPagedSmartMovementsUseCaseUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryNationalitiesUseCase
import com.multimoney.domain.interaction.accountsmart.QueryNationalitiesUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryProfessionUseCase
import com.multimoney.domain.interaction.accountsmart.QueryProfessionUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryRelatedContactsByPhoneUseCase
import com.multimoney.domain.interaction.accountsmart.QueryRelatedContactsByPhoneUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryRelationshipUseCase
import com.multimoney.domain.interaction.accountsmart.QueryRelationshipUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountTypeUseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountTypeUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryStepByStepUseCase
import com.multimoney.domain.interaction.accountsmart.QueryStepByStepUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.SubscriptionAccountSmartContractUseCase
import com.multimoney.domain.interaction.accountsmart.SubscriptionAccountSmartContractUseCaseImpl
import com.multimoney.domain.interaction.balance.QueryBalanceCardInformationUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceCardInformationUseCaseImpl
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationActivateClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.MutationActivateClientAutomaticDebitUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationDeactivateCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.MutationDeactivateCardAutomaticDebitUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationDeactivateClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.MutationDeactivateClientAutomaticDebitUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationProcessCreditExtensionDetailUseCase
import com.multimoney.domain.interaction.credit.MutationProcessCreditExtensionDetailUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationProcessPaymentListUseCase
import com.multimoney.domain.interaction.credit.MutationProcessPaymentListUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveClientBankAccountUseCase
import com.multimoney.domain.interaction.credit.MutationSaveClientBankAccountUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveCreditApplicationUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditApplicationUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveCreditExtensionDetailUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditExtensionDetailUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveCreditFlowStepUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditFlowStepUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveCreditOfferUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditOfferUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveCreditOperationUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditOperationUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveTermsAndConditionsCreditUseCase
import com.multimoney.domain.interaction.credit.MutationSaveTermsAndConditionsCreditUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSendCreditContractEventUseCase
import com.multimoney.domain.interaction.credit.MutationSendCreditContractEventUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryAccountStatementUseCase
import com.multimoney.domain.interaction.credit.QueryAccountStatementUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCase
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCompanyCantonUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyCantonUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCompanyDistrictUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyDistrictUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCompanyProvinceUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyProvinceUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCreditExtensionAmountUseCase
import com.multimoney.domain.interaction.credit.QueryCreditExtensionAmountUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCreditExtensionMessageUseCase
import com.multimoney.domain.interaction.credit.QueryCreditExtensionMessageUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCase
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryEmploymentSituationUseCase
import com.multimoney.domain.interaction.credit.QueryEmploymentSituationUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetCardAutomaticDebitUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientAutomaticDebitUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetExchangeRateCreditUseCase
import com.multimoney.domain.interaction.credit.QueryGetExchangeRateCreditUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetInfoDepositUseCase
import com.multimoney.domain.interaction.credit.QueryGetInfoDepositUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetLinkCreditContractUseCase
import com.multimoney.domain.interaction.credit.QueryGetLinkCreditContractUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetPagedCreditMovementsUseCase
import com.multimoney.domain.interaction.credit.QueryGetPagedCreditMovementsUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetPaymentPointsUseCase
import com.multimoney.domain.interaction.credit.QueryGetPaymentPointsUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetPromissoryNoteDetail
import com.multimoney.domain.interaction.credit.QueryGetPromissoryNoteDetailImpl
import com.multimoney.domain.interaction.credit.QueryHomeCantonUseCase
import com.multimoney.domain.interaction.credit.QueryHomeCantonUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryHomeDistrictUseCase
import com.multimoney.domain.interaction.credit.QueryHomeDistrictUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryHomeProvinceUseCase
import com.multimoney.domain.interaction.credit.QueryHomeProvinceUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryOccupationUseCase
import com.multimoney.domain.interaction.credit.QueryOccupationUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCase
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryProfessionsUseCase
import com.multimoney.domain.interaction.credit.QueryProfessionsUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryScreenConfigUseCase
import com.multimoney.domain.interaction.credit.QueryScreenConfigUseCaseImpl
import com.multimoney.domain.interaction.credit.SubscriptionCreditContractEventUseCase
import com.multimoney.domain.interaction.credit.SubscriptionCreditContractEventUseCaseImpl
import com.multimoney.domain.interaction.credit.TermsAndConditionsUseCase
import com.multimoney.domain.interaction.credit.TermsAndConditionsUseCaseImpl
import com.multimoney.domain.interaction.crypto.GetAvailableListOfCryptoCoinsUseCase
import com.multimoney.domain.interaction.crypto.GetAvailableListOfCryptoCoinsUseCaseImpl
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyMovementsUseCase
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyMovementsUseCaseImpl
import com.multimoney.domain.interaction.crypto.GetCurrencyHistoricalPricesUseCase
import com.multimoney.domain.interaction.crypto.GetCurrencyHistoricalPricesUseCaseImpl
import com.multimoney.domain.interaction.crypto.GetCurrencyNewsUseCase
import com.multimoney.domain.interaction.crypto.GetCurrencyNewsUseCaseImpl
import com.multimoney.domain.interaction.crypto.GetHistoricalClientBalanceUseCase
import com.multimoney.domain.interaction.crypto.GetHistoricalClientBalanceUseCaseImpl
import com.multimoney.domain.interaction.mmvisa.MutationDeleteTokenDeviceNVUseCase
import com.multimoney.domain.interaction.mmvisa.MutationDeleteTokenDeviceNVUseCaseImpl
import com.multimoney.domain.interaction.mmvisa.QueryCardIssuanceNVUseCase
import com.multimoney.domain.interaction.mmvisa.QueryCardIssuanceNVUseCaseImpl
import com.multimoney.domain.interaction.profile.QueryCountryContactUseCase
import com.multimoney.domain.interaction.profile.QueryCountryContactUseCaseImpl
import com.multimoney.domain.interaction.profile.QueryTermsAndConditionsSignedUseCase
import com.multimoney.domain.interaction.profile.QueryTermsAndConditionsSignedUseCaseImpl
import com.multimoney.domain.interaction.security.MutationChangeDeviceUseCase
import com.multimoney.domain.interaction.security.MutationChangeDeviceUseCaseImpl
import com.multimoney.domain.interaction.security.MutationChangeEmailUseCase
import com.multimoney.domain.interaction.security.MutationChangeEmailUseCaseImpl
import com.multimoney.domain.interaction.security.MutationChangePhoneUseCase
import com.multimoney.domain.interaction.security.MutationChangePhoneUseCaseImpl
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCaseImpl
import com.multimoney.domain.interaction.security.MutationOnfidoCheckProcessUseCase
import com.multimoney.domain.interaction.security.MutationOnfidoCheckProcessUseCaseImpl
import com.multimoney.domain.interaction.security.MutationRequestChangeDeviceUseCase
import com.multimoney.domain.interaction.security.MutationRequestChangeDeviceUseCaseImpl
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCase
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCaseImpl
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCase
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCaseImpl
import com.multimoney.domain.interaction.security.MutationUserPhoneMobileSaveUseCase
import com.multimoney.domain.interaction.security.MutationUserPhoneMobileSaveUseCaseImpl
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.interaction.security.MutationUserValidationUseCaseImpl
import com.multimoney.domain.interaction.security.MutationValidateOTPUseCase
import com.multimoney.domain.interaction.security.MutationValidateOTPUseCaseImpl
import com.multimoney.domain.interaction.security.QueryCatalogDocumentTypeUseCase
import com.multimoney.domain.interaction.security.QueryCatalogDocumentTypeUseCaseImpl
import com.multimoney.domain.interaction.security.QueryCompanyNameByIdentityUseCase
import com.multimoney.domain.interaction.security.QueryCompanyNameByIdentityUseCaseImpl
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCase
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCaseImpl
import com.multimoney.domain.interaction.security.QueryGetConfigurationVersionUseCase
import com.multimoney.domain.interaction.security.QueryGetConfigurationVersionUseCaseImpl
import com.multimoney.domain.interaction.security.QueryGetCountryUseCase
import com.multimoney.domain.interaction.security.QueryGetCountryUseCaseImpl
import com.multimoney.domain.interaction.security.QueryGetQuickActionsImpl
import com.multimoney.domain.interaction.security.QueryGetQuickActionsUseCase
import com.multimoney.domain.interaction.security.QueryMiniCardsUseCase
import com.multimoney.domain.interaction.security.QueryMiniCardsUseCaseImpl
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCase
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCaseImpl
import com.multimoney.domain.interaction.security.QueryValidatePinUseCase
import com.multimoney.domain.interaction.security.QueryValidatePinUseCaseImpl
import com.multimoney.domain.interaction.security.QueryValidateUserExistsUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserExistsUseCaseImpl
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCaseImpl
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCaseImpl
import com.multimoney.domain.interaction.virtualcard.MutationActivatedCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.virtualcard.MutationActivatedCardAutomaticDebitUseCaseImpl
import com.multimoney.domain.interaction.virtualcard.MutationCardBlockingUseCase
import com.multimoney.domain.interaction.virtualcard.MutationCardBlockingUseCaseImpl
import com.multimoney.domain.interaction.virtualcard.MutationCardUnblockingUseCase
import com.multimoney.domain.interaction.virtualcard.MutationCardUnblockingUseCaseImpl
import com.multimoney.domain.interaction.virtualcard.MutationDeleteCardVDUseCase
import com.multimoney.domain.interaction.virtualcard.MutationDeleteCardVDUseCaseImpl
import com.multimoney.domain.interaction.virtualcard.MutationPayCreditVDUseCase
import com.multimoney.domain.interaction.virtualcard.MutationPayCreditVDUseCaseImpl
import com.multimoney.domain.interaction.virtualcard.MutationUpdateCardVDUseCase
import com.multimoney.domain.interaction.virtualcard.MutationUpdateCardVDUseCaseImpl
import com.multimoney.domain.interaction.virtualcard.QueryListCardVDUseCase
import com.multimoney.domain.interaction.virtualcard.QueryListCardVDUseCaseImpl
import com.multimoney.domain.repository.BalanceRepository
import com.multimoney.domain.repository.CreditRepository
import com.multimoney.domain.repository.CryptoRepository
import com.multimoney.domain.repository.MultimoneyVisaRepository
import com.multimoney.domain.repository.ProfileRepository
import com.multimoney.domain.repository.SecurityRepository
import com.multimoney.domain.repository.SmartAccountRepository
import com.multimoney.domain.repository.VirtualCardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InteractionModule {

    // Security
    @Provides
    @Singleton
    fun provideQueryValidationUserExistsUseCase(securityRepository: SecurityRepository): QueryValidateUserExistsUseCase =
        QueryValidateUserExistsUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationUserValidationUseCase(securityRepository: SecurityRepository): MutationUserValidationUseCase =
        MutationUserValidationUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationUpdateUserRegisterUseCase(securityRepository: SecurityRepository): MutationUpdateUserRegisterUseCase =
        MutationUpdateUserRegisterUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryValidationSecurityUseCase(securityRepository: SecurityRepository): QueryValidationSecurityUseCase =
        QueryValidationSecurityUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryDataInformationUseCase(securityRepository: SecurityRepository): QueryDataInformationClientUseCase =
        QueryDataInformationClientUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryValidateUserStatusUseCase(securityRepository: SecurityRepository): QueryValidateUserStatusUseCase =
        QueryValidateUserStatusUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryGetQuickActions(securityRepository: SecurityRepository): QueryGetQuickActionsUseCase =
        QueryGetQuickActionsImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationSendPinProcessUseCase(securityRepository: SecurityRepository): MutationSendPinProcessUseCase =
        MutationSendPinProcessUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationOnFidoInitialProcess(securityRepository: SecurityRepository): MutationOnFidoInitialProcessUseCase =
        MutationOnFidoInitialProcessUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryValidatePin(securityRepository: SecurityRepository): QueryValidatePinUseCase =
        QueryValidatePinUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryCatalogDocumentTypeUseCase(securityRepository: SecurityRepository): QueryCatalogDocumentTypeUseCase =
        QueryCatalogDocumentTypeUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryGetCountry(securityRepository: SecurityRepository): QueryGetCountryUseCase =
        QueryGetCountryUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryGetConfigurationVersion(securityRepository: SecurityRepository): QueryGetConfigurationVersionUseCase =
        QueryGetConfigurationVersionUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryCompanyNameByIdentityUseCase(securityRepository: SecurityRepository): QueryCompanyNameByIdentityUseCase =
        QueryCompanyNameByIdentityUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryValidateBankAccountUseCase(securityRepository: SecurityRepository): QueryValidateBankAccountUseCase =
        QueryValidateBankAccountUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutableOnfidoCheckProcess(securityRepository: SecurityRepository): MutationOnfidoCheckProcessUseCase =
        MutationOnfidoCheckProcessUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationUserPhoneMobileUseCase(securityRepository: SecurityRepository): MutationUserPhoneMobileSaveUseCase =
        MutationUserPhoneMobileSaveUseCaseImpl(securityRepository)

    // Balance

    @Provides
    @Singleton
    fun provideQueryBalanceUseCase(balanceRepository: BalanceRepository): QueryBalanceUseCase =
        QueryBalanceUseCaseImpl(balanceRepository)

    @Provides
    @Singleton
    fun provideQueryBalanceCardInformationUseCase(balanceRepository: BalanceRepository): QueryBalanceCardInformationUseCase =
        QueryBalanceCardInformationUseCaseImpl(balanceRepository)

    // Credit

    @Provides
    @Singleton
    fun provideQueryCreditOfferUseCase(creditRepository: CreditRepository): QueryCreditOfferUseCase =
        QueryCreditOfferUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryPaymentAmountUseCase(creditRepository: CreditRepository): QueryPaymentAmountUseCase =
        QueryPaymentAmountUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationSaveCreditApplicationUseCase(creditRepository: CreditRepository): MutationSaveCreditApplicationUseCase =
        MutationSaveCreditApplicationUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryScreenConfigUseCase(creditRepository: CreditRepository): QueryScreenConfigUseCase =
        QueryScreenConfigUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryCompanyCantonUseCase(creditRepository: CreditRepository): QueryCompanyCantonUseCase =
        QueryCompanyCantonUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryCompanyDistrictUseCase(creditRepository: CreditRepository): QueryCompanyDistrictUseCase =
        QueryCompanyDistrictUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryCompanyProvinceUseCase(creditRepository: CreditRepository): QueryCompanyProvinceUseCase =
        QueryCompanyProvinceUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationSaveCreditFlowStepUseCase(creditRepository: CreditRepository): MutationSaveCreditFlowStepUseCase =
        MutationSaveCreditFlowStepUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationSaveCreditOfferUseCase(creditRepository: CreditRepository): MutationSaveCreditOfferUseCase =
        MutationSaveCreditOfferUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationSaveTermsAndConditionsCreditUseCase(creditRepository: CreditRepository): MutationSaveTermsAndConditionsCreditUseCase =
        MutationSaveTermsAndConditionsCreditUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryHomeCantonUseCase(creditRepository: CreditRepository): QueryHomeCantonUseCase =
        QueryHomeCantonUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryHomeDistrictUseCase(creditRepository: CreditRepository): QueryHomeDistrictUseCase =
        QueryHomeDistrictUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryHomeProvinceUseCase(creditRepository: CreditRepository): QueryHomeProvinceUseCase =
        QueryHomeProvinceUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryProfessionsUseCase(creditRepository: CreditRepository): QueryProfessionsUseCase =
        QueryProfessionsUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryOccupationUseCase(creditRepository: CreditRepository): QueryOccupationUseCase =
        QueryOccupationUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryEmploymentSituationUseCase(creditRepository: CreditRepository): QueryEmploymentSituationUseCase =
        QueryEmploymentSituationUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryGetLinkCreditContractUseCase(creditRepository: CreditRepository): QueryGetLinkCreditContractUseCase =
        QueryGetLinkCreditContractUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideTermsAndConditionUseCase(creditRepository: CreditRepository): TermsAndConditionsUseCase =
        TermsAndConditionsUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryGetClientBankAccountUseCase(creditRepository: CreditRepository): QueryGetClientBankAccountUseCase =
        QueryGetClientBankAccountUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryBanksAndRegularExpressionUseCase(creditRepository: CreditRepository): QueryBanksAndRegularExpressionUseCase =
        QueryBanksAndRegularExpressionUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryGetPaymentPointsUseCase(creditRepository: CreditRepository): QueryGetPaymentPointsUseCase =
        QueryGetPaymentPointsUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryGetExchangeRateUseCase(creditRepository: CreditRepository): QueryGetExchangeRateCreditUseCase =
        QueryGetExchangeRateCreditUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationProcessPaymentListUseCase(creditRepository: CreditRepository): MutationProcessPaymentListUseCase =
        MutationProcessPaymentListUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideSubscriptionCreditContractEvent(creditRepository: CreditRepository): SubscriptionCreditContractEventUseCase =
        SubscriptionCreditContractEventUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutableSaveCreditOperation(creditRepository: CreditRepository): MutationSaveCreditOperationUseCase =
        MutationSaveCreditOperationUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationActivateClientAutomaticDebitUseCase(creditRepository: CreditRepository): MutationActivateClientAutomaticDebitUseCase =
        MutationActivateClientAutomaticDebitUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationDeactivateClientAutomaticDebitUseCase(creditRepository: CreditRepository): MutationDeactivateClientAutomaticDebitUseCase =
        MutationDeactivateClientAutomaticDebitUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationDeactivateCardAutomaticDebitUseCase(creditRepository: CreditRepository): MutationDeactivateCardAutomaticDebitUseCase =
        MutationDeactivateCardAutomaticDebitUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideGetClientAutomaticDebitUseCase(creditRepository: CreditRepository): QueryGetClientAutomaticDebitUseCase =
        QueryGetClientAutomaticDebitUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideGetCardAutomaticDebitUseCase(creditRepository: CreditRepository): QueryGetCardAutomaticDebitUseCase =
        QueryGetCardAutomaticDebitUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryCreditExtensionAmountUseCase(creditRepository: CreditRepository): QueryCreditExtensionAmountUseCase =
        QueryCreditExtensionAmountUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryCreditExtensionMessageUseCase(creditRepository: CreditRepository): QueryCreditExtensionMessageUseCase =
        QueryCreditExtensionMessageUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationSaveCreditExtensionDetailUseCase(creditRepository: CreditRepository): MutationSaveCreditExtensionDetailUseCase =
        MutationSaveCreditExtensionDetailUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationSaveClientBankAccountUseCase(creditRepository: CreditRepository): MutationSaveClientBankAccountUseCase =
        MutationSaveClientBankAccountUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationProcessCreditExtensionDetailUseCase(creditRepository: CreditRepository): MutationProcessCreditExtensionDetailUseCase =
        MutationProcessCreditExtensionDetailUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideMutationCreditContractEventUseCase(creditRepository: CreditRepository): MutationSendCreditContractEventUseCase =
        MutationSendCreditContractEventUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryGetInfoDepositUseCase(creditRepository: CreditRepository): QueryGetInfoDepositUseCase =
        QueryGetInfoDepositUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryGetPromissoryNoteDetail(creditRepository: CreditRepository): QueryGetPromissoryNoteDetail =
        QueryGetPromissoryNoteDetailImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryGetPagedCreditMovementsUseCase(creditRepository: CreditRepository): QueryGetPagedCreditMovementsUseCase =
        QueryGetPagedCreditMovementsUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryAccountStatementUseCase(creditRepository: CreditRepository): QueryAccountStatementUseCase =
        QueryAccountStatementUseCaseImpl(creditRepository)

    // Smart

    @Provides
    @Singleton
    fun provideQueryGetCoreBankMovementsUseCase(smartAccountRepository: SmartAccountRepository): QueryGetCoreBankMovementsUseCase =
        QueryGetCoreBankMovementsUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryGetPagedSmartMovementsUseCase(smartAccountRepository: SmartAccountRepository): QueryGetPagedSmartMovementsUseCase =
        QueryGetPagedSmartMovementsUseCaseUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryCivilStatusUseCase(smartAccountRepository: SmartAccountRepository): QueryCivilStatusUseCase =
        QueryCivilStatusUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryProfessionUseCase(smartAccountRepository: SmartAccountRepository): QueryProfessionUseCase =
        QueryProfessionUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryNationalitiesUseCase(smartAccountRepository: SmartAccountRepository): QueryNationalitiesUseCase =
        QueryNationalitiesUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryStepByStepUseCase(smartAccountRepository: SmartAccountRepository): QueryStepByStepUseCase =
        QueryStepByStepUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideMutationGlobalRequestUseCase(smartAccountRepository: SmartAccountRepository): MutationGlobalRequestUseCase =
        MutationGlobalRequestUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideMutationInitialRequestUseCase(smartAccountRepository: SmartAccountRepository): MutationInitialRequestUseCase =
        MutationInitialUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideMutationSaveAutomatedSmartAccountUseCase(smartAccountRepository: SmartAccountRepository): MutationSaveAutomatedSmartAccountUseCase =
        MutationSaveAutomatedSmartAccountUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideMutationProcessTransferVisaToSmartVDUseCase(smartAccountRepository: SmartAccountRepository): MutationProcessTransferVisaToSmartVDUseCase =
        MutationProcessTransferVisaToSmartVDUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideMutationProcessSinpeTransferUseCase(smartAccountRepository: SmartAccountRepository): MutationProcessSinpeTransferUseCase =
        MutationProcessSinpeTransferUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryAddressLevelOneUseCase(smartAccountRepository: SmartAccountRepository): QueryAddressLevelOneUseCase =
        QueryAddressLevelOneUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryAddressLevelTwoUseCase(smartAccountRepository: SmartAccountRepository): QueryAddressLevelTwoUseCase =
        QueryAddressLevelTwoUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryAddressLevelThreeUseCase(smartAccountRepository: SmartAccountRepository): QueryAddressLevelThreeUseCase =
        QueryAddressLevelThreeUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryGeneralEconomicActivityUseCase(smartAccountRepository: SmartAccountRepository): QueryGeneralEconomicActivityUseCase =
        QueryGeneralEconomicActivityUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryRelationshipUseCaseImpl(smartAccountRepository: SmartAccountRepository): QueryRelationshipUseCase =
        QueryRelationshipUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideSubscriptionAccountSmartContractUseCaseImpl(smartAccountRepository: SmartAccountRepository): SubscriptionAccountSmartContractUseCase =
        SubscriptionAccountSmartContractUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideMutationValidateOTPUseCase(securityRepository: SecurityRepository): MutationValidateOTPUseCase =
        MutationValidateOTPUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationChangePhoneUseCase(securityRepository: SecurityRepository): MutationChangePhoneUseCase =
        MutationChangePhoneUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationChangeEmailUseCase(securityRepository: SecurityRepository): MutationChangeEmailUseCase =
        MutationChangeEmailUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMiniCardsUseCase(securityRepository: SecurityRepository): QueryMiniCardsUseCase =
        QueryMiniCardsUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationSaveSinpeAccountUseCase(smartAccountRepository: SmartAccountRepository): MutationSaveSinpeAccountUseCase =
        MutationSaveSinpeAccountUseCaseImpl(smartAccountRepository)

    // Multimoney Visa
    @Provides
    @Singleton
    fun provideQueryCardIssuanceUseCase(multimoneyVisaRepository: MultimoneyVisaRepository): QueryCardIssuanceNVUseCase =
        QueryCardIssuanceNVUseCaseImpl(multimoneyVisaRepository)

    @Provides
    @Singleton
    fun provideMutationDeleteTokenDeviceUseCase(multimoneyVisaRepository: MultimoneyVisaRepository): MutationDeleteTokenDeviceNVUseCase =
        MutationDeleteTokenDeviceNVUseCaseImpl(multimoneyVisaRepository)

    @Provides
    @Singleton
    fun provideQueryListSinpeAccountUseCase(smartAccountRepository: SmartAccountRepository): QueryListSinpeAccountUseCase =
        QueryListSinpeAccountUseCaseImpl(smartAccountRepository)

    // Crypto

    @Provides
    @Singleton
    fun provideQueryGetHistoricalClientBalance(cryptoRepository: CryptoRepository): GetHistoricalClientBalanceUseCase =
        GetHistoricalClientBalanceUseCaseImpl(cryptoRepository)

    @Provides
    @Singleton
    fun provideQueryGetAvailableListOfCryptoCoins(cryptoRepository: CryptoRepository): GetAvailableListOfCryptoCoinsUseCase =
        GetAvailableListOfCryptoCoinsUseCaseImpl(cryptoRepository)

    @Provides
    @Singleton
    fun provideQueryGetCryptoMovements(cryptoRepository: CryptoRepository): GetCryptoCurrencyMovementsUseCase =
        GetCryptoCurrencyMovementsUseCaseImpl(cryptoRepository)

    @Singleton
    @Provides
    fun provideQueryGetCurrencyNews(cryptoRepository: CryptoRepository): GetCurrencyNewsUseCase =
        GetCurrencyNewsUseCaseImpl(cryptoRepository)

    @Singleton
    @Provides
    fun provideQueryGetCurrencyHistoricalPrices(cryptoRepository: CryptoRepository): GetCurrencyHistoricalPricesUseCase =
        GetCurrencyHistoricalPricesUseCaseImpl(cryptoRepository)

    // Virtual Card

    @Provides
    @Singleton
    fun provideQueryListCardVDUseCase(virtualCardRepository: VirtualCardRepository): QueryListCardVDUseCase =
        QueryListCardVDUseCaseImpl(virtualCardRepository)

    @Provides
    @Singleton
    fun provideMutationPayCreditVDUseCase(virtualCardRepository: VirtualCardRepository): MutationPayCreditVDUseCase =
        MutationPayCreditVDUseCaseImpl(virtualCardRepository)

    @Provides
    @Singleton
    fun provideMutationUpdateCardVDUseCase(virtualCardRepository: VirtualCardRepository): MutationUpdateCardVDUseCase =
        MutationUpdateCardVDUseCaseImpl(virtualCardRepository)

    @Provides
    @Singleton
    fun provideMutationDeleteCardVDUseCase(virtualCardRepository: VirtualCardRepository): MutationDeleteCardVDUseCase =
        MutationDeleteCardVDUseCaseImpl(virtualCardRepository)

    @Provides
    @Singleton
    fun provideMutationActivatedCardAutomaticDebitUseCase(virtualCardRepository: VirtualCardRepository): MutationActivatedCardAutomaticDebitUseCase =
        MutationActivatedCardAutomaticDebitUseCaseImpl(virtualCardRepository)

    @Provides
    @Singleton
    fun provideMutationCardBlockingUseCase(virtualCardRepository: VirtualCardRepository): MutationCardBlockingUseCase =
        MutationCardBlockingUseCaseImpl(virtualCardRepository)

    @Provides
    @Singleton
    fun provideMutationCardUnblockingUseCase(virtualCardRepository: VirtualCardRepository): MutationCardUnblockingUseCase =
        MutationCardUnblockingUseCaseImpl(virtualCardRepository)

    // Profile

    @Provides
    @Singleton
    fun provideQueryCountryContactUseCase(profileRepository: ProfileRepository): QueryCountryContactUseCase =
        QueryCountryContactUseCaseImpl(profileRepository)

    @Provides
    @Singleton
    fun provideQuerySmartExchangeRateUseCase(smartAccountRepository: SmartAccountRepository): QuerySmartExchangeRateUseCase =
        QuerySmartExchangeRateUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQueryTermsAndConditionsSigned(profileRepository: ProfileRepository): QueryTermsAndConditionsSignedUseCase =
        QueryTermsAndConditionsSignedUseCaseImpl(profileRepository)

    @Provides
    @Singleton
    fun provideMutationRequestChangeDevice(securityRepository: SecurityRepository): MutationRequestChangeDeviceUseCase =
        MutationRequestChangeDeviceUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideMutationChangeDevice(securityRepository: SecurityRepository): MutationChangeDeviceUseCase =
        MutationChangeDeviceUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryRelatedContactsByPhoneUseCase(smartAccountRepository: SmartAccountRepository): QueryRelatedContactsByPhoneUseCase =
        QueryRelatedContactsByPhoneUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideQuerySmartAccountType(smartAccountRepository: SmartAccountRepository): QuerySmartAccountTypeUseCase =
        QuerySmartAccountTypeUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideBankListTransfer365(smartAccountRepository: SmartAccountRepository): QueryBankListTransfer365UseCase =
        QueryBankListTransfer365UseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideMutationAddACHAccount(smartAccountRepository: SmartAccountRepository): MutationAddACHAccountUseCase =
        MutationAddACHAccountUseCaseImpl(smartAccountRepository)

    @Provides
    @Singleton
    fun provideMutationUpdateFavoriteSmart(smartAccountRepository: SmartAccountRepository): MutationUpdateFavoriteSmartUseCase =
        MutationUpdateFavoriteSmartUseCaseImpl(smartAccountRepository)
}