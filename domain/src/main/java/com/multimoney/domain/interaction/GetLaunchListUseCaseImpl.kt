package com.multimoney.domain.interaction

import com.multimoney.domain.repository.LaunchRepository
import javax.inject.Inject

class GetLaunchListUseCaseImpl @Inject constructor(private val launchRepository: LaunchRepository) :
    GetLaunchListUseCase {
    override suspend fun invoke() = launchRepository.getLaunchList()
}
