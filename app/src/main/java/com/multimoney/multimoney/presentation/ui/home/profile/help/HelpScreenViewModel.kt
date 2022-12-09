package com.multimoney.multimoney.presentation.ui.home.profile.help

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.profile.QueryCountryContactUseCase
import com.multimoney.domain.model.profile.CountryContact
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HelpScreenViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle,
    private val queryCountryContactUseCase: QueryCountryContactUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    private fun getContactInfo() =
        executeUseCase {
            queryCountryContactUseCase.invoke(
                user = dataStorePreferences.getUserName().first(),
                idBrand = savedStateHandle[ID_BRAND] ?: 0
            ).collectLatest { result ->
                result.onSuccess { contactInfo ->
                    uiState = uiState.copy(contactInfo = contactInfo)
                    uiState = uiState.copy(isLoading = false)
                }
                result.onFailure {
                    onUIEvent(
                        UIEvent.OnFailureWithDialog(
                            isLoading = false,
                            openDialog = DialogParameters(
                                description = it.getError() ?: "",
                                isActive = mutableStateOf(true)
                            )
                        )
                    )
                    uiState = uiState.copy(isLoading = false)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }

            }
        }

    data class UIState(
        val contactInfo: CountryContact? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfileScreen.route, false)
            is UIEvent.OnGetContactInfo -> getContactInfo()
            is UIEvent.OnChatWithUsClick -> Timber.d("Open Whatsapp")
            is UIEvent.OnCallToAttentionCenterClick -> Timber.d("Open phone")
            is UIEvent.OnFAQClick -> Timber.d("Open FAQ website")
            is UIEvent.OnTermsAndConditionsClick -> Timber.d("Open Terms Website")
            is UIEvent.OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = uiEvent.isLoading, openDialog = uiEvent.openDialog)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnGetContactInfo : UIEvent()
        object OnChatWithUsClick : UIEvent()
        object OnCallToAttentionCenterClick : UIEvent()
        object OnFAQClick : UIEvent()
        object OnTermsAndConditionsClick : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()
    }
}