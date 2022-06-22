package com.multimoney.multimoney.presentation.util.onfido

import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.onfido.android.sdk.capture.token.TokenExpirationHandler

class OnFidoExpirationHandler(val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase) :
    TokenExpirationHandler {

    override fun refreshToken(injectNewToken: (String?) -> Unit) {
        injectNewToken("")
    }
}