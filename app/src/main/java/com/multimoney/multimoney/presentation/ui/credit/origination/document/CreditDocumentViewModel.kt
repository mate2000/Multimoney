package com.multimoney.multimoney.presentation.ui.credit.origination.document

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentViewModel.UIEvent.OnOpenOnFidoSdk
import com.multimoney.multimoney.presentation.ui.credit.origination.document.CreditDocumentViewModel.UIEvent.RefreshOnFidoToken
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIState
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
class CreditDocumentViewModel @Inject constructor(
    val onFidoHelper: OnFidoHelper,
    private val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    var onFidoError: String = ""

    // Events
    val onFidoTokenEvent = MutableSharedFlow<MultimoneyResult<OnfidoToken?>>()

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
                names,
                lastNames,
                identification,
                applicationId,
                Brand.CostaRica.id,
                user
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
                Brand.CostaRica.id,
                user
            ).collectLatest { result ->
                onFidoTokenEvent.emit(result)
            }
        }
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
            }
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnOpenOnFidoSdk -> onOpenOnFidoSDK(
                event.result,
                event.onOnFidoCompleted,
                event.onOnFidoError,
                event.onContinueEnable
            )

            is OnCallInFidoToken -> callMutationOnFidoInitialProcess(
                "${event.userData?.firstName} ${event.userData?.secondName}",
                "${event.userData?.firstLastName} ${event.userData?.secondLastName}",
                event.userData?.identification ?: "",
                event.applicationId,
                event.userData?.email ?: ""
            )

            is RefreshOnFidoToken -> onRefreshToken(
                "${event.userData?.firstName} ${event.userData?.secondName}",
                "${event.userData?.firstLastName} ${event.userData?.secondLastName}",
                event.userData?.identification ?: "",
                event.applicationId,
                event.userData?.email ?: "",
                event.injectNewToken
            )
            is OnOpenDialogValueChange -> uiState = uiState.copy(openDialog = event.openDialog)
        }
    }

    sealed class UIEvent {
        data class OnCallInFidoToken(
            val userData: UserData?,
            val applicationId: String
        ) : UIEvent()

        data class OnOpenOnFidoSdk(
            val result: ActivityResult,
            val onOnFidoCompleted: () -> Unit,
            val onOnFidoError: (dialogParameters: DialogParameters) -> Unit,
            val onContinueEnable: (value: Boolean) -> Unit
        ) : UIEvent()

        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class RefreshOnFidoToken(
            val userData: UserData?,
            val applicationId: String,
            val injectNewToken: (String?) -> Unit
        ) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }
}
