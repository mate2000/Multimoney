package com.multimoney.domain.repository

import com.multimoney.domain.model.security.User
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {

    suspend fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int
    ): Flow<MultimoneyResult<User?>>

    suspend fun mutationUpdateUserRegister(
        pkUser: String,
        user: String,
        email: String,
        phoneNumber: String?,
        fullName: String?,
        firstName: String?,
        secondName: String?,
        lastName: String?,
        secondLastName: String?,
        contactMeans: String?,
        nationality: String?,
        identification: String?,
        countryCode: String?,
        currentStep: String,
        idBrand: Int
    ): Flow<MultimoneyResult<User?>>

    suspend fun queryValidationSecurity(
        pkIUser: Int,
        password: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateSecurity?>>

}