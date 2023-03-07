package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.repository.SmartAccountRepository

class QueryLocalTransferFavoriteUseCaseImpl(
    private val smartAccountRepository: SmartAccountRepository
) : QueryLocalTransferFavoriteUseCase {
    override suspend operator fun invoke(
        idBrand: Int,
        user: String,
        isFavorite: Boolean,
        idCustomer: Long
    ) = smartAccountRepository.queryLocalTransferFavorite(
        idBrand,
        user,
        isFavorite,
        idCustomer
    )
}
