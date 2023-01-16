package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryRelatedContactsByPhoneUseCase
import com.multimoney.domain.model.accountsmart.PhoneSmart
import com.multimoney.domain.model.accountsmart.RelatedContact
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CONTACTS
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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
        relatedContacts = savedStateHandle.get<Array<RelatedContact>>(CONTACTS)?.toList() ?: listOf()
    }

    private fun callQueryRelatedContactsByPhoneUseCaseImp() {
        val listFake =
            relatedContacts.map {
                PhoneSmart(
                    identification = it.phoneNumber + "TEST",
                    number = it.phoneNumber + "TEST",
                    bankName = it.phoneNumber + "TEST",
                    idCurrency = it.phoneNumber + "TEST",
                    currency = it.phoneNumber + "TEST",
                    ibanNumber = it.phoneNumber + "TEST",
                    email = it.phoneNumber + "TEST",
                    titular = it.phoneNumber + "TEST",
                    accountNumber = it.phoneNumber + "TEST",
                )
            }
        uiState = uiState.copy(relatedContactList = listFake)
    }

    /*
    private fun callQueryRelatedContactsByPhoneUseCaseImp() = executeUseCase {
        queryRelatedContactsByPhoneUseCaseImp.invoke(
            user = user,
            idBrand = idBrand,
           contacts = relatedContacts
        ).collectLatest { result ->
            result.onSuccess { accountList ->
                uiState = uiState.copy(isLoading = false)
                if (accountList?.phones?.isEmpty() == true) {
                    navigateToAddSACAccount()
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
    }*/

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

    private fun navigateToAddSACAccount() {
        // Todo Add SAC account of transfer recipient
    }

    private fun onAccountClick(selectedContactAccount: RelatedContact?) {

    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var relatedContactList: List<PhoneSmart?> = listOf()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            UIEvent.OnAddSACAccountClick -> navigateToAddSACAccount()
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnAccountClick -> onAccountClick(uiEvent.contact)
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
        object OnCallQueryRelatedContactsByPhoneUseCaseImp : UIEvent()
        data class OnAccountClick(val contact: RelatedContact?) : UIEvent()
    }
}
