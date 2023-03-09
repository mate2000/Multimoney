package com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.virtualcard.MutationCreateUserVDUseCase
import com.multimoney.domain.interaction.virtualcard.QueryGetParametersMobileByCategoryUseCase
import com.multimoney.domain.interaction.virtualcard.QueryListCardVDUseCase
import com.multimoney.domain.model.metrics.BaseEventDataDto
import com.multimoney.domain.model.security.InfoUser
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.ADD_CARD_RESPONSE
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.INFO_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCallQueryGetCards
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnHandleAddCardResponse
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnResumeTimer
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent.OnStopTimer
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.catalog.AddVisaCardErrors
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getAddCardErrorFromValue
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentScheduleCardListViewModel @Inject constructor(
    val countDownTimer: MMCountDownTimer,
    savedStateHandle: SavedStateHandle,
    private val queryListCardVDUseCase: QueryListCardVDUseCase,
    private val mutationCreateUserVDUseCase: MutationCreateUserVDUseCase,
    private val queryGetParametersMobileByCategoryUseCase: QueryGetParametersMobileByCategoryUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var paymentDate: String = ""
    private var identification: String = ""
    private var previousScreen = ""
    private var infoUser: InfoUser? = null
    var reactApplicationName: String = ""
    var reactUserName: String = ""
    var reactUserPass: String = ""
    var reactEndPoint: String = ""

    init {
        infoUser = savedStateHandle[INFO_USER]
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        paymentDate = savedStateHandle[PAYMENT_DATE] ?: ""
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
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
                    clientCardList = cardsList
                )
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
            callerId = infoUser?.countryCode.plus(infoUser?.phone.orEmpty()),
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

    private fun onHandleAddCardResponse(response: String, isError: Boolean) {
        if (isError) {
            setErrorAlertResultAddCard(response.getAddCardErrorFromValue())
        } else {
            logEvents(AdjustEventType.SETTINGS_FIRST_ADD_CARD_8005)
            onNavigateToVisaVerifyInformation(response)
        }
    }

    private fun onStart() {
        if (infoUser?.visaDirectUser.isNullOrEmpty() && infoUser?.visaDirectId.isNullOrEmpty()) {
            onCallMutationCreateUserVDUseCase()
        } else {
            reactUserName = infoUser?.visaDirectUser ?: ""
            reactUserPass = infoUser?.visaDirectId ?: ""
            onCallGetParametersMobileByCategoryUseCase()
        }
    }

    private fun onNavigateToVisaVerifyInformation(response: String) = popAndNavigateTo(
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
                getNavParam(USER_NAME, infoUser?.userName.orEmpty())
            ),
        popTo = Screen.PaymentScheduleCardListScreen.route
    )

    private fun onCardSelected(card: CardVisaDirect?) = popAndNavigateTo(
        route = "${Screen.PaymentScheduleCardScreen.baseRoute}/$idClient/$idLoanClient/${
        encodeData(
            card
        )
        }/$paymentDate/${true}/${Screen.HomeScreen.route}/${false}/$identification/${
        encodeData(
            infoUser
        )
        }",
        popTo = Screen.PaymentScheduleCardListScreen.route
    )

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

    private fun onNavigateBack() = navigateBack(popTo = Screen.PaymentScheduleCardScreen.route, isRestart = false)

    private fun onNavigateBackHome(isRestart: Boolean) =
        navigateBack(popTo = Screen.HomeScreen.route, isRestart = isRestart)

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.payment_schedule_card_list_dialog_title,
                descriptionResource = R.string.payment_schedule_card_list_dialog_description,
                positiveResource = R.string.accept,
                negativeResource = R.string.cancel,
                positiveAction = {
                    navigateBack(
                        popTo = Screen.HomeScreen.route,
                        isRestart = false,
                        homeState = HomeState.COLLAPSED
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onResumeTimer() {
        countDownTimer.resumeTimer()
    }

    private fun onStopTimer() {
        countDownTimer.stopTimer()
    }

    fun logEvents(adjustEventType: AdjustEventType) {
        viewModelScope.launch {
            getAdjustEvent(adjustEventType).invoke()
        }
    }

    private fun getAdjustEvent(adjustEventType: AdjustEventType): suspend () -> Unit {
        val baseAdjustEvent = BaseEventDataDto(
            user = infoUser?.email,
            idBrand = infoUser?.idBrand,
            idClient = idClient,
            idLoanClient = idLoanClient,
            identification = identification
        )
        return when (adjustEventType) {
            AdjustEventType.SETTINGS_FIRST_ADD_CARD_8005 -> {
                getAddACardEvent(baseAdjustEvent)
            }
            AdjustEventType.SETTINGS_CTA_FIRST_START_FLOW_CARD_8007 -> {
                getStartAddACardEvent(baseAdjustEvent)
            }
            AdjustEventType.SETTINGS_CTA_FIRST_FINISH_FLOW_CARD_8008 -> {
                getFinishAddACardEvent(baseAdjustEvent)
            }
            else -> suspend {}
        }
    }

    private fun getAddACardEvent(baseAdjustEvent: BaseEventDataDto) =
        suspend {
            if (dataStorePreferences.isAdjustAddCardEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.SETTINGS_FIRST_ADD_CARD_8005,
                    data = baseAdjustEvent.toJson()
                )
                dataStorePreferences.isAdjustAddCardEventRegister(false)
            }
        }

    private fun getStartAddACardEvent(baseAdjustEvent: BaseEventDataDto) =
        suspend {
            if (dataStorePreferences.isAdjustFlowAddCardEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.SETTINGS_CTA_FIRST_START_FLOW_CARD_8007,
                    applyAdjust = false,
                    data = baseAdjustEvent.toJson()
                )
                dataStorePreferences.isAdjustFlowAddCardEventRegister(false)
            }
        }

    private fun getFinishAddACardEvent(baseAdjustEvent: BaseEventDataDto) =
        suspend {
            if (dataStorePreferences.isAdjustFinishFlowAddCardEventRegister().first()) {
                registerAdjustEvent(
                    AdjustEventType.SETTINGS_CTA_FIRST_FINISH_FLOW_CARD_8008,
                    applyAdjust = false,
                    data = baseAdjustEvent.toJson()
                )
                dataStorePreferences.isAdjustFinishFlowAddCardEventRegister(false)
            }
        }

    data class UIState(
        // Interactions
        val clientCardList: List<CardVisaDirect?>? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isAlertResultSuccess: Boolean = true,
        val isAlertResultVisaError: Boolean = false,
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = 0,
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescription: String = "",
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertResultButtonResource: Int = R.string.empty
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome(false)
            is OnCloseClick -> onCloseClick()
            is OnCallQueryGetCards -> onCallQueryGetCardsUseCase()
            is OnCardSelected -> onCardSelected(uiEvent.card)
            is OnStart -> onStart()
            is OnHandleAddCardResponse -> onHandleAddCardResponse(uiEvent.response, uiEvent.isError)
            is OnStopTimer -> onStopTimer()
            is OnResumeTimer -> onResumeTimer()
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetCards : UIEvent()
        class OnCardSelected(val card: CardVisaDirect?) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnCloseClick : UIEvent()
        object OnStart : UIEvent()
        object OnStopTimer : UIEvent()
        object OnResumeTimer : UIEvent()
        data class OnHandleAddCardResponse(val response: String, val isError: Boolean) : UIEvent()
    }

    companion object {
        const val RESULT_CODE_PROCESS_FINISHED = 200
        const val RESULT_CODE_PROCESS_INCOMPLETE = 400
        const val RESPONSE_VALUE = "response_value_key"
        const val RESPONSE_IS_ERROR = "response_error_key"
        const val VISA_DIRECT_CATEGORY = "VISA_DIRECT"
        const val APPLICATION_NAME = "applicationName"
        const val VISA_USER_NAME = "userName"
        const val VISA_USER_PASS = "userPassword"
        const val ENDPOINT = "endpoint"
        const val SEARCH_KEY_ENDPOINT = "FTT_SERVER_VISADIRECT"
        const val SEARCH_KEY_APPLICATION_NAME = "APPLICATIONNAME_VISADIRECT"
    }
}
