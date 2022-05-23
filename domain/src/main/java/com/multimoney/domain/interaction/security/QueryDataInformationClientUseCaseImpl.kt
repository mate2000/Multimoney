package com.multimoney.domain.interaction.security

import com.multimoney.domain.repository.SecurityRepository

class QueryDataInformationClientUseCaseImpl(val serviceRepository: SecurityRepository) :
    QueryDataInformationClientUseCase {
    override suspend fun invoke(identification: String, idBrand: Int, user: String) =
        serviceRepository.queryDataInformationClient(identification, idBrand, user)
}