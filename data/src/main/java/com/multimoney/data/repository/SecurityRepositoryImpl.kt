package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.security.mapToDomainModel
import com.multimoney.data.networking.SecurityApi
import com.multimoney.domain.model.security.User
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SecurityRepositoryImpl @Inject constructor(
    val securityApi: SecurityApi
) : BaseRepository(),
    SecurityRepository {

    override suspend fun mutationUserValidation(
        email: String,
        currentStep: String,
        idBrand: Int
    ): Flow<MultimoneyResult<User?>> = fetchData(
        apolloCall = securityApi.mutationUserValidation(email, currentStep, idBrand),
        apolloCallMapper = { data ->
            data.userValidation?.mapToDomainModel()
        }
    )

    override suspend fun queryValidationSecurity(
        pkIUser: Int,
        password: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateSecurity?>> = fetchData(
        apolloCall = securityApi.queryValidationSecurity(pkIUser, password, user, idBrand),
        apolloCallMapper = { data ->
            data.validateSecurity?.mapToDomainModel()
        }
    )
}
