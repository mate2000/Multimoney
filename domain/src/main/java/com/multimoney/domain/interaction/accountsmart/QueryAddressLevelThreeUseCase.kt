package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryAddressLevelThreeUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        idAddressLevelOne: String,
        idAddressLevelTwo: String,
    ): Flow<MultimoneyResult<AddressesLevel?>>
}