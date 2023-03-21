package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.UserEventMobileSave
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationUserEventMobileSaveUseCaseImpl(
    val securityRepository: SecurityRepository
) : MutationUserEventMobileSaveUseCase {
    override suspend fun invoke(
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
    ): Flow<MultimoneyResult<UserEventMobileSave>> = securityRepository.mutationUserEventMobileSave(
        idBrand,
        user,
        pkSuvLogUserEventMobile,
        fkSuvMtrUser,
        platform,
        uuid,
        deviceVersion,
        manufacture,
        deviceName,
        seriesNumber,
        ipAddress,
        latitude,
        longitude,
        tokenNotificationsPush
    )
}
