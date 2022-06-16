package com.multimoney.multimoney.presentation.ui.login.signup.idverification

import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.multimoney.presentation.base.BaseViewModel
import javax.inject.Inject

class SignUpIdVerificationViewModel @Inject constructor(
    val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase
) : BaseViewModel() {


    sealed class UIEvent {
        object OnCallInFidoToken : UIEvent()
    }
}