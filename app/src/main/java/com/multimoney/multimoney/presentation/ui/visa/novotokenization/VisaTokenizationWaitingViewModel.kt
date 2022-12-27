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
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.MutationUserPhoneMobileSaveUseCase
import com.multimoney.domain.model.balance.CardInformation
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.AVAILABLE_BALANCE_LABEL
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnGetAndroidId
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnStartNovoTokenization
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnAlertButtonClick
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnAlertCloseClick
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.YEAR_FORMAT
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getDateFormat
import com.multimoney.multimoney.presentation.util.getDeviceManufacture
import com.multimoney.multimoney.util.NovoHelper
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VisaTokenizationWaitingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
    private val mmCountDownTimer: MMCountDownTimer,
    private val novoHelper: NovoHelper,
    private val mutationUserPhoneMobileSaveUseCase: MutationUserPhoneMobileSaveUseCase
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
    var cardInformation: CardInformation? = null
    var androidId: String = ""
    var availableBalanceLabel: String? = null

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        email = savedStateHandle.get<String>(EMAIL) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        cardInformation = savedStateHandle.get<CardInformation>(CARD_INFORMATION)
        availableBalanceLabel = savedStateHandle[AVAILABLE_BALANCE_LABEL]
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
                        handleErrorResult()
                    }
                )
            } else {
                callNovoEnrollPan(NovoVTS.getWalletAccountNumber())
            }
        }
    }

    private fun callNovoEnrollPan(walletId: String) {
        val expirationDate = cardInformation?.expDate?.chunked(EXPIRATION_DATE_CHUCKS_LIMIT)
        novoHelper.novoEnrollPan(
            identification = identification,
            email = email,
            accountNumber = cardInformation?.cardNumber ?: "",
            cardName = cardInformation?.holderName ?: "",
            cardCvv = cardInformation?.cValidation ?: "",
            cardExpirationMonth = expirationDate?.first() ?: "",
            cardExpirationYear = getExpirationYear(expirationDate?.last() ?: ""),
            onErrorEnrollPan = {
                mmCountDownTimer.resumeTimer()
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
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            titleResource = R.string.success,
                            descriptionResource = R.string.visa_card_success_message_label,
                            isActive = mutableStateOf(true)
                        )
                    )
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
            if (idBrand == Brand.Guatemala.id) {
                Pair(
                    R.drawable.ic_novo_waiting_creditcard_outline,
                    buildAnnotatedString {
                        withStyle(
                            style = Typography.body1.toSpanStyle().copy(fontWeight = FontWeight.SemiBold, color = color)
                        ) {
                            append("${context.getString(R.string.visa_tokenization_waiting_description_three_bold_gt)} ")
                        }
                        withStyle(style = Typography.body1.toSpanStyle().copy(color = color)) {
                            append(context.getString(R.string.visa_tokenization_waiting_description_three_gt))
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
                            append("${context.getString(R.string.visa_tokenization_waiting_description_three_bold)} ")
                        }
                        withStyle(style = Typography.body1.toSpanStyle().copy(color = color)) {
                            append(context.getString(R.string.visa_tokenization_waiting_description_three))
                        }
                    }
                )
            }
        }
    }

    private fun navigateToNextScreen(screen: String) {
        viewModelScope.launch {
            dataStorePreferences.isOnBoardingEnabled(false)
            popAndNavigateTo(
                route = if (screen == Screen.SignUpScreen.baseRoute) {
                    "$screen/".plus(0)
                } else {
                    screen
                },
                popTo = Screen.OnBoardingScreen.route
            )
        }
    }

    private fun onNavigateToHomeMultimoneyVisa() =
        popAndNavigateTo(
            "${Screen.VisaCardScreen.baseRoute}/$idBrand/$pkUser/$identification/$email/$phone/${
                encodeData(
                    cardInformation
                )
            }/$availableBalanceLabel",
            Screen.VisaTokenizationWaitingScreen.route
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
                Brand.Guatemala.id -> R.string.card_tokenization_error_description_gt
                else -> R.string.card_tokenization_error_description_sv_cr
            },
            alertResultButtonResource = R.string.link
        )
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
        val alertResultButtonResource: Int = R.string.empty
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToNextScreen -> navigateToNextScreen(event.screen)
            is OnGoToNextScreen -> goToNextScreen(event.context, event.color)
            is OnStartNovoTokenization -> startTokenizationProcess()
            is OnGetAndroidId -> androidId = event.androidId
            is OnAlertButtonClick -> onAlertButtonClick()
            is OnAlertCloseClick -> onNavigateToHomeMultimoneyVisa()
        }
    }

    sealed class UIEvent {
        data class OnNavigateToNextScreen(val screen: String) : UIEvent()
        data class OnGoToNextScreen(val context: Context, val color: Color) : UIEvent()
        object OnStartNovoTokenization : UIEvent()
        object OnAlertButtonClick : UIEvent()
        object OnAlertCloseClick : UIEvent()
        data class OnGetAndroidId(val androidId: String) : UIEvent()
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
