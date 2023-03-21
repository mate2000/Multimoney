package com.multimoney.multimoney.di

import com.multimoney.domain.interaction.security.QueryValidatePasswordStructure
import com.multimoney.multimoney.presentation.util.password.PasswordValidationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HelperModule {

    @Provides
    fun providePasswordValidationHelper(queryValidatePasswordStructure: QueryValidatePasswordStructure) =
        PasswordValidationHelper(queryValidatePasswordStructure)
}