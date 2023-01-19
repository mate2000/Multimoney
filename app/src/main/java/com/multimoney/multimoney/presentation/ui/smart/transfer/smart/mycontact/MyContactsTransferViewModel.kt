package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryRelatedContactsByPhoneUseCase
import com.multimoney.domain.model.accountsmart.PhoneSmart
import com.multimoney.domain.model.accountsmart.RelatedContact
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CONTACTS
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class MyContactsTransferViewModel @Inject constructor(
    private val queryRelatedContactsByPhoneUseCaseImp: QueryRelatedContactsByPhoneUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    var idBrand: Int = 0
    var relatedContacts: List<RelatedContact> = listOf()

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        relatedContacts =
            savedStateHandle.get<Array<RelatedContact>>(CONTACTS)?.toList() ?: listOf()
    }

    private fun callQueryRelatedContactsByPhoneUseCaseImp() = executeUseCase {
        queryRelatedContactsByPhoneUseCaseImp.invoke(
            user = user,
            idBrand = idBrand,
            contacts = relatedContacts
        ).collectLatest { result ->
            result.onSuccess { accountList ->
                uiState = uiState.copy(isLoading = false)
                if (accountList?.phones?.isEmpty() == true) {
                    //navigateToAddSACAccount()
                } else {
                    accountList?.phones?.let {
                        uiState = uiState.copy(relatedContactList = it)
                    }
                }
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
                onFailure(it)
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false)

    private fun navigateToAddToFavoriteAccount(contactToFavorite: PhoneSmart) {
        // Todo Add  account to favorite REV-1449
    }

    private fun onQueryValueChange(value: String) {
        uiState = uiState.copy(
            queryValue = value
        )
    }

    private fun onAddSACAccountClick() {
        // Todo Add SAC account of transfer recipient REV-1447
    }

    data class UIState(
        val queryValue: String = "",
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var relatedContactList: List<PhoneSmart?> = listOf()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnAddToFavoriteAccountClick -> navigateToAddToFavoriteAccount(uiEvent.contactToFavorite)
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnAddSACAccountClick -> onAddSACAccountClick()
            is UIEvent.OnQueryValueChange -> onQueryValueChange(uiEvent.value)
            is UIEvent.OnNavigateToHome -> onNavigateToHome()
            UIEvent.OnCallQueryRelatedContactsByPhoneUseCaseImp -> callQueryRelatedContactsByPhoneUseCaseImp()
        }
    }

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true
        )
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateToHome : UIEvent()
        object OnAddSACAccountClick : UIEvent()
        data class OnAddToFavoriteAccountClick(val contactToFavorite: PhoneSmart) : UIEvent()
        object OnCallQueryRelatedContactsByPhoneUseCaseImp : UIEvent()
        data class OnQueryValueChange(val value: String) : UIEvent()
    }
}