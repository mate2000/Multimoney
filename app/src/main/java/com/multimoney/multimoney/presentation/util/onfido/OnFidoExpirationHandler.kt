package com.multimoney.multimoney.presentation.util.onfido

import com.onfido.android.sdk.capture.token.TokenExpirationHandler

class OnFidoExpirationHandler(@Transient val onRefresh: RefreshToken) :
    TokenExpirationHandler {

    override fun refreshToken(injectNewToken: (String?) -> Unit) {
        onRefresh.refreshToke(injectNewToken)
    }

    interface RefreshToken {
        fun refreshToke(injectNewToken: (String?) -> Unit)
    }
}