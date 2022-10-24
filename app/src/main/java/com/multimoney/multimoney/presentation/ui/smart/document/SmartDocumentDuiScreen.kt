package com.multimoney.multimoney.presentation.ui.smart.document

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.Companion.BIRTH_DATE_MIN_DAY
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.Companion.BIRTH_DATE_MIN_MONTH
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.Companion.BIRTH_DATE_MIN_YEAR
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.Companion.DATE_FORMAT
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnExpirationDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnIssueDateValueChange
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import java.util.*

@Composable
fun SmartDocumentDuiScreen(
    viewModel: SmartDocumentViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h4.toSpanStyle()
                        .copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                ) {
                    append(stringResource(id = string.smart_account_document_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            leadingIcon = drawable.ic_calendar,
            modifier = Modifier
                .padding(top = 32.dp),
            labelText = stringResource(id = string.smart_account_document_issue_title),
            placeHolder = stringResource(id = string.select),
            value = viewModel.uiState.issueDate,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ), keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequired = true,
            isRequiredMessage = stringResource(id = string.credit_job_date_required),
            onClick = {
                focusManager.clearFocus()
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context, { _, year, month, day ->
                        viewModel.onUIEvent(
                            OnIssueDateValueChange(
                                getPickedDateAsString(
                                    year,
                                    month,
                                    day,
                                    DATE_FORMAT
                                )
                            )
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                calendar.set(BIRTH_DATE_MIN_YEAR, BIRTH_DATE_MIN_MONTH, BIRTH_DATE_MIN_DAY)
                datePicker.datePicker.minDate = calendar.timeInMillis
                datePicker.datePicker.maxDate = Date().time
                datePicker.show()
            },
            isClickable = true
        )

        CustomOutlinedTextField(
            leadingIcon = drawable.ic_calendar,
            modifier = Modifier
                .padding(top = 32.dp),
            labelText = stringResource(id = string.smart_account_document_expiration_title),
            placeHolder = stringResource(id = string.select),
            value = viewModel.uiState.expirationDate,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ), keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequired = true,
            isRequiredMessage = stringResource(id = string.smart_account_document_expiration_date_required),
            onClick = {
                focusManager.clearFocus()
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context, { _, year, month, day ->
                        viewModel.onUIEvent(
                            OnExpirationDateValueChange(
                                getPickedDateAsString(
                                    year,
                                    month,
                                    day,
                                    DATE_FORMAT
                                )
                            )
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                calendar.set(BIRTH_DATE_MIN_YEAR, BIRTH_DATE_MIN_MONTH, BIRTH_DATE_MIN_DAY)
                datePicker.datePicker.minDate = calendar.timeInMillis
                datePicker.datePicker.maxDate = Date().time
                datePicker.show()
            },
            isClickable = true
        )
    }
}