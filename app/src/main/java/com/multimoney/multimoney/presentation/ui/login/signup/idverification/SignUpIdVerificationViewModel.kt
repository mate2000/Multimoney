package com.multimoney.multimoney.presentation.ui.login.signup.idverification

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpIdVerificationViewModel @Inject constructor(
    val onFidoHelper: OnFidoHelper,
    private val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase
) : BaseViewModel() {

    var onFidoError: String = ""

    // Events
    val onFidoTokenEvent = MutableSharedFlow<MultimoneyResult<OnfidoToken?>>()

    private fun callMutationOnFidoInitialProcess(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        user: String
    ) {
        viewModelScope.launch {
            mutationOnFidoInitialProcessUseCase.invoke(
                names,
                lastNames,
                identification,
                applicationId,
                Brand.Revamp.id,
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
        user: String,
        injectNewToken: (String?) -> Unit
    ) {
        viewModelScope.launch {
            mutationOnFidoInitialProcessUseCase.invoke(
                names, lastNames, identification, applicationId, Brand.Revamp.id, user
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

    private fun onOpenOnFidoSDK(
        result: ActivityResult,
        onOnFidoCompleted: () -> Unit,
        onOnFidoError: (DialogParameters) -> Unit,
        onContinueValueChanged: (value: Boolean) -> Unit
    ) {
        onFidoHelper.getOnFidoClient().handleActivityResult(
            result.resultCode,
            result.data,
            object : OnfidoResultListener {
                override fun userCompleted(captures: Captures) {
                    onOnFidoCompleted()
                }

                override fun userExited(exitCode: ExitCode) {
                    // Empty on purpose
                }

                override fun onError(exception: OnfidoException) {
                    onContinueValueChanged(false)
                    onOnFidoError(
                        DialogParameters(
                            description = onFidoError,
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            })
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnCallInFidoToken -> callMutationOnFidoInitialProcess(
                event.userData?.firstName ?: "",
                event.userData?.lastName ?: "",
                event.userData?.identification ?: "",
                event.applicationId,
                event.userData?.email ?: "",
            )
            is OnInitValues -> onInitValues(onFidoError)
            is OnOpenOnFidoSdk -> onOpenOnFidoSDK(
                event.result,
                event.onOnFidoCompleted,
                event.onOnFidoError,
                event.onContinueEnable
            )
            is RefreshOnFidoToken -> onRefreshToken(
                event.userData?.firstName ?: "",
                event.userData?.lastName ?: "",
                event.userData?.identification ?: "",
                event.applicationId,
                event.userData?.email ?: "",
                event.injectNewToken
            )
        }
    }

    sealed class UIEvent {
        data class OnCallInFidoToken(
            val userData: UserData?,
            val applicationId: String
        ) : UIEvent()

        data class OnInitValues(val isFirstLaunch: Boolean, val onFidoError: String) : UIEvent()
        data class OnOpenOnFidoSdk(
            val result: ActivityResult,
            val onOnFidoCompleted: () -> Unit,
            val onOnFidoError: (dialogParameters: DialogParameters) -> Unit,
            val onContinueEnable: (value: Boolean) -> Unit
        ) : UIEvent()

        data class RefreshOnFidoToken(
            val userData: UserData?,
            val applicationId: String,
            val injectNewToken: (String?) -> Unit
        ) : UIEvent()
    }
}