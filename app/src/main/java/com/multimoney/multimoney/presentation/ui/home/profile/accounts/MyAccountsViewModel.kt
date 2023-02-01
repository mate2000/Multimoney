package com.multimoney.multimoney.presentation.ui.home.profile.accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MyAccountsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListSinpeAccountUseCase: QueryListSinpeAccountUseCase,
    private val dataStorePreferences: DataStorePreferences,

    ) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String = ""

    init {
        user = savedStateHandle[USER_NAME] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        callListSinpeAccountUseCase()
    }


    private fun callListSinpeAccountUseCase() = executeUseCase {
        queryListSinpeAccountUseCase.invoke(
            user = user,
            identification = identification,
            idBrand = idBrand,
            country = "",
            idAccount = 0,
            accountNumber = "",
        ).collectLatest { result ->
            result.onSuccess { accounts ->
                if (accounts != null) {
                    uiState =
                        uiState.copy(
                            favoriteAccounts = accounts.data.filter { it?.isFavorite == true },
                            registeredAccounts = accounts.data.filter { it?.isFavorite == false })
                }
            }.onFailure {
            }.onLoading {
            }
        }
    }

    data class UIState(
        // Fields
        val favoriteAccounts: List<SinpeAccount?> = listOf(),
        val registeredAccounts: List<SinpeAccount?> = listOf()
    )
}
