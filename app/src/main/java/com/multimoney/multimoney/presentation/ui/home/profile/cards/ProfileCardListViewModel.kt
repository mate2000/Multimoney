package com.multimoney.multimoney.presentation.ui.home.profile.cards

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.virtualcard.MutationDeleteCardVDUseCase
import com.multimoney.domain.interaction.virtualcard.QueryListCardVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PROFILE_CARD_LIST_ORIGIN
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnCallQueryGetClientCards
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnCardThreePointsSelected
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnDeleteCard
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnDeleteCardShowToast
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnEditCard
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnEditCardShowToast
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnHideCardListBottomSheet
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnHideToast
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.home.profile.cards.ProfileCardListViewModel.UIEvent.OnNavigateToVerifyCard
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.ProfileCardListOrigin
import com.multimoney.multimoney.presentation.util.getNavParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class ProfileCardListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListCardVDUseCase: QueryListCardVDUseCase,
    private val mutationDeleteCardVDUseCase: MutationDeleteCardVDUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String? = null
    private var origin: String = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        origin = savedStateHandle[PROFILE_CARD_LIST_ORIGIN] ?: ProfileCardListOrigin.Profile.value
    }

    private fun onCallQueryGetClientCardsUseCase(fromDelete: Boolean) {
        executeUseCase {
            queryListCardVDUseCase.invoke(
                user = user,
                idBrand = idBrand,
                identification = identification ?: ""
            ).collectLatest { result ->
                result.onSuccess { cardsList ->
                    uiState = uiState.copy(
                        isLoading = false,
                        cardVDListVerified = cardsList?.filter { it?.verified == true },
                        cardVDListNotVerified = cardsList?.filter { it?.verified != true },
                        isCardListEmpty = cardsList.isNullOrEmpty()
                    )
                    if (fromDelete) {
                        onDeleteCardShowToast()
                    }
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        deleteDialogIsVisible = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        ),
                        cardVDListVerified = listOf(),
                        cardVDListNotVerified = listOf(),
                        isCardListEmpty = true
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun onCallMutationDeleteCardVD(idCard: Long) =
        executeUseCase {
            mutationDeleteCardVDUseCase.invoke(
                identification = identification.orEmpty(),
                user = user,
                idBrand = idBrand,
                idCard = idCard
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(isLoading = false)
                    if (it?.isApproved.toBoolean()) {
                        onCallQueryGetClientCardsUseCase(fromDelete = true)
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            deleteDialogIsVisible = false,
                            openDialog = DialogParameters(
                                description = it?.messageError?.message ?: "",
                                isActive = mutableStateOf(true)
                            )
                        )
                    }
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        deleteDialogIsVisible = false,
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

    private fun onCardThreePointsSelected(cardSelected: CardVisaDirect?) {
        uiState = uiState.copy(
            cardVDSelected = cardSelected,
            bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
        )
    }

    private fun onDeleteCard(card: CardVisaDirect?) {
        uiState = uiState.copy(
            deleteDialogIsVisible = true,
            openDialog = DialogParameters(
                titleResource = R.string.profile_my_cards_delete_card_dialog_title,
                descriptionResource = R.string.profile_my_cards_delete_card_dialog_description,
                positiveResource = R.string.common_remove,
                negativeResource = R.string.cancel,
                positiveAction = { onCallMutationDeleteCardVD(card?.idCard?.toLong() ?: 0) },
                negativeAction = {
                    uiState = uiState.copy(
                        bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
                    )
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onEditCard(card: CardVisaDirect?) {
        navigateTo(
            route = "${Screen.ProfileMyCardsEditCardScreen.baseRoute}/$identification/$user/$idBrand/${
            encodeData(
                card
            )
            }"
        )
    }

    private fun onEditCardShowToast() {
        uiState = uiState.copy(
            toastIsVisible = true,
            toastMessage = R.string.profile_my_cards_edit_card_toast_result_success
        )
    }

    private fun onDeleteCardShowToast() {
        uiState = uiState.copy(
            toastIsVisible = true,
            toastMessage = R.string.profile_my_cards_delete_card_toast_result_success
        )
    }

    private fun onNavigateBack() {
        if (origin == ProfileCardListOrigin.Profile.value) {
            navigateBack(popTo = Screen.ProfileScreen.route, isRestart = false)
        } else {
            navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)
        }
    }

    private fun onHideCardListBottomSheet() {
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
    }

    private fun onNavigateToVerifyCard() = navigateTo(
        route = Screen.VisaVerifyInformationScreen.baseRoute
            .plus(
                getNavParam(IDENTIFICATION, identification)
            )
            .plus(
                getNavParam(ID_CARD, uiState.cardSelected?.idCard ?: "")
            )
            .plus(
                getNavParam(USER, user)
            )
            .plus(
                getNavParam(ID_BRAND, idBrand)
            )
            .plus(
                getNavParam(PREVIOUS_SCREEN, Screen.ProfileCardListScreen.baseRoute)
            )
    )

    data class UIState(
        // Interactions
        val cardVDListVerified: List<CardVisaDirect?>? = null,
        val cardVDListNotVerified: List<CardVisaDirect?>? = null,
        val isCardListEmpty: Boolean = true,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isVisaAnimationVisible: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        val cardVDSelected: CardVisaDirect? = null,
        val toastIsVisible: Boolean = false,
        val toastMessage: Int = R.string.empty,
        val deleteDialogIsVisible: Boolean = false,
        val cardSelected: CardVisaDirect? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnCallQueryGetClientCards -> onCallQueryGetClientCardsUseCase(fromDelete = false)
            is OnCardThreePointsSelected -> onCardThreePointsSelected(uiEvent.cardSelected)
            is OnEditCard -> onEditCard(uiEvent.card)
            is OnDeleteCard -> onDeleteCard(uiEvent.card)
            is OnEditCardShowToast -> onEditCardShowToast()
            is OnDeleteCardShowToast -> onDeleteCardShowToast()
            is OnHideToast -> uiState = uiState.copy(
                toastIsVisible = false
            )
            is OnHideCardListBottomSheet -> onHideCardListBottomSheet()
            is OnNavigateToVerifyCard -> onNavigateToVerifyCard()
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetClientCards : UIEvent()

        class OnCardThreePointsSelected(val cardSelected: CardVisaDirect?) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnEditCardShowToast : UIEvent()
        object OnDeleteCardShowToast : UIEvent()
        object OnHideToast : UIEvent()
        data class OnEditCard(val card: CardVisaDirect?) : UIEvent()
        data class OnDeleteCard(val card: CardVisaDirect?) : UIEvent()
        object OnNavigateToVerifyCard : UIEvent()
        object OnHideCardListBottomSheet : UIEvent()
    }
}
