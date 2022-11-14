package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationGlobalRequestUseCaseImpl(val repository: SmartAccountRepository) :
    MutationGlobalRequestUseCase {
    override suspend fun invoke(
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
        positionJob: String,
        idEconomicActivity: Long,
        income: Double,
        addressDetail: String,
        user: String,
        idBrand: Int,
        currentStep: String,
        institutionPension: String,
        specifiesIncomeSource: String,
        entrepreneurship: String,
        legalID: String,
        isActivityOfArt15: Boolean,
        isUSCitizen: Boolean,
        isPEP: Boolean,
        isUSTaxPayer: Boolean,
        isTaxPayer: Boolean
    ): Flow<MultimoneyResult<GlobalRequest?>> = repository.mutationGlobalRequest(
        pkUser,
        status,
        idProfessionType,
        idCivilStatusType,
        birthday,
        expirationDate,
        idGender,
        companyName,
        aboutCompany,
        idAddressLevel1,
        idAddressLevel2,
        idAddressLevel3,
        positionJob,
        idEconomicActivity,
        income,
        addressDetail,
        user,
        idBrand,
        currentStep,
        institutionPension,
        specifiesIncomeSource,
        entrepreneurship,
        legalID,
        isActivityOfArt15,
        isUSCitizen,
        isPEP,
        isUSTaxPayer,
        isTaxPayer
    )
}
