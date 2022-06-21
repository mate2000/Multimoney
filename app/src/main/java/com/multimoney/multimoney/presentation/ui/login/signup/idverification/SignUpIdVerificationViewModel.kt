package com.multimoney.multimoney.presentation.ui.login.signup.idverification

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.BaseEvent.OnOnFidoCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnInitValues
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnOpenOnFidoSdk
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.onfido.OnFidoHelper
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido.OnfidoResultListener
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.token.TokenExpirationHandler
import com.onfido.android.sdk.capture.upload.Captures
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpIdVerificationViewModel @Inject constructor(
    val onFidoHelper: OnFidoHelper,
    val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase
) : BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // StateLess
    var isFirstLaunch = true
    var onFidoError: String = ""

    // Events
    val onFidoTokenEvent = MutableSharedFlow<MultimoneyResult<OnfidoToken?>>()
    val onCompletedOnfidoEvent = MutableSharedFlow<Any>()

    private fun callMutationOnFidoInitialProcess(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String
    ) {
        viewModelScope.launch {
            mutationOnFidoInitialProcessUseCase.invoke(
                names,
                lastNames,
                identification,
                applicationId,
                idBrand,
                user
            ).collectLatest { result ->
                onFidoTokenEvent.emit(result)
//                result.onSuccess {
//                    uiState = uiState.copy(
//                        isLoading = false,
//                        onFidoTokenSuccess = Pair(true, it)
//                    )
//                }
//                result.onLoading {
//                    uiState = uiState.copy(isLoading = true)
//                }
//                result.onFailure {
//                    uiState = uiState.copy(
//                        isLoading = false,
//                        onFidoTokenFailure = DialogParameters(
//                            description = it.getError() ?: "",
//                            isActive = mutableStateOf(true)
//                        )
//                    )
//                }
            }
        }
    }

    fun onRefreshToken(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String
    ) = object : TokenExpirationHandler {
        override fun refreshToken(injectNewToken: (String?) -> Unit) {
            viewModelScope.launch {
                mutationOnFidoInitialProcessUseCase.invoke(
                    names, lastNames, identification, applicationId, idBrand, user
                ).collectLatest { result ->
                    result.onSuccess {
                        injectNewToken(it?.sdkToken ?: "")
                    }
                    result.onFailure {
                        // Close the sdk
                    }
                }
            }
        }
    }

    private fun onInitValues(isFirstLaunch: Boolean, onFidoError: String) {
        this.isFirstLaunch = isFirstLaunch
        this.onFidoError = onFidoError
    }

    private fun onOpenOnFidoSDK(result: ActivityResult) {
        onFidoHelper.getOnFidoClient().handleActivityResult(
            result.resultCode,
            result.data,
            object : OnfidoResultListener {
                override fun userCompleted(captures: Captures) {
//                    uiState = uiState.copy(hasOnFidoCompleted = true)
                    emitBaseEvent(OnOnFidoCompleted(true))
                }

                override fun userExited(exitCode: ExitCode) {
                    // Empty on purpose
                }

                override fun onError(exception: OnfidoException) {
                    uiState = uiState.copy(
                        onFidoTokenFailure = DialogParameters(
                            description = onFidoError,
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            })
    }

    data class UIState(
        val isLoading: Boolean = false,
        val onFidoTokenFailure: DialogParameters = DialogParameters(),
        val onFidoTokenSuccess: Pair<Boolean, OnfidoToken?> = Pair(false, null),
        val hasOnFidoCompleted: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnCallInFidoToken -> callMutationOnFidoInitialProcess(
                event.names,
                event.lastNames,
                event.identification,
                event.applicationId,
                event.idBrand,
                event.user
            )
            is OnInitValues -> onInitValues(event.isFirstLaunch, onFidoError)
            is OnOpenOnFidoSdk -> onOpenOnFidoSDK(event.result)
        }
    }

    sealed class UIEvent {
        data class OnCallInFidoToken(
            val names: String,
            val lastNames: String,
            val identification: String,
            val applicationId: String,
            val idBrand: Int,
            val user: String
        ) : UIEvent()

        data class OnInitValues(val isFirstLaunch: Boolean, val onFidoError: String) : UIEvent()
        data class OnOpenOnFidoSdk(val result: ActivityResult) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnOnFidoCompleted(val isCompleted: Boolean) : BaseEvent()
    }
}