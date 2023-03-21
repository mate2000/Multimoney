package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.UserEventMobileSave
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationUserEventMobileSaveUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        user: String,
        pkSuvLogUserEventMobile: Long,
        fkSuvMtrUser: Int,
        platform: String,
        uuid: String,
        deviceVersion: String,
        manufacture: String,
        deviceName: String,
        seriesNumber: String,
        ipAddress: String,
        latitude: String,
        longitude: String,
        tokenNotificationsPush: String
    ): Flow<MultimoneyResult<UserEventMobileSave>>
}
