package com.multimoney.domain.di

import com.multimoney.domain.interaction.balance.QueryBalanceUseCase
import com.multimoney.domain.interaction.balance.QueryBalanceUseCaseImpl
import com.multimoney.domain.interaction.credit.MutationSaveCreditApplicationUseCase
import com.multimoney.domain.interaction.credit.MutationSaveCreditApplicationUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCompanyAddressSVUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyAddressSVUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCompanyAddressUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyAddressUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCase
import com.multimoney.domain.interaction.credit.QueryCreditOfferUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryHomeAddressSVUseCase
import com.multimoney.domain.interaction.credit.QueryHomeAddressSVUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryHomeAddressUseCase
import com.multimoney.domain.interaction.credit.QueryHomeAddressUseCaseImpl
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCase
import com.multimoney.domain.interaction.credit.QueryPaymentAmountUseCaseImpl
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
    fun provideQueryCompanyAddressUseCase(creditRepository: CreditRepository): QueryCompanyAddressUseCase =
        QueryCompanyAddressUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryCompanyAddressSVUseCase(creditRepository: CreditRepository): QueryCompanyAddressSVUseCase =
        QueryCompanyAddressSVUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryHomeAddressUseCase(creditRepository: CreditRepository): QueryHomeAddressUseCase =
        QueryHomeAddressUseCaseImpl(creditRepository)

    @Provides
    @Singleton
    fun provideQueryHomeAddressSVUseCase(creditRepository: CreditRepository): QueryHomeAddressSVUseCase =
        QueryHomeAddressSVUseCaseImpl(creditRepository)
}
