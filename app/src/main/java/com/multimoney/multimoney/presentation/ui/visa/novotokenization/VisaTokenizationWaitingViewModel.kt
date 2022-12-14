package com.multimoney.multimoney.presentation.ui.visa.novotokenization

import android.content.Context
import android.util.Log
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
import com.multimoney.domain.model.balance.BalanceCardInformation
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EMAIL
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_INFORMATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnStartNovoTokenization
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.util.NovoHelper
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

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

    // arguments
    var idBrand: Int = 0
    var pkUser: Long = 0
    var phone = ""
    var novoDeviceId: String = ""
    var email: String = ""
    var balanceCardInformation: BalanceCardInformation? = null

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        pkUser = savedStateHandle.get<Long>(PK_USER) ?: 0
        email = savedStateHandle.get<String>(EMAIL) ?: ""
        phone = savedStateHandle.get<String>(PHONE_NUMBER) ?: ""
        balanceCardInformation = savedStateHandle.get<BalanceCardInformation>(CARD_INFORMATION)
    }

    private fun startTokenizationProcess() {
        mmCountDownTimer.stopTimer()
        if (NovoVTS.isDeviceEnrolled().not()) {
            novoHelper.novoEnrollDevice(
                pkUser.toInt(),
                phone,
                onSuccessEnrollDevice = {
                    novoDeviceId = it.data
                    callNovoEnrollPan()
                },
                onErrorEnrollDevice = {
                    mmCountDownTimer.resumeTimer()
                    // odo handle novo sdk error
                    Log.wtf("MM_NOVO_ENROLL_DEVICE_ERROR", it.message)
                    Log.wtf("MM_NOVO_ENROLL_DEVICE_ERROR", it.code.toString())
                }
            )
        } else {
            callNovoEnrollPan()
        }
    }

    private fun callNovoEnrollPan() {
        val expirationDate = balanceCardInformation?.cardInformation?.expDate?.chunked(EXPIRATION_DATE_CHUCKS_LIMIT)
        novoHelper.novoEnrollPan(
            pkUser = pkUser.toInt(),
            email = email,
            accountNumber = balanceCardInformation?.cardInformation?.cardNumber ?: "",
            cardName = balanceCardInformation?.cardInformation?.holderName ?: "",
            cardCvv = balanceCardInformation?.cardInformation?.cValidation ?: "",
            cardExpirationMonth = expirationDate?.first() ?: "",
            cardExpirationYear = expirationDate?.last() ?: "",
            onSuccessEnrollDevice = {
                mmCountDownTimer.resumeTimer()
                NovoVTS.setFavoriteCard(it.data.vProvisionedToken)
            },
            onErrorEnrollDevice = {
                // todo handle novo sdk error
                mmCountDownTimer.resumeTimer()
                Log.wtf("MM_NOVO_ENROLL_PAN_ERROR", it.message)
                Log.wtf("MM_NOVO_ENROLL_PAN_ERROR", it.code.toString())
            }
        )
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
                    withStyle(style = Typography.body1.toSpanStyle().copy(fontWeight = FontWeight.SemiBold, color = color)) {
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

    data class UIState(
        // Fields
        val icon: Int = R.drawable.ic_novo_waiting_smartphone,
        val description: AnnotatedString = buildAnnotatedString {}
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToNextScreen -> navigateToNextScreen(event.screen)
            is OnGoToNextScreen -> goToNextScreen(event.context, event.color)
            is OnStartNovoTokenization -> startTokenizationProcess()
        }
    }

    sealed class UIEvent {
        data class OnNavigateToNextScreen(val screen: String) : UIEvent()
        data class OnGoToNextScreen(val context: Context, val color: Color) : UIEvent()
        object OnStartNovoTokenization : UIEvent()
    }

    companion object {
        const val MAX_STEPS = 3
        const val STEP_ONE = 1
        const val STEP_TWO = 2
        const val TIME_TO_WAITING_NOVO_STEP = 10000L
        const val EXPIRATION_DATE_CHUCKS_LIMIT = 2
    }
}
