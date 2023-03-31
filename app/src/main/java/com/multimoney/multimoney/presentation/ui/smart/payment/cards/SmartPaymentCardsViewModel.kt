package com.multimoney.multimoney.presentation.ui.smart.payment.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.credit.QueryGetCardAutomaticDebitUseCase
import com.multimoney.domain.interaction.virtualcard.MutationCreateUserVDUseCase
import com.multimoney.domain.interaction.virtualcard.QueryGetParametersMobileByCategoryUseCase
import com.multimoney.domain.interaction.virtualcard.QueryListCardVDUseCase
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.ADD_CARD_RESPONSE
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.INFO_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnAddCard
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnHandleAddCardResponse
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnStopTimer
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnResumeTimer
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.VisaUtils.SEARCH_KEY_APPLICATION_NAME
import com.multimoney.multimoney.presentation.util.VisaUtils.SEARCH_KEY_ENDPOINT
import com.multimoney.multimoney.presentation.util.VisaUtils.VISA_DIRECT_CATEGORY
import com.multimoney.multimoney.presentation.util.catalog.AddVisaCardErrors
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.getAddCardErrorFromValue
import com.multimoney.multimoney.presentation.util.getNavParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartPaymentCardsViewModel @Inject constructor(
    val countDownTimer: MMCountDownTimer,
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
    private val queryListCardVDUseCase: QueryListCardVDUseCase,
    private val mutationCreateUserVDUseCase: MutationCreateUserVDUseCase,
    private val queryGetParametersMobileByCategoryUseCase: QueryGetParametersMobileByCategoryUseCase,
    private val getCardAutomaticDebitUseCase: QueryGetCardAutomaticDebitUseCase
) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var identification: String = ""
    private var infoUser: InfoUser? = null
    private var smartAccount: SmartAccountID? = null
    var reactApplicationName: String = ""
    var reactUserName: String = ""
    var reactUserPass: String = ""
    var reactEndPoint: String = ""

    private fun onStart() {
        viewModelScope.launch {
            infoUser = savedStateHandle[INFO_USER]
            identification = dataStorePreferences.getIdentification().first()
            smartAccount = savedStateHandle[SMART_ACCOUNT]
            getUserVDU()
        }
    }

    private fun onAddCard(enabled: Boolean){
        uiState = uiState.copy(
            isAddCardEnabled = enabled
        )
    }

    private fun onCallQueryGetCardsUseCase() = executeUseCase {
        queryListCardVDUseCase.invoke(
            user = infoUser?.email.orEmpty(),
            idBrand = infoUser?.idBrand ?: 0,
            identification = identification
        ).collectLatest { result ->
            result.onSuccess { cardsList ->
                uiState = uiState.copy(
                    isLoading = false,
                    cardVDList = cardsList?.filter { it?.verified == true },
                    isCardListEmpty = cardsList?.filter { it?.verified == true }.isNullOrEmpty()
                )
                if (cardsList.isNullOrEmpty()) {
                    onAddCard(enabled = true)
                }
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    ),
                    cardVDList = listOf(),
                    isCardListEmpty = true
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onNavigateToVisaVerifyInformation(response: String) = navigateTo(
        route = Screen.VisaVerifyInformationScreen.baseRoute
            .plus(
                getNavParam(IDENTIFICATION, identification)
            )
            .plus(
                getNavParam(ID_CARD, "")
            )
            .plus(
                getNavParam(USER, infoUser?.email.orEmpty())
            )
            .plus(
                getNavParam(ID_BRAND, infoUser?.idBrand ?: 0)
            )
            .plus(
                getNavParam(PREVIOUS_SCREEN, Screen.ProfileCardListScreen.baseRoute)
            )
            .plus(
                getNavParam(ADD_CARD_RESPONSE, response)
            )
            .plus(
                getNavParam(
                    USER_NAME,
                    infoUser?.userName.orEmpty()
                )
            )
    )

    private fun onCallGetParametersMobileByCategoryUseCase() = executeUseCase {
        queryGetParametersMobileByCategoryUseCase.invoke(
            idBrand = infoUser?.idBrand ?: 0,
            category = VISA_DIRECT_CATEGORY
        ).collectLatest { result ->
            result.onSuccess { parameters ->
                uiState = uiState.copy(
                    isLoading = false
                )
                if (parameters?.isNotEmpty() == true) {
                    val applicationName = parameters.find {
                        it?.searchKey.equals(
                            SEARCH_KEY_APPLICATION_NAME
                        )
                    }
                    reactApplicationName = applicationName?.value ?: ""

                    val endpoint = parameters.find {
                        it?.searchKey.equals(
                            SEARCH_KEY_ENDPOINT
                        )
                    }
                    reactEndPoint = endpoint?.value ?: ""
                }
                onCallQueryGetCardsUseCase()
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onCallMutationCreateUserVDUseCase() = executeUseCase {
        mutationCreateUserVDUseCase.invoke(
            identification = identification,
            firstName = infoUser?.firstName.orEmpty(),
            secondName = infoUser?.secondName.orEmpty(),
            lastName = infoUser?.lastName.orEmpty(),
            secondLastName = infoUser?.secondLastName.orEmpty(),
            email = infoUser?.email.orEmpty(),
            callerId = infoUser?.countryCode?.replace("+", "").plus(infoUser?.phone.orEmpty()),
            user = infoUser?.userName.orEmpty(),
            idBrand = infoUser?.idBrand ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
                reactUserName = it?.userName ?: ""
                reactUserPass = it?.password ?: ""
                onCallGetParametersMobileByCategoryUseCase()
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun getUserVDU() {
        if (infoUser?.visaDirectUser.isNullOrEmpty() && infoUser?.visaDirectId.isNullOrEmpty()) {
            onCallMutationCreateUserVDUseCase()
        } else {
            reactUserName = infoUser?.visaDirectUser ?: ""
            reactUserPass = infoUser?.visaDirectId ?: ""
            onCallGetParametersMobileByCategoryUseCase()
        }
    }

    private fun onStopTimer() {
        countDownTimer.stopTimer()
    }

    private fun onResumeTimer() {
        countDownTimer.resumeTimer()
    }

    private fun setErrorAlertResultAddCard(
        addVisaCardErrors: AddVisaCardErrors
    ) {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultVisaError = true,
            isAlertResultSuccess = false,
            alertResultIconResource = R.drawable.ic_error_symbol,
            alertResultTitleResource = addVisaCardErrors.title,
            alertResultDescriptionResource = addVisaCardErrors.description,
            alertResultButtonResource = R.string.payment_schedule_error_alert_button_two,
            isLoading = false
        )
    }

    private fun onHandleAddCardResponse(response: String, isError: Boolean) {
        if (isError) {
            setErrorAlertResultAddCard(response.getAddCardErrorFromValue())
        } else {
            onNavigateToVisaVerifyInformation(response)
        }
    }

    private fun onCardSelected(cardSelected: CardVisaDirect) {
        navigateTo(
            "${Screen.SmartPaymentSavingAmountSV.baseRoute}/" +
                "${Screen.SmartPaymentCardsScreenSV.baseRoute}/" +
                "${encodeData(cardSelected)}/${encodeData(smartAccount)}/${SmartTransferTypes.VisaToSmart.id}"

        )
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.SmartPaymentMethodScreenSV.route, isRestart = false)

    private fun onNavigateBackHome(isRestart: Boolean) =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = isRestart)

    data class UIState(
        // Interactions
        val cardVDList: List<CardVisaDirect?>? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isAlertResultSuccess: Boolean = true,
        val isAlertResultVisaError: Boolean = false,
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescription: String = "",
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonResource: Int = R.string.empty,
        val cardVisaDirect: CardVisaDirect? = null,
        val isCardListEmpty: Boolean = true,
        val isAddCardEnabled: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome(false)
            is OnCardSelected -> onCardSelected(uiEvent.cardSelected)
            is OnStart -> onStart()
            is OnHandleAddCardResponse -> onHandleAddCardResponse(uiEvent.response, uiEvent.isError)
            is OnStopTimer -> onStopTimer()
            is OnResumeTimer -> onResumeTimer()
            is OnAddCard -> onAddCard(uiEvent.enabled)
        }
    }

    sealed class UIEvent {
        data class OnCardSelected(val cardSelected: CardVisaDirect) : UIEvent()
        data class OnHandleAddCardResponse(val response: String, val isError: Boolean) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnStart : UIEvent()
        object OnStopTimer : UIEvent()
        object OnResumeTimer : UIEvent()
        data class OnAddCard(val enabled: Boolean) : UIEvent()
    }


}
