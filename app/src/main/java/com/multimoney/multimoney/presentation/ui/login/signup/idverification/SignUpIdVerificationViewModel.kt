package com.multimoney.multimoney.presentation.ui.login.signup.idverification

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.BaseEvent.OnOnFidoCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.BaseEvent.OnOnFidoError
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnInitValues
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.OnOpenOnFidoSdk
import com.multimoney.multimoney.presentation.ui.login.signup.idverification.SignUpIdVerificationViewModel.UIEvent.RefreshOnFidoToken
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.onfido.OnFidoHelper
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido.OnfidoResultListener
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.upload.Captures
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpIdVerificationViewModel @Inject constructor(
    val onFidoHelper: OnFidoHelper,
    val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase
) : BaseViewModel() {

    var onFidoError: String = ""

    // Events
    val onFidoTokenEvent = MutableSharedFlow<MultimoneyResult<OnfidoToken?>>()

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
            }
        }
    }

    private fun onRefreshToken(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String,
        injectNewToken: (String?) -> Unit
    ) {
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

    private fun onInitValues(onFidoError: String) {
        this.onFidoError = onFidoError
    }

    private fun onOpenOnFidoSDK(result: ActivityResult) {
        onFidoHelper.getOnFidoClient().handleActivityResult(
            result.resultCode,
            result.data,
            object : OnfidoResultListener {
                override fun userCompleted(captures: Captures) {
                    emitBaseEvent(OnOnFidoCompleted(true))
                }

                override fun userExited(exitCode: ExitCode) {
                    // Empty on purpose
                }

                override fun onError(exception: OnfidoException) {
                    emitBaseEvent(
                        OnOnFidoError(
                            DialogParameters(
                                description = onFidoError,
                                isActive = mutableStateOf(true)
                            )
                        )
                    )
                }
            })
    }

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
            is OnInitValues -> onInitValues(onFidoError)
            is OnOpenOnFidoSdk -> onOpenOnFidoSDK(event.result)
            is RefreshOnFidoToken -> onRefreshToken(
                event.names,
                event.lastNames,
                event.identification,
                event.applicationId,
                event.idBrand,
                event.user,
                event.injectNewToken
            )
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
        data class RefreshOnFidoToken(
            val names: String,
            val lastNames: String,
            val identification: String,
            val applicationId: String,
            val idBrand: Int,
            val user: String,
            val injectNewToken: (String?) -> Unit
        ) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnOnFidoCompleted(val isCompleted: Boolean) : BaseEvent()
        data class OnOnFidoError(val error: DialogParameters) : BaseEvent()
    }
}