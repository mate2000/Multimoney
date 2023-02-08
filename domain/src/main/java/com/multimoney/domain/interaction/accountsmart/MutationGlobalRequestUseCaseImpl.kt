package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationGlobalRequestUseCaseImpl(val repository: SmartAccountRepository) :
    MutationGlobalRequestUseCase {
    override suspend fun invoke(
        pkUser: Int,
        status: Int,
        user: String,
        idBrand: Int,
        idGlobalRequest: Long,
        idProfessionType: Int?,
        idCivilStatusType: Long?,
        birthday: String?,
        expirationDate: String?,
        idGender: Long?,
        companyName: String?,
        aboutCompany: String?,
        idAddressLevel1: Long?,
        idAddressLevel2: Long?,
        idAddressLevel3: Long?,
        positionJob: String?,
        idEconomicActivity: Long?,
        income: Double?,
        addressDetail: String?,
        fullJobAddress: String?,
        currentStep: String?,
        institutionPension: String?,
        specifiesIncomeSource: String?,
        beneficiaries: List<Beneficiary>?,
        entrepreneurship: String?,
        legalID: String?,
        isActivityOfArt15: Boolean?,
        isUSCitizen: Boolean?,
        isPEP: Boolean?,
        isUSTaxPayer: Boolean?,
        isTaxPayer: Boolean?,
        idJobLevel1: Long?,
        idJobLevel2: Long?,
        idJobLevel3: Long?
    ): Flow<MultimoneyResult<GlobalRequest?>> = repository.mutationGlobalRequest(
        pkUser,
        status,
        user,
        idBrand,
        idGlobalRequest,
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
        fullJobAddress,
        currentStep,
        institutionPension,
        specifiesIncomeSource,
        beneficiaries,
        entrepreneurship,
        legalID,
        isActivityOfArt15,
        isUSCitizen,
        isPEP,
        isUSTaxPayer,
        isTaxPayer,
        idJobLevel1,
        idJobLevel2,
        idJobLevel3
    )
}
