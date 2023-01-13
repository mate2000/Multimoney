package com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard

import android.util.Log
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
import com.multimoney.multimoney.presentation.navigation.navgraph.*
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnNicknameValueChange
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnCvvValueChange
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnSaveChangesClick
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.home.profile.mycards.editcard.EditCardViewModel.UIEvent.OnDisclaimerClick
import com.multimoney.multimoney.presentation.util.getDateTimeFormatterPattern
import com.multimoney.multimoney.presentation.util.getLocalDateFromParse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private var cardDescription: String? = ""
    var cardMasked: String? = ""
    var cardValidDate: String? = ""
    private var cardDefault: Boolean? = false
    private var user: String? = ""
    private var idBrand: Int? = null
    private var cardValidDateFormatter: DateTimeFormatter? = null
    private var cardValidLocalDate: LocalDate? = null

    init {
        idCard = savedStateHandle[ID_CARD]
        identification = savedStateHandle[IDENTIFICATION]
        cardMasked = savedStateHandle[CARD_MASKED]
        cardDefault = savedStateHandle[CARD_DEFAULT]
        user = savedStateHandle[EMAIL]
        idBrand = savedStateHandle[ID_BRAND]
        cardValidDateFormatter = getDateTimeFormatterPattern(BACKEND_DATE_FORMAT)
        cardValidLocalDate =
            //(cardValidDateFormatter)?.let { getLocalDateFromParse(savedStateHandle[CARD_VALID_DATE] ?: "", it) }
            (cardValidDateFormatter)?.let { getLocalDateFromParse("2018-05-10" ?: "", it) }
        cardValidDate = cardValidLocalDate?.format(getDateTimeFormatterPattern(DATE_MONTH_FORMAT)).orEmpty()
            .plus(VISUAL_DATE_SYMBOL)
            .plus(cardValidLocalDate?.format(getDateTimeFormatterPattern(DATE_YEAR_FORMAT)).orEmpty())
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

    private fun onSaveChangesClick(focusManager: FocusManager) {
        focusManager.clearFocus()
        onCallMutationUpdateCardVD()

    }

    private fun navigateBack(isRestart: Boolean) =
        navigateBack(
            isRestart = isRestart,
            popTo = Screen.ProfileScreen.route
        )

    private fun onCallMutationUpdateCardVD() =
        executeUseCase {
            mutationUpdateCardVDUseCase.invoke(
                idCard = idCard ?: 0,
                identification = identification ?: "",
                cardDescription = uiState.nickname,
                cardMasked = cardMasked ?: "",
                expirationMonth = "",
                expirationYear = "",
                verificationValue = uiState.cvv,
                default = cardDefault ?: false,
                user = user ?: "",
                idBrand = idBrand ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
                    navigateBack(true)
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
            is OnSaveChangesClick -> onSaveChangesClick(event.focusManager)
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
        data class OnSaveChangesClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnDisclaimerClick(val focusManager: FocusManager) : UIEvent()
    }

    companion object {
        const val DATE_FORMAT = "dd-MM-yyyy"
        const val BACKEND_DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_MONTH_FORMAT = "MM"
        const val DATE_YEAR_FORMAT = "yy"
        const val VISUAL_DATE_SYMBOL = " | "
        const val CVV_MAX_LENGTH = 3
    }

}