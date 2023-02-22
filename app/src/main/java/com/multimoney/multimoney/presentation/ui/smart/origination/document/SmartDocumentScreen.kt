package com.multimoney.multimoney.presentation.ui.smart.origination.document

import android.app.DatePickerDialog
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Gender
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.nonpreapproved.NonPreApprovedViewModel.Companion.DATE_MIN_YEARS
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnBirthDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnCallQueryCivilStatusUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnCallQueryProfessionUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnCivilStateChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnGenderChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnLoadCurrentStepData
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.BIRTH_DATE_MIN_DAY
import com.multimoney.multimoney.presentation.util.BIRTH_DATE_MIN_MONTH
import com.multimoney.multimoney.presentation.util.BIRTH_DATE_MIN_YEAR
import com.multimoney.multimoney.presentation.util.DAY_MONTH_YEAR_PATTERN_BAR_FORMAT
import com.multimoney.multimoney.presentation.util.ISO_8601_API_FORMAT_PATTERN
import com.multimoney.multimoney.presentation.util.getFormatDateByString
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import com.multimoney.multimoney.presentation.util.toLocalDate
import java.util.Calendar
import java.util.Date

@Composable
fun SmartDocumentScreen(
    viewModel: SmartDocumentViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(sharedViewModel.accountSmartData) {
        viewModel.onUIEvent(OnLoadCurrentStepData(sharedViewModel.accountSmartData))
    }

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueVisible(true))
        viewModel.baseEvent.collect { event ->
            when (event) {
                is SmartDocumentViewModel.BaseEvent.OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
                is SmartDocumentViewModel.BaseEvent.OnLoadingValueChange -> sharedViewModel.onUIEvent(
                    SmartViewModel.UIEvent.OnLoadingValueChange(event.isLoading)
                )
                is SmartDocumentViewModel.BaseEvent.OnFailureWithDialog -> sharedViewModel.onUIEvent(
                    SmartViewModel.UIEvent.OnFailureWithDialog(event.isLoading, event.openDialog)
                )
            }
        }
    }

    LaunchedEffect(true) {
        // the OnNextActionClick event will be triggering the parent button action (located in SmartScreen)
        // then, the OnCallMutationUpdateGlobalRequestUseCase() event will receive the form data in order to
        // update the object that contains the data to be sent to the API, such method will trigger the API
        // call as well, with the data passed as parameter.

        sharedViewModel.onUIEvent(
            SmartViewModel.UIEvent.OnSetNavigation(
                nextAction = {
                    viewModel.onUIEvent(
                        UIEvent.OnNextActionClick(
                            nextStepAction = {
                                sharedViewModel.onUIEvent(
                                    SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase(
                                        accountSmartData = sharedViewModel.accountSmartData?.copy(
                                            idProfessionType = viewModel.uiState.professionId,
                                            stringProfessionType = viewModel.uiState.profession,
                                            idGender = viewModel.uiState.genderId,
                                            strGenre = viewModel.uiState.gender,
                                            expirationDate = getFormatDateByString(
                                                viewModel.uiState.expirationDate,
                                                DAY_MONTH_YEAR_PATTERN_BAR_FORMAT,
                                                ISO_8601_API_FORMAT_PATTERN
                                            ),
                                            birthday = getFormatDateByString(
                                                viewModel.uiState.birthdate,
                                                DAY_MONTH_YEAR_PATTERN_BAR_FORMAT,
                                                ISO_8601_API_FORMAT_PATTERN
                                            ),
                                            idCivilStatusType = viewModel.uiState.civilStateId,
                                            strMaritalStatus = viewModel.uiState.civilState,
                                            currentStep = SmartSteps.Search.getNameById(
                                                sharedViewModel.uiState.currentStep
                                            )
                                        )
                                    )
                                )
                            }
                        )
                    )
                },
                nextStep = SmartSteps.Two.id,
                previousStep = SmartSteps.One.id
            )
        )
        viewModel.onUIEvent(
            OnCallQueryCivilStatusUseCase(
                sharedViewModel.accountSmartData?.user.orEmpty(),
                sharedViewModel.accountSmartData?.idBrand ?: 0
            )
        )
        viewModel.onUIEvent(
            OnCallQueryProfessionUseCase(
                sharedViewModel.accountSmartData?.user.orEmpty(),
                sharedViewModel.accountSmartData?.idBrand ?: 0
            )
        )
        viewModel.onUIEvent(OnValidateForm)
    }

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = Typography.h6.toSpanStyle()
                        .copy(
                            color = MultimoneyTheme.colors.labelText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            letterSpacing = 0.15.sp
                        )
                ) {
                    append(stringResource(id = R.string.smart_account_document_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            trailingIcon = R.drawable.ic_calendar_credit_questions,
            modifier = Modifier
                .padding(top = 32.dp),
            labelText = stringResource(id = R.string.smart_account_document_birthdate_title),
            placeHolder = stringResource(id = R.string.smart_account_date_placeholder),
            value = viewModel.uiState.birthdate,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            errorMessage = stringResource(id = viewModel.uiState.birthdateError),
            isError = viewModel.uiState.birthdateErrorStatus,
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.credit_job_date_required),
            onClick = {
                focusManager.clearFocus()
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context,
                    R.style.CustomDarkDatePickerStyle,
                    { _, year, month, day ->
                        val date = getPickedDateAsString(
                            year,
                            month,
                            day,
                            DAY_MONTH_YEAR_PATTERN_BAR_FORMAT
                        )

                        val calendarValidation = Calendar.getInstance()
                        calendarValidation.set(year, month, day)
                        viewModel.onUIEvent(
                            OnBirthDateValueChange(
                                date,
                                calendarValidation.toLocalDate()
                            )
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                calendar.set(
                    calendar.get(Calendar.YEAR) - DATE_MIN_YEARS.toInt(),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.datePicker.minDate = calendar.timeInMillis
                datePicker.datePicker.maxDate = Date().time
                datePicker.show()
            },
            isClickable = true
        )

        CustomDropdown(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = Gender.Search.getGenderList(),
            value = viewModel.uiState.gender,
            onValueChange = { gender, _ ->
                viewModel.onUIEvent(OnGenderChange(gender))
            },
            labelText = stringResource(id = R.string.gender),
            placeHolder = stringResource(id = R.string.select)
        )

        CustomDropdown(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = viewModel.uiState.civilStatusList.map { it?.maritalStatusDescription.orEmpty() },
            value = viewModel.uiState.civilState,
            onValueChange = { valueSelected, _ ->
                viewModel.onUIEvent(OnCivilStateChange(valueSelected))
            },
            labelText = stringResource(id = R.string.civil_state),
            placeHolder = stringResource(id = R.string.select)
        )

        CustomDropdown(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = viewModel.uiState.professionSmartList.map { it?.name.orEmpty() },
            value = viewModel.uiState.profession,
            onValueChange = { valueSelected, _ ->
                viewModel.onUIEvent(OnProfessionChange(valueSelected))
            },
            labelText = stringResource(id = R.string.profession),
            placeHolder = stringResource(id = R.string.select)
        )

        CustomOutlinedTextField(
            trailingIcon = R.drawable.ic_calendar_credit_questions,
            modifier = Modifier
                .padding(top = 32.dp),
            labelText = stringResource(id = R.string.smart_account_document_expiration_title),
            placeHolder = stringResource(id = R.string.smart_account_date_placeholder),
            value = viewModel.uiState.expirationDate,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.smart_account_document_expiration_date_required),
            onClick = {
                focusManager.clearFocus()
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context,
                    R.style.CustomDarkDatePickerStyle,
                    { _, year, month, day ->
                        val date = getPickedDateAsString(
                            year,
                            month,
                            day,
                            DAY_MONTH_YEAR_PATTERN_BAR_FORMAT
                        )
                        viewModel.onUIEvent(UIEvent.OnExpirationDateValueChange(date))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                calendar.set(
                    BIRTH_DATE_MIN_YEAR,
                    BIRTH_DATE_MIN_MONTH,
                    BIRTH_DATE_MIN_DAY
                )
                datePicker.datePicker.minDate = Date().time
                datePicker.show()
            },
            isClickable = true
        )
    }
}
