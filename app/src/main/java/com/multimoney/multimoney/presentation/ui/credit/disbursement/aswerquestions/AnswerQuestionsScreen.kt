package com.multimoney.multimoney.presentation.ui.credit.disbursement.aswerquestions

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel
import com.multimoney.multimoney.presentation.uielement.*
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import com.multimoney.multimoney.presentation.util.transformation.formatMoney

@Composable
fun AnswerQuestionsScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: AnswerQuestionsScreenViewModel = hiltViewModel()
) {
    // View

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var title = R.string.empty
    if (viewModel.idBrand.isNotEmpty()) {
        title = when (viewModel.idBrand.toInt()) {
            Brand.Guatemala.id -> R.string.disbursement_answer_questions_gt
            else -> R.string.smart_account_document_title
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)

    ) {
        Column {
            TopBar(onBackClick = {
                viewModel.onUIEvent(AnswerQuestionsScreenViewModel.UIEvent.OnBackClick(focusManager))
            } )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Title(title = title)
                BankDestiny(
                    items = viewModel.uiState.bankList,
                    value = viewModel.uiState.bankSelected,
                    onValueChange = {
                        viewModel.onUIEvent(
                            AnswerQuestionsScreenViewModel.UIEvent.OnBankValueChanged(
                                it
                            )
                        )
                    }
                )
                AccountType(
                    items = viewModel.uiState.accountTypeListFiltered?.map { it?.description ?: "" }
                        ?: listOf(),
                    value = viewModel.uiState.accountTypeSelectedString,
                    onValueChange = { value ->
                        viewModel.onUIEvent(
                            AnswerQuestionsScreenViewModel.UIEvent.OnAccountTypeValueChanged(
                                viewModel.uiState.accountTypeListFiltered?.findLast { it?.description == value })
                        )
                    })
                AccountNumber(
                    value = viewModel.uiState.accountNumber,
                    onValueChange = {
                        viewModel.onUIEvent(
                            AnswerQuestionsScreenViewModel.UIEvent.OnAccountNumberValueChange(
                                it
                            )
                        )
                    },
                    onError = viewModel.uiState.accountNumberError,
                    focusManager = focusManager
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Continue(
                enable = viewModel.uiState.isContinueEnabled,
                onClick = {  }
            )
            LoadingIndicator(viewModel.uiState.isLoading)
            ErrorDialog(viewModel.uiState.openDialog)
        }
    }

}

@Composable
private fun ErrorDialog(openDialog: DialogParameters) {
    if (openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = openDialog.titleResource),
            message = openDialog.description,
            positiveButtonText = stringResource(id = openDialog.positiveResource),
            negativeButtonText = stringResource(id = openDialog.negativeResource),
            openDialogCustom = openDialog.isActive,
            onPositiveAction = openDialog.positiveAction
        )
    }
}

@Composable
private fun Continue(enable: Boolean, onClick: () -> Unit) {
    CustomButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        onClick = onClick,
        buttonType = CustomButtonType.PrimaryPrimary,
        text = stringResource(id = R.string.button_continue),
        enable = enable
    )
}

@Composable
private fun MonthlyIncome(
    value: String,
    onValueChange: (String) -> Unit,
    onError: Pair<Boolean, Int>,
    focusManager: FocusManager
) {
    CustomOutlinedTextField(
        modifier = Modifier.padding(top = 32.dp),
        value = viewModel.uiState.income,
        leadingIcon = R.drawable.ic_money_gray,
        placeHolder = stringResource(
            id = R.string.credit_monthly_income_income_hint,
            sharedViewModel.currencySymbol.ifEmpty {
                stringResource(id = sharedViewModel.idBrand.toInt().getCurrencySymbol())
            }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.clearFocus()
        }),
        isRequiredMessage = stringResource(id = R.string.credit_monthly_income_required_income),
        onValueChange = {
            viewModel.onUIEvent(
                MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange(
                    it
                )
            )
        },
        isError = viewModel.uiState.incomeError.first,
        errorMessage = stringResource(id = viewModel.uiState.incomeError.second),
        customTransformation = formatMoney(
            sharedViewModel.currencySymbol.ifEmpty {
                stringResource(id = sharedViewModel.idBrand.toInt().getCurrencySymbol())
            }
        )
    )
}

@Composable
private fun LaborSituation(
    items: List<CreditCatalogOption?>?,
    value: CreditCatalogOption?,
    onValueChange: (CreditCatalogOption?) -> Unit
) {
    CustomDropdown(
        modifier = Modifier
            .padding(top = 16.dp)
            .wrapContentSize(Alignment.TopStart)
            .focusable(false),
        items = items,
        value = value,
        onValueChange = onValueChange,
        labelText = stringResource(id = R.string.credit_bank_account_destiny),
        placeHolder = stringResource(id = R.string.select)
    )
}

@Composable
private fun BirthDay(
    context: Context,
    focusManager: FocusManager,
    value: String,
    onValueChange: (String) -> Unit
){
    CustomDatePicker(
        context = context,
        modifier = Modifier.padding(top = 16.dp),
        labelText = stringResource(id = R.string.credit_job_joined_date),
        placeHolder = stringResource(id = R.string.credit_job_date_placeholder),
        value = viewModel.uiState.date,
        minYear = JobInfoViewModel.JOB_DATE_MIN_YEAR,
        minMonth = JobInfoViewModel.JOB_DATE_MIN_MONTH,
        minDay = JobInfoViewModel.JOB_DATE_MIN_DAY,
        leadingIcon = R.drawable.ic_calendar_voucher,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.clearFocus()
        }),
        onValueChange = { _, year, month, dayOfMonth ->
            viewModel.onUIEvent(
                JobInfoViewModel.UIEvent.OnDateValueChange(
                    getPickedDateAsString(
                        year,
                        month,
                        dayOfMonth,
                        JobInfoViewModel.DATE_FORMAT
                    )
                )
            )
        }
    )
}

@Composable
private fun Title(title: Int) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = Typography.h6.toSpanStyle()
                    .copy(
                        color = MultimoneyTheme.colors.text,
                        fontWeight = FontWeight.SemiBold
                    )
            ) {
                append(stringResource(id = title))
            }
        },
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit
) {
    TopNavBar(
        isLeftButtonVisible = true,
        isRightButtonVisible = false,
        onLeftButtonClick = onBackClick
    )
}

