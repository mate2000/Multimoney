package com.multimoney.domain.di

import com.multimoney.domain.interaction.accountsmart.MutationGlobalRequestUseCase
import com.multimoney.domain.interaction.accountsmart.MutationGlobalRequestUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryCivilStatusUseCase
import com.multimoney.domain.interaction.accountsmart.QueryCivilStatusUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryNationalitiesUseCase
import com.multimoney.domain.interaction.accountsmart.QueryNationalitiesUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryProfessionUseCase
import com.multimoney.domain.interaction.accountsmart.QueryProfessionUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryStepByStepUseCase
import com.multimoney.domain.interaction.accountsmart.QueryStepByStepUseCaseImpl
import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveCreditApplicationUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditApplicationUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveCreditFlowStepUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditFlowStepUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCase
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCompanyCantonUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyCantonUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCompanyDistrictUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyDistrictUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCompanyProvinceUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyProvinceUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCase
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryHomeCantonUseCase
import com.multimoney.domain.interaction.credit.QueryHomeCantonUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryHomeDistrictUseCase
import com.multimoney.domain.interaction.credit.QueryHomeDistrictUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryHomeProvinceUseCase
import com.multimoney.domain.interaction.credit.QueryHomeProvinceUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCase
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryScreenConfigUseCase
import com.multimoney.domain.interaction.credit.QueryScreenConfigUseCaseImpl
import com.multimoney.domain.interaction.credit.TermsAndConditionsUseCase
import com.multimoney.domain.interaction.credit.TermsAndConditionsUseCaseImpl
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCaseImpl
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCase
import com.multimoney.domain.interaction.security.MutationSendPinProcessUseCaseImpl
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCase
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCaseImpl
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.interaction.security.MutationUserValidationUseCaseImpl
import com.multimoney.domain.interaction.security.QueryCatalogDocumentTypeUseCase
import com.multimoney.domain.interaction.security.QueryCatalogDocumentTypeUseCaseImpl
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCase
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCaseImpl
import com.multimoney.domain.interaction.security.QueryGetCountryUseCase
import com.multimoney.domain.interaction.security.QueryGetCountryUseCaseImpl
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
import com.multimoney.domain.repository.BalanceRepository
import com.multimoney.domain.repository.CreditRepository
import com.multimoney.domain.repository.SecurityRepository
import com.multimoney.domain.repository.SmartAccountRepository
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

    // Balance

    @Provides
    @Singleton
    fun provideQueryBalanceUseCase(balanceRepository: BalanceRepository): QueryBalanceUseCase =
        QueryBalanceUseCaseImpl(balanceRepository)

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
    fun provideTermsAndConditionUseCase(creditRepository: CreditRepository): TermsAndConditionsUseCase =
        TermsAndConditionsUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryGetClientBankAccountUseCase(creditRepository: CreditRepository): QueryGetClientBankAccountUseCase =
        QueryGetClientBankAccountUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryValidateBankAccountUseCase(securityRepository: SecurityRepository): QueryValidateBankAccountUseCase =
        QueryValidateBankAccountUseCaseImpl(securityRepository)

    @Provides
    @Singleton
    fun provideQueryBanksAndRegularExpressionUseCase(creditRepository: CreditRepository): QueryBanksAndRegularExpressionUseCase =
        QueryBanksAndRegularExpressionUseCaseImpl(creditRepository)

    // Smart

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
    fun provideQueryAddressLevelTwoUseCase(smartAccountRepository: SmartAccountRepository): QueryAddressLevelTwoUseCase =
        QueryAddressLevelTwoUseCaseImpl(smartAccountRepository)

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
}
