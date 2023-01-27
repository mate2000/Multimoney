package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QueryRelatedContactsByPhoneUseCase
import com.multimoney.domain.model.accountsmart.PhoneSmart
import com.multimoney.domain.model.accountsmart.RelatedContact
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CONTACTS
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnAddSACAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnAddToFavoriteAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnCallQueryRelatedContactsByPhoneUseCase
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnContactClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnQueryValueChange
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.SortedMap
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class MyContactsTransferViewModel @Inject constructor(
    private val queryRelatedContactsByPhoneUseCase: QueryRelatedContactsByPhoneUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var selectedSmartAccount: SmartAccountID? = null
    private var user: String = ""
    var idBrand: Int = 0
    var relatedContacts: List<RelatedContact> = listOf()

    init {
        selectedSmartAccount = savedStateHandle[SMART_ACCOUNT]
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        relatedContacts =
            savedStateHandle.get<Array<RelatedContact>>(CONTACTS)?.toList() ?: listOf()
    }

    private fun callQueryRelatedContactsByPhoneUseCaseImp() = executeUseCase {
        queryRelatedContactsByPhoneUseCase.invoke(
            user = user,
            idBrand = idBrand,
            contacts = relatedContacts
        ).collectLatest { result ->
            result.onSuccess { accountList ->
                uiState = uiState.copy(isLoading = false)
                accountList?.phones?.let { phoneSmarts ->
                    uiState = uiState.copy(
                        relatedContactList = phoneSmarts.groupBy { phoneSmart ->
                            phoneSmart?.identification.orEmpty()
                        }.toSortedMap()
                    )
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
        Log.d("contacttransfer", "Navigate To Add To Favorite")
    }

    private fun onQueryValueChange(value: String) {
        uiState = uiState.copy(
            queryValue = value
        )
    }

    private fun onContactClick(accounts: List<PhoneSmart?>) {
        if (idBrand == Brand.ElSalvador.id) {
            accounts.first()?.let { onAccountClick(it) }
        } else {
            uiState = uiState.copy(
                selectedContact = accounts.filterNotNull(),
                bottomSheetState = ModalBottomSheetState(Expanded)
            )
        }
    }

    private fun onAccountClick(account: PhoneSmart) {
        // todo Add navigation
        Log.d("contacttransfer", "On Account Click")
    }

    private fun onAddSACAccountClick() {
        navigateTo(
            "${Screen.SmartAddSACAccountScreen.baseRoute}/$idBrand/$user/${
            encodeData(selectedSmartAccount)
            }/${SmartTransferTypes.SmartToSmart.id}"
        )
    }

    data class UIState(
        val queryValue: String = "",
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var relatedContactList: SortedMap<String, List<PhoneSmart?>> = sortedMapOf(),
        var selectedContact: List<PhoneSmart> = listOf(),
        val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(Hidden)
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnAddToFavoriteAccountClick -> navigateToAddToFavoriteAccount(uiEvent.contactToFavorite)
            is OnNavigateBack -> onNavigateBack()
            is OnAddSACAccountClick -> onAddSACAccountClick()
            is OnQueryValueChange -> onQueryValueChange(uiEvent.value)
            is OnNavigateToHome -> onNavigateToHome()
            is OnCallQueryRelatedContactsByPhoneUseCase -> callQueryRelatedContactsByPhoneUseCaseImp()
            is OnContactClick -> onContactClick(uiEvent.contact)
            is OnAccountClick -> onAccountClick(uiEvent.account)
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
        object OnCallQueryRelatedContactsByPhoneUseCase : UIEvent()
        data class OnQueryValueChange(val value: String) : UIEvent()
        data class OnContactClick(val contact: List<PhoneSmart?>) : UIEvent()
        data class OnAccountClick(val account: PhoneSmart) : UIEvent()
    }
}
