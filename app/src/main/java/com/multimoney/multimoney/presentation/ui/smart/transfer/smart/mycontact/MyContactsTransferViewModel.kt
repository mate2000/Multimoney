package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationUpdateFavoriteSmartUseCase
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
import com.multimoney.multimoney.presentation.navigation.Screen.MyContactsTransferAmountScreen
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
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact.MyContactsTransferViewModel.UIEvent.OnSelectContactAsFavorite
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.getCurrencyFromValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class MyContactsTransferViewModel @Inject constructor(
    private val mutationUpdateFavoriteSmartUseCase: MutationUpdateFavoriteSmartUseCase,
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
    var smartAccount: SmartAccountID? = null
    var contactAccountSelected: PhoneSmart? = null

    init {
        selectedSmartAccount = savedStateHandle[SMART_ACCOUNT]
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        smartAccount = savedStateHandle[SMART_ACCOUNT]
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
                        relatedContactList = phoneSmarts.sortedBy { phoneSmart ->
                            phoneSmart?.titular.orEmpty()
                        }.groupBy { phone -> phone?.identification.orEmpty() }
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

    private fun onNavigateBack() {
        if (uiState.contactClickBottomSheetState.isVisible || uiState.selectFavoriteContactBottomSheetState.isVisible) {
            uiState = uiState.copy(
                contactClickBottomSheetState = ModalBottomSheetState(Hidden),
                selectFavoriteContactBottomSheetState = ModalBottomSheetState(Hidden)
            )
        } else {
            navigateBack(popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false)
        }
    }

    private fun navigateToAddToFavoriteAccount(contactToFavorite: PhoneSmart) {
        contactAccountSelected = contactToFavorite
        uiState = if (uiState.selectFavoriteContactBottomSheetState.isVisible) {
            uiState.copy(
                selectFavoriteContactBottomSheetState = ModalBottomSheetState(Hidden)
            )
        } else {
            uiState.copy(
                selectFavoriteContactBottomSheetState = ModalBottomSheetState(Expanded)
            )
        }
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
                contactClickBottomSheetState = ModalBottomSheetState(Expanded)
            )
        }
    }

    private fun onAccountClick(account: PhoneSmart) {
        navigateTo(
            "${MyContactsTransferAmountScreen.baseRoute}/" +
                "${encodeData(smartAccount)}/${encodeData(account)}/" +
                "${SmartTransferTypes.SmartToContact.id}/$idBrand/" +
                Screen.MyContactsTransferScreen.baseRoute
        )
    }

    private fun onAddSACAccountClick() {
        navigateTo(
            "${Screen.SmartAddSACAccountScreen.baseRoute}/$idBrand/$user/${
            encodeData(selectedSmartAccount)
            }/${SmartTransferTypes.SmartToSmart.id}"
        )
    }

    private fun onSelectContactAsFavorite() {
        executeUseCase {
            mutationUpdateFavoriteSmartUseCase.invoke(
                idBrand = idBrand,
                user = user,
                idFavorite = 0,
                idAccountType = null,
                idCustomer = selectedSmartAccount?.customerId ?: 0L,
                accountNumber = contactAccountSelected?.accountNumber ?: "",
                accountName = contactAccountSelected?.titular,
                email = contactAccountSelected?.email ?: "",
                active = true,
                isFavorite = true,
                phoneNumber = contactAccountSelected?.number,
                idCurrencyAccount = contactAccountSelected?.currency?.getCurrencyFromValue()?.id
                    ?: 0
            ).collectLatest { result ->
                result.onLoading { uiState = uiState.copy(isLoading = true) }
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
                }
                result.onFailure { onFailure(it) }
            }
        }
    }

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true
        )
    }

    data class UIState(
        val queryValue: String = "",
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var relatedContactList: Map<String, List<PhoneSmart?>> = mapOf(),
        var selectedContact: List<PhoneSmart> = listOf(),
        val selectFavoriteContactBottomSheetState: ModalBottomSheetState = ModalBottomSheetState(
            Hidden
        ),
        val contactClickBottomSheetState: ModalBottomSheetState = ModalBottomSheetState(Hidden)
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnAddToFavoriteAccountClick -> navigateToAddToFavoriteAccount(uiEvent.contactToFavorite)
            is OnNavigateBack -> onNavigateBack()
            is OnSelectContactAsFavorite -> onSelectContactAsFavorite()
            is OnAddSACAccountClick -> onAddSACAccountClick()
            is OnQueryValueChange -> onQueryValueChange(uiEvent.value)
            is OnNavigateToHome -> onNavigateToHome()
            is OnCallQueryRelatedContactsByPhoneUseCase -> callQueryRelatedContactsByPhoneUseCaseImp()
            is OnContactClick -> onContactClick(uiEvent.contact)
            is OnAccountClick -> onAccountClick(uiEvent.account)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnSelectContactAsFavorite : UIEvent()
        object OnNavigateToHome : UIEvent()
        object OnAddSACAccountClick : UIEvent()
        data class OnAddToFavoriteAccountClick(val contactToFavorite: PhoneSmart) : UIEvent()
        object OnCallQueryRelatedContactsByPhoneUseCase : UIEvent()
        data class OnQueryValueChange(val value: String) : UIEvent()
        data class OnContactClick(val contact: List<PhoneSmart?>) : UIEvent()
        data class OnAccountClick(val account: PhoneSmart) : UIEvent()
    }
}
