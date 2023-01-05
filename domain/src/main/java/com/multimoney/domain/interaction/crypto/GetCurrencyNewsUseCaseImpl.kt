package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.repository.CryptoRepository

class GetCurrencyNewsUseCaseImpl(val repository: CryptoRepository) : GetCurrencyNewsUseCase {

    override suspend fun invoke(
        baseAsset: String,
        user: String,
        idBrand: Int
    ) = repository.getCurrencyNews(baseAsset, user, idBrand)
}