package com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard

import com.multimoney.domain.interaction.virtualcard.MutationUpdateCardVDUseCase
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class EditCardViewModel(
    private val mutationUpdateCardVDUseCase: MutationUpdateCardVDUseCase
) : BaseViewModel(shouldObserveToken = true) {

}