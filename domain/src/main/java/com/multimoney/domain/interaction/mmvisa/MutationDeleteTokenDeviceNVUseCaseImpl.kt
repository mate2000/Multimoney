package com.multimoney.domain.interaction.mmvisa

import com.multimoney.domain.model.mmvisa.CardIssuanceNV
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.MultimoneyVisaRepository
import kotlinx.coroutines.flow.Flow

class MutationDeleteTokenDeviceNVUseCaseImpl(val repository: MultimoneyVisaRepository) :
    MutationDeleteTokenDeviceNVUseCase {
    override suspend fun invoke(
        identification: String,
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        idDevice: String
    ): Flow<MultimoneyResult<CardIssuanceNV?>> = repository.mutationDeleteTokenDeviceNVMutation(
        identification,
        user,
        idBrand,
        idClient,
        idLoanClient,
        idDevice
    )
}
