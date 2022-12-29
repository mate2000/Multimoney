package com.multimoney.multimoney.presentation.ui.crypto.movements

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.crypto.GetCryptoCurrencyMovementsUseCase
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CryptoMovementsScreenViewModel  @Inject constructor(
    private val queryGetCryptoCurrencyMovementsUseCase: GetCryptoCurrencyMovementsUseCase,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun setUserData() {
        uiState = uiState.copy(
            user = savedStateHandle[USER],
            idBrand = savedStateHandle[ID_BRAND],
            identification = savedStateHandle[IDENTIFICATION]
        )
    }

    data class UIState(
        val user: String? = null,
        val idBrand: Int? = null,
        val identification: String? = null
    )
}