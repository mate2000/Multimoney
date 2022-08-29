package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationUserValidationUseCase {
    suspend operator fun invoke(
        email: String,
        currentStep: String,
        idBrand: Int,
        idDocument:Int,
        identification:String,
        firstName:String,
        secondName:String,
        firstSurname:String,
        secondSurname:String
    ): Flow<MultimoneyResult<UserData?>>
}