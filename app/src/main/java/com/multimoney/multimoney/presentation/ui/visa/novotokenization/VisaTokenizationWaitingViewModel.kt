package com.multimoney.multimoney.presentation.ui.visa.novotokenization

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.MutationSaveRegisterCoreLogUseCase
import com.multimoney.domain.interaction.security.MutationUserPhoneMobileSaveUseCase
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.domain.model.metrics.BaseEventDataDto
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.util.parametercorelog.EnrollCardParameters
import com.multimoney.domain.model.util.parametercorelog.EnrollDeviceParameters
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.BALANCE_CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.BaseEvent.OnOpenTapAndPayConfig
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnGetAndroidId
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnNavigateToHomeVisa
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnShowSuccessTokenizationScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnStartNovoTokenization
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.YEAR_FORMAT
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.RegisterCoreLogProcess
import com.multimoney.multimoney.presentation.util.getDateFormat
import com.multimoney.multimoney.presentation.util.getDeviceManufacture
import com.multimoney.multimoney.presentation.util.toJson
import com.multimoney.multimoney.util.NovoHelper
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class VisaTokenizationWaitingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mmCountDownTimer: MMCountDownTimer,
    private val novoHelper: NovoHelper,
    private val mutationUserPhoneMobileSaveUseCase: MutationUserPhoneMobileSaveUseCase,
    private val mutationSaveRegisterCoreLogUseCase: MutationSaveRegisterCoreLogUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var currentStep = 0
    var numAttemptsToStartTokenization: Int = 0

    // arguments
    var idBrand: Int = 0
    var pkUser: Long = 0
    var identification: String = ""
    var phone = ""
    var email: String = ""
    var balanceCardInformation: BalanceCardInformation? = null
    var androidId: String = ""

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        email = savedStateHandle.get<String>(EMAIL) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        balanceCardInformation = savedStateHandle.get<BalanceCardInformation>(BALANCE_CARD_INFORMATION)
    }

    private fun startTokenizationProcess() {
        executeUseCase {
            delay(HALF_SECOND)
            mmCountDownTimer.stopTimer()
            if (NovoVTS.isDeviceEnrolled().not()) {
                novoHelper.novoEnrollDevice(
                    identification,
                    EMPTY_PHONE,
                    onSuccessEnrollDevice = {
                        callNovoEnrollPan(it.data)
                    },
                    onErrorEnrollDevice = {
                        mmCountDownTimer.resumeTimer()
                        callMutationSaveRegisterCoreLog(
                            RegisterCoreLogProcess.NOVO_ENROLL_DEVICE.process,
                            Gson().toJson(EnrollDeviceParameters(identification, phone)),
                            it.message ?: ""
                        )
                        handleErrorResult()
                    }
                )
            } else {
                callNovoEnrollPan(NovoVTS.getWalletAccountNumber())
            }
        }
    }

    private fun callNovoEnrollPan(walletId: String) {
        val expirationDate = balanceCardInformation?.cardInformation?.expDate?.chunked(EXPIRATION_DATE_CHUCKS_LIMIT)
        novoHelper.novoEnrollPan(
            identification = identification,
            email = email,
            accountNumber = balanceCardInformation?.cardInformation?.cardNumber ?: "",
            cardName = balanceCardInformation?.cardInformation?.holderName ?: "",
            cardCvv = balanceCardInformation?.cardInformation?.cValidation ?: "",
            cardExpirationMonth = expirationDate?.first() ?: "",
            cardExpirationYear = getExpirationYear(expirationDate?.last() ?: ""),
            onErrorEnrollPan = {
                mmCountDownTimer.resumeTimer()
                callMutationSaveRegisterCoreLog(
                    RegisterCoreLogProcess.NOVO_ENROLL_CARD.process,
                    Gson().toJson(
                        EnrollCardParameters(
                            identification,
                            email,
                            balanceCardInformation?.cardInformation?.cardNumber ?: "",
                            balanceCardInformation?.cardInformation?.holderName ?: "",
                            balanceCardInformation?.cardInformation?.cValidation ?: "",
                            expirationDate?.first() ?: "",
                            getExpirationYear(
                                expirationDate?.last() ?: ""
                            )
                        )
                    ),
                    it.message ?: ""
                )
                handleErrorResult()
            },
            onSuccessEnrollPan = {
                mmCountDownTimer.resumeTimer()
                NovoVTS.setFavoriteCard(it.data.vProvisionedToken)
                createWallet(walletId)
            }
        )
    }

    private fun handleErrorResult() {
        if (numAttemptsToStartTokenization < MAX_NUMBER_ATTEMPTS_TO_START_TOKENIZATION) {
            setErrorAlertResult()
            numAttemptsToStartTokenization++
        } else {
            onNavigateToHomeMultimoneyVisa()
        }
    }

    private fun getExpirationYear(yearChunked: String): String {
        val currentYear = getDateFormat(Date(), YEAR_FORMAT)
        return "${currentYear.substring(YEAR_START_INDEX, YEAR_END_INDEX)}$yearChunked"
    }

    /**
     * This function execute the mutation userPhoneMobileSave without walletID
     * in order to create the wallet
     */
    private fun createWallet(walletId: String) {
        executeUseCase {
            mutationUserPhoneMobileSaveUseCase.invoke(
                idBrand,
                "",
                getDeviceManufacture(),
                pkUser,
                androidId,
                email
            ).collectLatest { result ->
                result.onSuccess {
                    linkWalletWithThisDevice(walletId = walletId)
                }.onFailure {
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onMessage {
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            descriptionResource = R.string.error,
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            }
        }
    }

    private fun callMutationSaveRegisterCoreLog(
        process: String,
        parameter: String,
        result: String
    ) {
        executeUseCase {
            mutationSaveRegisterCoreLogUseCase.invoke(
                email,
                idBrand,
                process,
                parameter,
                result
            )
        }
    }

    private fun linkWalletWithThisDevice(walletId: String) {
        executeUseCase {
            mutationUserPhoneMobileSaveUseCase.invoke(
                idBrand,
                walletId,
                getDeviceManufacture(),
                pkUser,
                androidId,
                email
            ).collectLatest { result ->
                result.onSuccess {
                    if (NovoVTS.isDefaultPaymentService().not()) {
                        emitBaseEvent(OnOpenTapAndPayConfig)
                    } else {
                        viewModelScope.launch {
                            if (dataStorePreferences.isAdjustFirstActivatedMMVisaEventRegister().first()) {
                                registerAdjustEvent(
                                    AdjustEventType.MM_VISA_CTA_FIRST_MM_VISA_ACTIVATED_5040,
                                    data = BaseEventDataDto(
                                        user = email,
                                        idBrand = idBrand,
                                        identification = identification
                                    ).toJson()
                                )
                                dataStorePreferences.isAdjustFirstActivatedMMVisaEventRegister(false)
                            }
                        }
                        uiState = uiState.copy(showSuccessTokenizationScreen = true)
                    }
                }.onFailure {
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onMessage {
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            descriptionResource = R.string.error,
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            }
        }
    }

    private fun goToNextScreen(context: Context, color: Color) {
        if (currentStep < MAX_STEPS) {
            currentStep++
            val newValues = getStepContent(currentStep, context, color)
            uiState = uiState.copy(
                icon = newValues.first,
                description = newValues.second
            )
        } else {
            currentStep = STEP_ONE
            val newValues = getStepContent(currentStep, context, color)
            uiState = uiState.copy(
                icon = newValues.first,
                description = newValues.second
            )
        }
    }

    private fun getStepContent(step: Int, context: Context, color: Color): Pair<Int, AnnotatedString> = when (step) {
        STEP_ONE -> {
            Pair(
                R.drawable.ic_novo_waiting_smartphone,
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = color)) {
                        append("${context.getString(R.string.visa_tokenization_waiting_description_one_bold)} ")
                    }
                    withStyle(style = Typography.body1.toSpanStyle().copy(color = color)) {
                        append(context.getString(R.string.visa_tokenization_waiting_description_one))
                    }
                }
            )
        }
        STEP_TWO -> {
            Pair(
                R.drawable.ic_novo_waiting_shopping_cart,
                buildAnnotatedString {
                    withStyle(style = Typography.body1.toSpanStyle().copy(color = color)) {
                        append("${context.getString(R.string.visa_tokenization_waiting_description_two_first)} ")
                    }
                    withStyle(
                        style = Typography.body1.toSpanStyle().copy(fontWeight = FontWeight.SemiBold, color = color)
                    ) {
                        append(context.getString(R.string.visa_tokenization_waiting_description_two_bold))
                    }
                    withStyle(style = Typography.body1.toSpanStyle().copy(color = color)) {
                        append(" ${context.getString(R.string.visa_tokenization_waiting_description_two_second)}")
                    }
                }
            )
        }
        else -> {
            if (idBrand == Brand.CostaRica.id) {
                Pair(
                    R.drawable.ic_novo_waiting_creditcard_outline,
                    buildAnnotatedString {
                        withStyle(
                            style = Typography.body1.toSpanStyle().copy(fontWeight = FontWeight.SemiBold, color = color)
                        ) {
                            append("${context.getString(R.string.visa_tokenization_waiting_description_three_bold)} ")
                        }
                        withStyle(style = Typography.body1.toSpanStyle().copy(color = color)) {
                            append(context.getString(R.string.visa_tokenization_waiting_description_three))
                        }
                    }
                )
            } else {
                Pair(
                    R.drawable.ic_novo_waiting_creditcard_outline,
                    buildAnnotatedString {
                        withStyle(
                            style = Typography.body1.toSpanStyle().copy(fontWeight = FontWeight.SemiBold, color = color)
                        ) {
                            append("${context.getString(R.string.visa_tokenization_waiting_description_three_bold_sv)} ")
                        }
                        withStyle(style = Typography.body1.toSpanStyle().copy(color = color)) {
                            append(context.getString(R.string.visa_tokenization_waiting_description_three_sv))
                        }
                    }
                )
            }
        }
    }

    private fun onNavigateToHomeMultimoneyVisa(isRestart: Boolean = false) =
        navigateBack(
            Screen.VisaCardScreen.route,
            isRestart
        )

    private fun onAlertButtonClick() {
        uiState = uiState.copy(
            isAlertResultVisible = false
        )
        startTokenizationProcess()
    }

    private fun setErrorAlertResult() {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = false,
            alertResultIconResource = R.drawable.ic_error_symbol,
            alertResultTitleResource = R.string.card_tokenization_error_title,
            alertResultDescriptionResource = when (idBrand) {
                Brand.CostaRica.id -> R.string.card_tokenization_error_description_cr
                else -> R.string.card_tokenization_error_description_sv
            },
            alertResultButtonResource = R.string.link
        )
    }

    fun getDescriptionTokenizationSuccess() =
        if (idBrand == Brand.CostaRica.id) {
            R.string.visa_tokenization_success_description
        } else {
            R.string.visa_tokenization_success_description_sv
        }

    data class UIState(
        // Fields
        val icon: Int = R.drawable.ic_novo_waiting_smartphone,
        val description: AnnotatedString = buildAnnotatedString {},
        val openDialog: DialogParameters = DialogParameters(),
        val isAlertResultSuccess: Boolean = true,
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonResource: Int = R.string.empty,
        val showSuccessTokenizationScreen: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnGoToNextScreen -> goToNextScreen(event.context, event.color)
            is OnStartNovoTokenization -> startTokenizationProcess()
            is OnGetAndroidId -> androidId = event.androidId
            is OnAlertButtonClick -> onAlertButtonClick()
            is OnAlertCloseClick -> onNavigateToHomeMultimoneyVisa()
            is OnShowSuccessTokenizationScreen -> uiState = uiState.copy(showSuccessTokenizationScreen = true)
            is OnNavigateToHomeVisa -> onNavigateToHomeMultimoneyVisa(true)
        }
    }

    sealed class UIEvent {
        data class OnGoToNextScreen(val context: Context, val color: Color) : UIEvent()
        object OnStartNovoTokenization : UIEvent()
        object OnAlertButtonClick : UIEvent()
        object OnAlertCloseClick : UIEvent()
        data class OnGetAndroidId(val androidId: String) : UIEvent()
        object OnShowSuccessTokenizationScreen : UIEvent()
        object OnNavigateToHomeVisa : UIEvent()
    }

    sealed class BaseEvent {
        object OnOpenTapAndPayConfig : BaseEvent()
    }

    companion object {
        const val MAX_STEPS = 3
        const val STEP_ONE = 1
        const val STEP_TWO = 2
        const val TIME_TO_WAITING_NOVO_STEP = 10000L
        const val EXPIRATION_DATE_CHUCKS_LIMIT = 2
        const val HALF_SECOND = 500L
        const val YEAR_START_INDEX = 0
        const val YEAR_END_INDEX = 2
        const val EMPTY_PHONE = "+1"
        const val MAX_NUMBER_ATTEMPTS_TO_START_TOKENIZATION = 1
    }
}
