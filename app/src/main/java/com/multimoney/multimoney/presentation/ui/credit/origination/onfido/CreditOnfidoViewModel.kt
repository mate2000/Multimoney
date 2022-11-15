package com.multimoney.multimoney.presentation.ui.credit.origination.onfido

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.MutationSaveCreditOperationUseCase
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.multimoney.domain.interaction.security.MutationOnfidoCheckProcessUseCase
import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnCallInFidoToken
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnOpenDialogValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.OnOpenOnFidoSdk
import com.multimoney.multimoney.presentation.ui.credit.origination.onfido.CreditOnfidoViewModel.UIEvent.RefreshOnFidoToken
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
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
class CreditOnfidoViewModel @Inject constructor(
    val onFidoHelper: OnFidoHelper,
    private val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase,
    private val mutationOnfidoCheckProcessUseCase: MutationOnfidoCheckProcessUseCase,
    private val mutationSaveCreditOperationUseCase: MutationSaveCreditOperationUseCase,
    val countDownTimer: MMCountDownTimer
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
        onOnFidoError: (DialogParameters) -> Unit,
        onContinueValueChanged: (value: Boolean) -> Unit,
        onNextStep: () -> Unit,
        pkUser: Long,
        identification: String,
        idBrand: Int,
        idUserRequest: Long,
        user: String
    ) {
        onFidoHelper.getOnFidoClient()
            .handleActivityResult(
                result.resultCode,
                result.data,
                object : OnfidoResultListener {
                    override fun userCompleted(captures: Captures) {
                        countDownTimer.resumeTimer()
                        onCallOnfidoCheckProcess(pkUser, identification, idBrand, idUserRequest, user, onNextStep)
                    }

                    override fun userExited(exitCode: ExitCode) {
                        countDownTimer.resumeTimer()
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

    private fun onCallOnfidoCheckProcess(
        pkUser: Long,
        identification: String,
        idBrand: Int,
        idUserRequest: Long,
        user: String,
        onNextStep: () -> Unit
    ) {
        executeUseCase {
//            mutationOnfidoCheckProcessUseCase.invoke(
//                identification,
//                PACKAGE_NAME,
//                AppFlow.CREDIT_ORIGINATION.flow,
//                pkUser,
//                idUserRequest,
//                idBrand,
//                user
//            ).collectLatest { result ->
//                result.onSuccess {
//                    // nothing to do here
//                }
//                result.onFailure {
//                    // nothing to do here
//                }
//            }
            onNextStep()
        }
    }

    private fun onCallSaveCreditOperation(
        idUserRequest: Long,
        pkUser: Long,
        user: String,
        idBrand: Int
    ) {
        executeUseCase {
            mutationSaveCreditOperationUseCase.invoke(
                idUserRequest,
                pkUser,
                user,
                idBrand
            ).collectLatest { result ->
                result.onSuccess {}
                result.onFailure {}
            }
        }
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnOpenOnFidoSdk -> onOpenOnFidoSDK(
                event.result,
                event.onOnFidoError,
                event.onContinueEnable,
                event.onNextStep,
                event.pkUser,
                event.identification,
                event.idBrand,
                event.idUserRequest,
                event.user
            )

            is OnCallInFidoToken -> callMutationOnFidoInitialProcess(
                "${event.userData?.firstName}",
                "${event.userData?.firstLastName}",
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

    data class UIState(
        val isAlertVisible: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    sealed class UIEvent {
        data class OnCallInFidoToken(
            val userData: UserData?,
            val applicationId: String
        ) : UIEvent()

        data class OnOpenOnFidoSdk(
            val result: ActivityResult,
            val onOnFidoError: (dialogParameters: DialogParameters) -> Unit,
            val onContinueEnable: (value: Boolean) -> Unit,
            val onNextStep: () -> Unit,
            val pkUser: Long,
            val identification: String,
            val idBrand: Int,
            val idUserRequest: Long,
            val user: String
        ) : UIEvent()

        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class RefreshOnFidoToken(
            val userData: UserData?,
            val applicationId: String,
            val injectNewToken: (String?) -> Unit
        ) : UIEvent()
    }

    companion object {
        const val PACKAGE_NAME = "com.multimoney.multimoney.sv"
    }
}
