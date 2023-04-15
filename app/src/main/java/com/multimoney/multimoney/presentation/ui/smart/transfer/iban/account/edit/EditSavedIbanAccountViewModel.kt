package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationUpdateACHAccountUseCase
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteGetUseCase
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ACCOUNT_ID
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class EditSavedIbanAccountViewModel @Inject constructor(
    private val queryACHTransferFavoriteGetUseCase: QueryACHTransferFavoriteGetUseCase,
    private val mutationUpdateACHAccountUseCase: MutationUpdateACHAccountUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String?
    private var idBrand: Int?
    private var identification: String?
    private var accountId: Int?
    private var idClient: String = ""
    private var smartAccount: SmartAccountID? = null

    init {
        user = savedStateHandle[USER]
        idBrand = savedStateHandle[ID_BRAND]
        identification = savedStateHandle[IDENTIFICATION]
        accountId = savedStateHandle[ACCOUNT_ID]
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        smartAccount = savedStateHandle[SMART_ACCOUNT]
    }

    private fun getIbanAccount() = executeUseCase {
        uiState = uiState.copy(validationFinish = false)
        queryACHTransferFavoriteGetUseCase.invoke(
            user.orEmpty(),
            idBrand ?: Brand.CostaRica.id,
            accountId ?: 0
        ).collectLatest { result ->
            result.onSuccess { achAccountFull ->
                uiState = uiState.copy(
                    isLoading = false,
                    validationFinish = true,
                    titularName = achAccountFull?.titularName.orEmpty(),
                    documentNumber = achAccountFull?.identificacionNumber.orEmpty(),
                    accountNumber = achAccountFull?.accountNumber
                        ?.replace(Brand.CostaRica.iban, "")
                        .orEmpty(),
                    nickname = achAccountFull?.description.orEmpty()
                )
            }
            result.onFailure { onFailure(it) }
            result.onLoading { uiState = uiState.copy(isLoading = true) }
        }
    }

    private fun onNicknameValueChange(nickname: String) {
        uiState = uiState.copy(
            nickname = nickname,
            isButtonEnabled = nickname.isNotEmpty()
        )
    }

    private fun saveChanges() = executeUseCase {
        mutationUpdateACHAccountUseCase.invoke(
            user = user.orEmpty(),
            idBrand = idBrand ?: Brand.CostaRica.id,
            description = uiState.nickname,
            accountId = accountId ?: 0,
            titularName = uiState.titularName,
            identification = uiState.documentNumber.replace(DASH,""),
            accountNumber = uiState.accountNumber
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
                onNavigateToAccountsList()
            }
            result.onFailure { onFailure(it) }
            result.onLoading { uiState = uiState.copy(isLoading = true) }
        }
    }

    private fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartTransferIbanAccountScreen.route, isRestart = false)
    }

    private fun onNavigateToAccountsList() {
        popAndNavigateTo(
            popTo = Screen.SmartEditSavedIbanAccount.route,
            route = Screen.SmartTransferIbanAccountScreen.baseRoute
                .plus("/${encodeData(smartAccount)}")
                .plus("$user")
                .plus("/$idBrand")
                .plus("/$identification")
                .plus("/${Screen.HomeScreen.route}")
                .plus("/$idClient")
                .plus("/${true}")
        )
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

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        val titularName: String = "",
        val documentNumber: String = "",
        val accountNumber: String = "",
        val nickname: String = "",
        val validationFinish: Boolean = false,
        val isButtonEnabled: Boolean = false,
        var isLoading: Boolean = false
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnGetAccountInformation -> getIbanAccount()
            is UIEvent.OnNicknameChange -> onNicknameValueChange(uiEvent.nickname)
            is UIEvent.OnSaveButtonClick -> saveChanges()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnGetAccountInformation : UIEvent()
        object OnSaveButtonClick : UIEvent()
        data class OnNicknameChange(val nickname: String) : UIEvent()
    }

    companion object {
        const val DASH = "-"
    }
}