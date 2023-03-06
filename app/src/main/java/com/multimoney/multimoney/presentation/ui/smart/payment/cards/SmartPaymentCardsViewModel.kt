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
import com.multimoney.multimoney.presentation.navigation.navgraph.*
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardListViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.PaymentScheduleCardViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnAddCard
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnCardSelected
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnHandleAddCardResponse
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnSetAddCardActivityOnResult
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.catalog.AddVisaCardErrors
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Colon
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
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
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
    private val queryListCardVDUseCase: QueryListCardVDUseCase,
    private val mutationCreateUserVDUseCase: MutationCreateUserVDUseCase,
    private val queryGetParametersMobileByCategoryUseCase: QueryGetParametersMobileByCategoryUseCase,
    private val getCardAutomaticDebitUseCase: QueryGetCardAutomaticDebitUseCase,
) : BaseViewModel(true) {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String = ""
    private var idCurrency: Int = 0
    private var tokenNumber: Long = 0
    private var infoUser: InfoUser? = null
    private var smartAccount: SmartAccountID? = null
    var currency: String = ""
    private var isEditBankAccount: Boolean = false
    private var isEditPaymentSchedule: Boolean = false
    private var previousScreen = ""
    var reactApplicationName: String = ""
    var reactUserName: String = ""
    var reactUserPass: String = ""
    var reactEndPoint: String = ""
    private var getCardAttempts = 0
    private var getPaymentScheduleAttempts = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0

    private fun onStart() {
        viewModelScope.launch {
            idCurrency = savedStateHandle[ID_CURRENCY] ?: 0
            tokenNumber = savedStateHandle[ACCOUNT_TOKEN] ?: 0
            user = dataStorePreferences.getUserName().first()
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            infoUser = savedStateHandle[INFO_USER]
            identification = dataStorePreferences.getIdentification().first()
            smartAccount = savedStateHandle[SMART_ACCOUNT]
            isEditBankAccount = savedStateHandle[IS_EDIT_BANK_ACCOUNT] ?: false
            isEditPaymentSchedule = savedStateHandle[IS_EDIT_PAYMENT_SCHEDULE] ?: false
            currency = if (idCurrency == Dollar.id) Dollar.symbol else Colon.symbol
            previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
            //onCallQueryGetClientCardsUseCase() //Ya no se puede usar
            getUserVDU()
        }
    }

    private fun getClientCardVisaDirect() = when {
        isEditBankAccount || previousScreen == Screen.PaymentCardVoucherScreen.baseRoute ->
            uiState =
                uiState.copy(
                    cardVisaDirect = savedStateHandle[CLIENT_CARD_VISA_DIRECT],
                    isCardListEmpty = false
                )
        previousScreen == Screen.HomeScreen.route && isEditPaymentSchedule.not() -> onCallQueryGetCardsUseCase() //TODO
        else -> onCallGetCardsAutomaticDebitUseCase()
    }

    private fun onCallGetCardsAutomaticDebitUseCase() = executeUseCase {
        getCardAutomaticDebitUseCase.invoke(
            user = infoUser?.email.orEmpty(),
            identification = identification,
            idBrand = infoUser?.idBrand ?: 0,
            idClient = idClient.toLong(),
            idLoanClient = idLoanClient.toLong()
        ).collectLatest { result ->
            getPaymentScheduleAttempts++
            result.onSuccess { cardsList ->
                uiState = uiState.copy(
                    isLoading = false,
                    cardVisaDirect = cardsList?.firstOrNull(),
                    isCardListEmpty = cardsList.isNullOrEmpty()
                )
            }.onFailure {
                setErrorAlertResult(attempts = getPaymentScheduleAttempts)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
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
                    cardVisaDirect = cardsList?.firstOrNull(),
                    isCardListEmpty = cardsList.isNullOrEmpty()
                )
            }.onFailure {
                setErrorAlertResult(attempts = getCardAttempts)
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
            category = PaymentScheduleCardViewModel.VISA_DIRECT_CATEGORY
        ).collectLatest { result ->
            result.onSuccess { parameters ->
                uiState = uiState.copy(
                    isLoading = false,
                )
                if (parameters?.isNotEmpty() == true) {
                    val applicationName = parameters.find {
                        it?.searchKey.equals(
                            PaymentScheduleCardViewModel.SEARCH_KEY_APPLICATION_NAME
                        )
                    }
                    reactApplicationName = applicationName?.value ?: ""

                    val endpoint = parameters.find {
                        it?.searchKey.equals(
                            PaymentScheduleCardViewModel.SEARCH_KEY_ENDPOINT
                        )
                    }
                    reactEndPoint = endpoint?.value ?: ""

                }
                getClientCardVisaDirect()
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
                getClientCardVisaDirect()
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

    private fun setErrorAlertResult(
        alertResultDescription: String = "",
        attempts: Int
    ) {
        uiState = uiState.copy(
            isAlertResultVisible = true,
            isAlertResultSuccess = false,
            alertResultIconResource = R.drawable.ic_error_symbol,
            alertResultTitleResource = R.string.payment_schedule_error_alert_title,
            alertResultDescription = alertResultDescription,
            alertResultDescriptionResource = if (attempts == PaymentScheduleCardViewModel.ATTEMPT_ONE && alertResultDescription.isEmpty()) {
                R.string.payment_schedule_error_alert_subtitle_one
            } else if (alertResultDescription.isEmpty()) {
                R.string.payment_schedule_error_alert_subtitle_two
            } else {
                R.string.empty
            },
            alertResultButtonResource = if (attempts == PaymentScheduleCardViewModel.ATTEMPT_ONE) {
                R.string.payment_schedule_error_alert_button_one
            } else {
                R.string.payment_schedule_error_alert_button_two
            },
            isLoading = false
        )
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

    private fun onAddCard() {
        uiState.addCardActivityOnResult()
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.SmartPaymentMethodScreenSV.route, isRestart = false)

    data class UIState(
        // Interactions
        val cardVDList: List<CardVisaDirect?> = emptyList(),
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val addCardActivityOnResult: () -> Unit = {},
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
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCardSelected -> onCardSelected(uiEvent.cardSelected)
            is OnAddCard -> onAddCard()
            is OnStart -> onStart()
            is OnSetAddCardActivityOnResult -> uiState =
                uiState.copy(addCardActivityOnResult = uiEvent.activityOnResult)
            is OnHandleAddCardResponse -> onHandleAddCardResponse(uiEvent.response, uiEvent.isError)
        }
    }

    sealed class UIEvent {
        data class OnCardSelected(val cardSelected: CardVisaDirect) : UIEvent()
        data class OnSetAddCardActivityOnResult(val activityOnResult: () -> Unit) : UIEvent()
        data class OnHandleAddCardResponse(val response: String, val isError: Boolean) : UIEvent()
        object OnAddCard : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnStart : UIEvent()
    }
}
