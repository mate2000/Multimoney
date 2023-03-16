package com.multimoney.multimoney.presentation.ui.credit.origination.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.account.CrosselingAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.credit.origination.account.CrosselingAccountViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.account.CrosselingAccountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class CrosselingAccountViewModel @Inject constructor(
    private val queryListSinpeAccountUseCase: QueryListSinpeAccountUseCase,
    private val queryBanksAndRegularExpressionUseCase: QueryBanksAndRegularExpressionUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    private var identification: String = ""
    private var email: String = ""
    private var bank: CreditCatalog? = null
    private var bankList: List<CreditCatalogOption?>? = listOf()

    private fun onStart(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        identification: String,
        country: String,
        idAccount: Long,
        accountNumber: String,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit,
        onSuccess: (Boolean) -> Unit
    ) {
        this.email = user
        this.idBrand = idBrand
        this.identification = identification

        getTextResources()

        getFullSinpeAccountList(
            user = user,
            identification = identification,
            idBrand = idBrand,
            country = country,
            idAccount = idAccount,
            accountNumber = accountNumber,
            onSuccess = onSuccess,
            onLoadingValueChange = onLoadingValueChange,
            onFailureWithDialog = onFailureWithDialog
        )

        if (idBrand == Brand.ElSalvador.id) {
            onCallQueryBanksAndRegularExpressions(
                pkUser = pkUser,
                user = user,
                idBrand = idBrand,
                idUserRequest = idUserRequest,
                onLoadingValueChange = onLoadingValueChange,
                onFailureWithDialog = onFailureWithDialog
            )
        }
    }

    private fun getFullSinpeAccountList(
        user: String,
        identification: String,
        idBrand: Int,
        country: String,
        idAccount: Long,
        accountNumber: String,
        onSuccess: (Boolean) -> Unit,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        // Reset state
        uiState = uiState.copy(clientBankAccountList = listOf())

        // Call favorites accounts
        onCallQueryListSinpeAccount(
            user,
            identification,
            idBrand,
            country,
            idAccount,
            accountNumber,
            true,
            onSuccess,
            onLoadingValueChange,
            onFailureWithDialog
        )

        // Call non favorites accounts
        onCallQueryListSinpeAccount(
            user,
            identification,
            idBrand,
            country,
            idAccount,
            accountNumber,
            false,
            onSuccess,
            onLoadingValueChange,
            onFailureWithDialog
        )
    }

    private fun onCallQueryListSinpeAccount(
        user: String,
        identification: String,
        idBrand: Int,
        country: String,
        idAccount: Long,
        accountNumber: String,
        isFavorite: Boolean,
        onSuccess: (Boolean) -> Unit,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryListSinpeAccountUseCase.invoke(
            user = user,
            identification = identification,
            idBrand = idBrand,
            country = country,
            idAccount = idAccount,
            accountNumber = accountNumber,
            option = CROSSELING,
            isFavorite = isFavorite
        ).collectLatest { result ->
            result.onSuccess { accountList ->
                onLoadingValueChange(false)
                onSuccess(accountList?.data?.isEmpty() == true)
                accountList?.data?.let {
                    uiState = uiState.copy(
                        clientBankAccountList = uiState.clientBankAccountList + it
                    )
                }
            }
            result.onFailure {
                onLoadingValueChange(false)
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }
            result.onLoading {
                onLoadingValueChange(true)
            }
        }
    }

    private fun onCallQueryBanksAndRegularExpressions(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryBanksAndRegularExpressionUseCase.invoke(pkUser, user, idBrand, idUserRequest).collectLatest { result ->
            result.onSuccess {
                bank = it.banks?.first()
                bankList = bank?.subOptions?.filter { filter ->
                    filter?.description != MIDDLE_DASH
                }
            }.onFailure {
                onFailureWithDialog(
                    false,
                    DialogParameters(
                        description = it.getError().toString(),
                        isActive = mutableStateOf(true)
                    )
                )
            }
        }
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = when (idBrand) {
                Brand.CostaRica.id -> R.string.crosseling_account_cr_title
                else -> R.string.crosseling_account_sv_title
            }
        )
    }

    private fun onClientBankAccountSelected(clientBankAccount: SinpeAccount?, onContinueClick: () -> Unit) {
        uiState = uiState.copy(
            clientBankAccountSelected = clientBankAccount,
            accountTypeSelectedString = clientBankAccount?.accountType.toString(),
            accountNumber = clientBankAccount?.sinpeAccount ?: ""
        )
        onContinueClick()
    }

    private fun onNextActionClick(
        user: String,
        nextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) {
        if (idBrand == Brand.CostaRica.id) {
            saveCreditStepsHelper.saveStepOneCR(
                user,
                uiState.accountNumber
            )
        } else {
            saveCreditStepsHelper.saveStepOneCrosselingSv(
                user,
                bank,
                uiState.clientBankAccountSelected?.bank,
                uiState.clientBankAccountSelected?.idBancoCore.toString(),
                uiState.clientBankAccountSelected?.regularExpression ?: "",
                uiState.clientBankAccountSelected?.accountTypeCore?.toString().orEmpty(),
                uiState.accountNumber,
            )
        }
        nextStepAction()
    }

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val clientBankAccountList: List<SinpeAccount?> = listOf(),
        val clientBankAccountSelected: SinpeAccount? = null,
        val accountTypeSelectedString: String = "",
        val accountNumber: String = "",
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnStart -> onStart(
                uiEvent.pkUser,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.idUserRequest,
                uiEvent.identification,
                uiEvent.country,
                uiEvent.idAccount,
                uiEvent.accountNumber,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog,
                uiEvent.onSuccess
            )
            is OnNextActionClick -> onNextActionClick(
                uiEvent.user,
                uiEvent.nextStepAction,
                uiEvent.saveCreditStepsHelper
            )
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.clientBankAccount, uiEvent.onContinueClick)
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnStart(
            val pkUser: Int,
            val user: String,
            val idBrand: Int,
            val idUserRequest: Int,
            val identification: String,
            val country: String,
            val idAccount: Long,
            val accountNumber: String,
            val onLoadingValueChange: (isLoading: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit,
            val onSuccess: (Boolean) -> Unit
        ) : UIEvent()

        class OnClientBankAccountSelected(
            val clientBankAccount: SinpeAccount?,
            val onContinueClick: () -> Unit
        ) : UIEvent()
    }

    companion object {
        const val MIDDLE_DASH = "-"
        const val CROSSELING = "CROSSELING"
    }
}
