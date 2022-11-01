package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationGlobalRequestUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        status: Int,
        idProfessionType: Int,
        idCivilStatusType: Long,
        birthday: String,
        expirationDate: String,
        idGender: Long,
        companyName: String,
        aboutCompany: String,
        idAddressLevel1: Long,
        idAddressLevel2: Long,
        idAddressLevel3: Long,
        idEconomicActivity: Long,
        income: Int,
        addressDetail: String,
        isPEP: Boolean,
        user: String,
        idBrand: Int,
        currentStep: String,
        institutionPension: String,
        specifiesIncomeSource: String
    ): Flow<MultimoneyResult<GlobalRequest?>>
}
