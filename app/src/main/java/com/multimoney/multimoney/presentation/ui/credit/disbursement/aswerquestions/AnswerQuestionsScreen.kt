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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.*
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import com.multimoney.multimoney.presentation.util.transformation.formatDecimalMoney

@Composable
fun AnswerQuestionsScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: AnswerQuestionsViewModel = hiltViewModel()
) {

    // Properties

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Navigation

    LaunchedEffect(true) {
        //viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
        viewModel.onUIEvent(AnswerQuestionsViewModel.UIEvent.OnCallQueryEmploymentSituation)
    }

    // View

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)

    ) {
        Column {
            TopBar(onBackClick = {
                viewModel.onUIEvent(AnswerQuestionsViewModel.UIEvent.OnBackClick(focusManager))
            })
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Title(title = viewModel.uiState.titleResource)
                BirthDay(
                    context = context,
                    focusManager = focusManager,
                    value = viewModel.uiState.birthDate,
                    minYear = AnswerQuestionsViewModel.BIRTH_DATE_MIN_YEAR,
                    minMonth = AnswerQuestionsViewModel.BIRTH_DATE_MIN_MONTH,
                    minDay = AnswerQuestionsViewModel.BIRTH_DATE_MIN_DAY,
                    onValueChange = {
                        viewModel.onUIEvent(
                            AnswerQuestionsViewModel.UIEvent.OnBirthDateValueChange(it)
                        )
                    },
                    onError = viewModel.uiState.birthDateError
                )
                MonthlyIncome(
                    value = viewModel.uiState.paymentAmount,
                    currencySymbol = viewModel.idBrand?.getCurrencySymbol() ?: 0,
                    onValueChange = {
                                    viewModel.onUIEvent(AnswerQuestionsViewModel.UIEvent.OnPaymentAmountValueChange(it))
                    },
                    onError = Pair(true, 0),
                    focusManager = focusManager
                )
                EmploymentSituation(
                    items = viewModel.uiState.employmentSituationList,
                    value = viewModel.uiState.employmentSituationSelected,
                    onValueChange = {
                        viewModel.onUIEvent(AnswerQuestionsViewModel.UIEvent.OnEmploymentSituationValueChanged(it))
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Continue(
                enable = viewModel.uiState.isContinueEnabled,
                onClick = {
                    viewModel.onUIEvent(AnswerQuestionsViewModel.UIEvent.OnContinueClick(focusManager))
                }
            )
        }
    }
    LoadingIndicator(viewModel.uiState.isLoading)
    ErrorDialog(viewModel.uiState.openDialog)

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
    currencySymbol: Int,
    onValueChange: (String) -> Unit,
    onError: Pair<Boolean, Int>,
    focusManager: FocusManager
) {
    CustomOutlinedTextField(
        value = value,
        leadingIcon = R.drawable.ic_money_voucher,
        onValueChange = {
            //viewModel.onUIEvent(SmartRetiredViewModel.UIEvent.OnPaymentAmountValueChange(it))
                        onValueChange(it)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        }),
        labelText = stringResource(id = R.string.disbursement_answer_questions_income_label),
        modifier = Modifier
            .padding(top = 16.dp),
        placeHolder = stringResource(id = R.string.disbursement_answer_questions_income_placeholder),
        customTransformation = formatDecimalMoney(
            stringResource(id = currencySymbol)
            //stringResource(sharedViewModel.idBrandAsInt.getCurrencySymbol())
        )
    )
}

@Composable
private fun EmploymentSituation(
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
        labelText = stringResource(id = R.string.disbursement_answer_questions_labor_situation_label),
        placeHolder = stringResource(id = R.string.select)
    )
}

@Composable
private fun BirthDay(
    context: Context,
    focusManager: FocusManager,
    value: String,
    minYear: Int,
    minMonth: Int,
    minDay: Int,
    onValueChange: (String) -> Unit,
    onError: Pair<Boolean, Int>
) {
    CustomDatePicker(
        context = context,
        modifier = Modifier.padding(top = 16.dp),
        labelText = stringResource(id = R.string.disbursement_answer_questions_birthdate_label),
        placeHolder = stringResource(id = R.string.credit_job_date_placeholder),
        value = value,
        minYear = minYear,
        minMonth = minMonth,
        minDay = minDay,
        leadingIcon = R.drawable.ic_calendar_voucher,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.clearFocus()
        }),
        onValueChange = { _, year, month, dayOfMonth ->
            onValueChange(
                getPickedDateAsString(
                    year,
                    month,
                    dayOfMonth,
                    AnswerQuestionsViewModel.DATE_FORMAT
                )
            )
        },
        isError = onError.first,
        errorMessage = stringResource(id = onError.second)
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
        isRightButtonVisible = true,
        onLeftButtonClick = onBackClick
    )
}

