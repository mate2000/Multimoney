package com.multimoney.multimoney.presentation.ui.visa.verification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.multimoney.data.mapper.virtualcard.AddCardResponse
import com.multimoney.domain.interaction.virtualcard.MutationCreateCardVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.ADD_CARD_RESPONSE
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getNavParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class VisaVerifyInformationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mutationCreateCardVDUseCase: MutationCreateCardVDUseCase,
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var idCard: String = ""
    private var identification: String = ""
    private var user: String = ""
    private var userName: String = ""
    private var idBrand: Int = 0
    private var previousScreen = ""
    private var addCardResponse = ""

    init {
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idCard = savedStateHandle[ID_CARD] ?: ""
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        addCardResponse = savedStateHandle[ADD_CARD_RESPONSE] ?: ""
        userName = savedStateHandle[USER_NAME] ?: ""
    }

    private fun onCallMutationCreateCardVDUseCase() = executeUseCase {
        mutationCreateCardVDUseCase.invoke(
            identification = identification,
            cardTokenID = getCardTokenId(addCardResponse),
            default = true,
            user = userName,
            idBrand = idBrand
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
                idCard = it?.cardTokenId ?: ""
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

    fun getCardTokenId(response: String): String {
        return parseResponse(response)?.cardTokenId ?: ""
    }

    fun parseResponse(value: String): AddCardResponse? {
        return Gson().fromJson(value, AddCardResponse::class.java)
    }

    private fun onNavigateToNextScreen() = navigateTo(
        route = Screen.VisaVerifyDepositScreen.baseRoute
            .plus(
                getNavParam(IDENTIFICATION, identification)
            )
            .plus(
                getNavParam(ID_CARD, idCard)
            )
            .plus(
                getNavParam(USER, user)
            )
            .plus(
                getNavParam(ID_BRAND, idBrand)
            )
            .plus(
                getNavParam(PREVIOUS_SCREEN, previousScreen)
            )
    )

    private fun onNavigateBack() {
        if (previousScreen == Screen.ProfileCardListScreen.baseRoute) {
            navigateBack(popTo = Screen.ProfileCardListScreen.route, isRestart = false)
        } else {
            // TODO return to nickname card screen
        }
    }

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.visa_verified_dialog_title,
                descriptionResource = R.string.visa_verified_dialog_description,
                positiveResource = R.string.cancel,
                positiveAction = { onNavigateBackHome() },
                negativeResource = R.string.button_continue,
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnNavigateToNextScreen -> onNavigateToNextScreen()
            is UIEvent.OnCallMutationCreateCardVDUseCase -> onCallMutationCreateCardVDUseCase()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnCloseClick : UIEvent()
        object OnNavigateToNextScreen : UIEvent()
        object OnCallMutationCreateCardVDUseCase : UIEvent()
    }
}
