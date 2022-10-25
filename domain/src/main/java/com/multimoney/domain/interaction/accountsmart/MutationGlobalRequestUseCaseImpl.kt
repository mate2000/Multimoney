package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationGlobalRequestUseCaseImpl(val repository: SmartAccountRepository) : MutationGlobalRequestUseCase {
    override suspend fun invoke(
        pkUser: Int,
        status: Int,
        idProfessionType: Int,
        idAddressLevel1: Long,
        idAddressLevel2: Long,
        idAddressLevel3: Long,
        idEconomicActivity: Long,
        income: Float,
        addressDetail: String,
        isPEP: Boolean,
        user: String,
        idBrand: Int,
        currentStep: String,
    ): Flow<MultimoneyResult<GlobalRequest?>> = repository.mutationGlobalRequest(pkUser,
        status,
        idProfessionType,
        idAddressLevel1,
        idAddressLevel2,
        idAddressLevel3,
        idEconomicActivity,
        income,
        addressDetail,
        isPEP,
        user,
        idBrand,
        currentStep)
}