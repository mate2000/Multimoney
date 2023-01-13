package com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.virtualcard.MutationUpdateCardVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_MASKED
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_EXPIRATION_MONTH
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_EXPIRATION_YEAR
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_DEFAULT
import com.multimoney.multimoney.presentation.navigation.navgraph.CARD_DESCRIPTION
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnNicknameValueChange
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnCvvValueChange
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnSaveChangesClick
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnDisclaimerClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class EditCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mutationUpdateCardVDUseCase: MutationUpdateCardVDUseCase
) : BaseViewModel(shouldObserveToken = true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idCard: Long? = 0
    private var identification: String? = ""
    var cardMasked: String? = ""
    var cardExpirationMonth: String? = ""
    var cardExpirationYear: String? = ""
    private var cardDefault: Boolean? = false
    private var user: String? = ""
    private var idBrand: Int? = null

    init {
        idCard = savedStateHandle[ID_CARD]
        identification = savedStateHandle[IDENTIFICATION]
        cardMasked = savedStateHandle[CARD_MASKED]
        cardExpirationMonth = savedStateHandle[CARD_EXPIRATION_MONTH]
        cardExpirationYear = savedStateHandle[CARD_EXPIRATION_YEAR]
        cardDefault = savedStateHandle[CARD_DEFAULT]
        user = savedStateHandle[USER]
        idBrand = savedStateHandle[ID_BRAND]
        uiState = uiState.copy(
            nickname = savedStateHandle[CARD_DESCRIPTION] ?: ""
        )
    }

    private fun validateForm() {
        uiState = uiState.copy(
            isSaveChangesEnabled = uiState.nickname.isNotEmpty() && uiState.cvv.isNotEmpty() && uiState.cvvError.first.not()
        )
    }

    private fun onNicknameValueChange(nickname: String) {
        uiState = uiState.copy(
            nickname = nickname
        )
        validateForm()
    }

    private fun onCvvValueChange(cvv: String) {
        if (cvv.length <= CVV_MAX_LENGTH) {
            uiState = uiState.copy(
                cvv = cvv,
                cvvError = if (cvv.length < CVV_MAX_LENGTH) {
                    Pair(true, R.string.profile_my_cards_edit_card_cvv_length_error)
                } else {
                    Pair(false, R.string.empty)
                }
            )
            validateForm()
        }
    }

    private fun onCvvCodeDialog() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.profile_my_cards_edit_card_cvv_dialog_title,
                descriptionResource = R.string.profile_my_cards_edit_card_cvv_dialog_description,
                positiveResource = R.string.understood,
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onSaveChangesClick(
        focusManager: FocusManager,
        onEditCardShowToastBaseEvent: () -> Unit
    ) {
        focusManager.clearFocus()
        onCallMutationUpdateCardVD(onEditCardShowToastBaseEvent)
    }

    private fun navigateBack(isRestart: Boolean) =
        navigateBack(
            isRestart = isRestart,
            popTo = Screen.ProfileCardListScreen.route
        )

    private fun onCallMutationUpdateCardVD(onEditCardShowToastBaseEvent: () -> Unit) =
        executeUseCase {
            mutationUpdateCardVDUseCase.invoke(
                idCard = idCard ?: 0,
                identification = identification ?: "",
                cardDescription = uiState.nickname,
                cardMasked = cardMasked ?: "",
                expirationMonth = cardExpirationMonth.orEmpty(),
                expirationYear = cardExpirationYear.orEmpty(),
                verificationValue = uiState.cvv,
                default = cardDefault ?: false,
                user = user ?: "",
                idBrand = idBrand ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
                    if (it?.isApproved == true) {
                        navigateBack(true)
                        onEditCardShowToastBaseEvent()
                    }
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

    data class UIState(
        val nickname: String = "",
        val cvv: String = "",
        val nicknameError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val cvvError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val isSaveChangesEnabled: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is OnNicknameValueChange -> onNicknameValueChange(event.nickname)
            is OnCvvValueChange -> onCvvValueChange(event.cvv)
            is OnBackClick -> navigateBack(false)
            is OnSaveChangesClick -> onSaveChangesClick(
                event.focusManager,
                event.onEditCardShowToastBaseEvent
            )
            is OnDisclaimerClick -> onCvvCodeDialog()
        }
    }

    sealed class UIEvent {
        object OnStart : UIEvent()
        object OnValidateForm : UIEvent()
        data class OnNicknameValueChange(val nickname: String) : UIEvent()
        data class OnCvvValueChange(val cvv: String) : UIEvent()

        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnSaveChangesClick(
            val focusManager: FocusManager,
            val onEditCardShowToastBaseEvent: () -> Unit
        ) : UIEvent()

        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnDisclaimerClick(val focusManager: FocusManager) : UIEvent()
    }

    companion object {
        const val VISUAL_DATE_SYMBOL = " | "
        const val CVV_MAX_LENGTH = 3
    }

}