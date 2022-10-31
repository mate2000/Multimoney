import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps.Five
import com.multimoney.data.util.catalog.SmartSteps.Search
import com.multimoney.data.util.catalog.SmartSteps.Three
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis.OwnBusinessOnPersonalBasis
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis.OwnBusinessOnPersonalBasis.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis.OwnBusinessOnPersonalBasis.UIEvent.OnIdentificationChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis.OwnBusinessOnPersonalBasis.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType.MainSourceIncomeScreenType
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.transformation.formatBusinessIdentification
import com.multimoney.multimoney.presentation.util.transformation.formatDecimalMoney

@Composable
fun OwnBusinessOnPersonalBasisScreen(
    viewModel: OwnBusinessOnPersonalBasis = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
    sourceIncomeSharedViewModel: SourceIncomeViewModel = hiltViewModel()
) {
    val currencySymbol = sharedViewModel.idBrand.toIntOrNull()?.getCurrencySymbol()
        ?.let { stringResource(it) } ?: "$"

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueVisible(true))
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(
                        OnNextActionClick(
                            nextStepAction = {
                                sharedViewModel.onUIEvent(
                                    OnCallMutationUpdateGlobalRequestUseCase(
                                        // FIXME, pass whatever needed and obtain it from the uiState variable
                                        accountSmartData = sharedViewModel.accountSmartData?.copy(
                                            status = 1,
                                            currentStep = Search.getNameById(
                                                sharedViewModel.uiState.currentStep
                                            ),
                                            income = viewModel.uiState.businessIncome.toFloat(),
                                            legalID = viewModel.uiState.businessIdentification,
                                            entrepreneurship = viewModel.uiState.businessActivity
                                        )
                                    )
                                )
                            }
                        )
                    )
                },
                // TODO check for correct steps
                nextStep = Five.id,
                previousStep = Three.id
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        CustomOutlinedTextField(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            value = viewModel.uiState.businessActivity,
            onValueChange = {
                viewModel.onUIEvent(
                    OwnBusinessOnPersonalBasis.UIEvent.OnBusinessActivityChange(
                        it
                    )
                )
            },
            labelText = stringResource(R.string.smart_business_personal_basis_activity_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onDone = {
            }),
            isTextArea = true,
            isError = viewModel.uiState.activityError.first,
            errorMessage = stringResource(viewModel.uiState.activityError.second),
            isRequired = true
        )

        CustomOutlinedTextField(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            value = viewModel.uiState.businessIncome,
            onValueChange = {
                viewModel.onUIEvent(
                    OwnBusinessOnPersonalBasis.UIEvent.OnIncomeAmountChange(
                        it
                    )
                )
            },
            labelText = stringResource(R.string.credit_monthly_income_income_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
            }),
            placeHolder = stringResource(
                R.string.decimal_income_placeholder,
                currencySymbol
            ),
            leadingIcon = R.drawable.ic_money_gray,
            customTransformation = formatDecimalMoney(currencySymbol),
            isError = viewModel.uiState.incomeError.first,
            errorMessage = stringResource(viewModel.uiState.incomeError.second)
        )

        CustomOutlinedTextField(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            value = viewModel.uiState.businessIdentification,
            onValueChange = {
                viewModel.onUIEvent(
                    OnIdentificationChange(
                        it,
                        sharedViewModel.idBrand.toInt(),
                        sharedViewModel.user
                    )
                )
            },
            labelText = stringResource(R.string.smart_business_personal_basis_identification_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
            }),
            placeHolder = stringResource(R.string.smart_business_personal_basis_identification_placeholder),
            customTransformation = formatBusinessIdentification(),
            isError = viewModel.uiState.identificationError.first,
            errorMessage = viewModel.uiState.identificationError.second,
            showInfo = viewModel.uiState.identificationLoading.first,
            infoMessage = stringResource(viewModel.uiState.identificationLoading.second),
            isSuccess = viewModel.uiState.identificationSuccess.first,
            successMessage = stringResource(viewModel.uiState.identificationSuccess.second)
        )
        Text(
            text = viewModel.uiState.companyName,
            color = MultimoneyTheme.colors.text,
            modifier = Modifier
                .padding(start = 5.dp)
                .wrapContentSize(),
            style = Typography.caption
        )
    }

    BackHandler {
        sourceIncomeSharedViewModel.onUIEvent(
            OnNavigateToSelectedSourceOfIncomeOption(
                MainSourceIncomeScreenType.id
            )
        )
    }
}
