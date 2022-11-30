package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.model.security.UserData
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnNavigateToEditPhone -> navigateToEditPhone()
        }
    }

    private fun navigateToEditPhone(){
        navigateTo("${Screen.ChangePhoneScreen.baseRoute}/${uiState.idBrand}/${uiState.phoneNumber}")
    }

    data class UIState(
        // Fields
        val userData: UserData? = null,
        val userName: String = "",
        val userEmail: String = "",
        val phoneNumber: String? = null,
        val idBrand: Int? = null,
    )

    var uiState by mutableStateOf(UIState())

    init {
        uiState = uiState.copy(
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            idBrand = savedStateHandle[ID_BRAND]
        )
    }


    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateToEditPhone : UIEvent()

    }

}
