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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingViewModel.UIEvent.OnNavigateToNextScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class VisaTokenizationWaitingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var currentStep = 0
    var idBrand: Int = 0

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
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
        }
    }

    sealed class UIEvent {
        data class OnNavigateToNextScreen(val screen: String) : UIEvent()
        data class OnGoToNextScreen(val context: Context, val color: Color) : UIEvent()
    }

    companion object {
        const val MAX_STEPS = 3
        const val STEP_ONE = 1
        const val STEP_TWO = 2
        const val TIME_TO_WAITING_NOVO_STEP = 10000L
    }
}
