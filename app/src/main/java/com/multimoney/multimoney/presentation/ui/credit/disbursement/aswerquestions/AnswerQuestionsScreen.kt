package com.multimoney.multimoney.presentation.ui.credit.disbursement.aswerquestions

import android.app.DatePickerDialog
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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.retired.SmartRetiredViewModel
import com.multimoney.multimoney.presentation.uielement.*
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import com.multimoney.multimoney.presentation.util.transformation.formatDecimalMoney
import com.multimoney.multimoney.presentation.util.transformation.formatMoney
import java.util.*

@Composable
fun AnswerQuestionsScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: AnswerQuestionsScreenViewModel = hiltViewModel()
) {
    // View

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

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
                Title(title = viewModel.uiState.titleResource)
                BirthDay(
                    context = context,
                    focusManager = focusManager,
                    value = "",
                    onValueChange = {

                    }
                )
                MonthlyIncome(
                    value = "",
                    onValueChange = {},
                    onError = Pair(true, 0),
                    focusManager = focusManager
                )
                LaborSituation(items = listOf(), value = "" , onValueChange = { } )
            }
            Spacer(modifier = Modifier.weight(1f))
            Continue(
                enable = viewModel.uiState.isContinueEnabled,
                onClick = {  }
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
    onValueChange: (String) -> Unit,
    onError: Pair<Boolean, Int>,
    focusManager: FocusManager
) {
    CustomOutlinedTextField(
        value = "",
        onValueChange = {
            //viewModel.onUIEvent(SmartRetiredViewModel.UIEvent.OnPaymentAmountValueChange(it))
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
        customTransformation = formatDecimalMoney("$"
            //stringResource(sharedViewModel.idBrandAsInt.getCurrencySymbol())
        )
    )
}

@Composable
private fun LaborSituation(
    items: List<String>,
    value: String,
    onValueChange: (String) -> Unit
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
    onValueChange: (String) -> Unit
){
    CustomOutlinedTextField(
        leadingIcon = R.drawable.ic_calendar,
        modifier = Modifier
            .padding(top = 32.dp),
        labelText = stringResource(id = R.string.disbursement_answer_questions_birthdate_label),
        placeHolder = stringResource(id = R.string.disbursement_answer_questions_birthdate_placeholder),
        value = "",
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.clearFocus()
        }),
        isRequired = true,
        isRequiredMessage = stringResource(id = R.string.credit_job_date_required),
        onClick = {
            focusManager.clearFocus()
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                context,
                { _, year, month, day ->
                    val date = getPickedDateAsString(
                        year,
                        month,
                        day,
                        SmartDocumentViewModel.DATE_FORMAT
                    )
                    //viewModel.onUIEvent(SmartDocumentViewModel.UIEvent.OnBirthDateValueChange(date))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            calendar.set(
                SmartDocumentViewModel.BIRTH_DATE_MIN_YEAR,
                SmartDocumentViewModel.BIRTH_DATE_MIN_MONTH,
                SmartDocumentViewModel.BIRTH_DATE_MIN_DAY
            )
            datePicker.datePicker.minDate = calendar.timeInMillis
            datePicker.datePicker.maxDate = Date().time
            datePicker.show()
        },
        isClickable = true
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

