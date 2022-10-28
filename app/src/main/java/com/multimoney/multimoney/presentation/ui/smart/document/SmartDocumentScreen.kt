package com.multimoney.multimoney.presentation.ui.smart.document

import android.app.DatePickerDialog
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Gender
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnBirthDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnCallQueryAddressLevelTwoUseCase
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnCallQueryCivilStatusUseCase
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnCallQueryNationalitiesUseCase
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnCallQueryProfessionUseCase
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnCivilStateChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnGenderChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.document.SmartDocumentViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import java.util.Calendar
import java.util.Date

@Composable
fun SmartDocumentScreen(
    viewModel: SmartDocumentViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.baseEvent.collect { event ->
            when (event) {
                is SmartDocumentViewModel.BaseEvent.OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
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
                        viewModel.onUIEvent(UIEvent.OnNextActionClick(
                            nextStepAction = {
                                sharedViewModel.onUIEvent(
                                    SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase(
                                        // FIXME, pass whatever needed and obtain it from the uiState variable
                                        accountSmartData = sharedViewModel.accountSmartData?.copy(
                                            status = 1,
                                            idProfessionType = viewModel.uiState.professionId,
                                            idGender= viewModel.uiState.genderId,
                                            expirationDate = viewModel.uiState.expirationDate,
                                            birthday = viewModel.uiState.birthdate,
                                            idCivilStatusType = viewModel.uiState.civilStateId,
                                            currentStep = SmartSteps.Search.getNameById(sharedViewModel.uiState.currentStep)
                                        )
                                    )
                                )
                            }
                        ))
                    },
                    nextStep = SmartSteps.Two.id,
                    previousStep = SmartSteps.One.id
                )
            )

        viewModel.onUIEvent(
            OnCallQueryNationalitiesUseCase(
                sharedViewModel.accountSmartData?.user.orEmpty(),
                sharedViewModel.accountSmartData?.idBrand ?: 0
            )
        )
        viewModel.onUIEvent(
            OnCallQueryAddressLevelTwoUseCase(
                sharedViewModel.accountSmartData?.user.orEmpty(),
                sharedViewModel.accountSmartData?.pkUser.orEmpty(),
                sharedViewModel.accountSmartData?.idBrand ?: 0
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
                    style = Typography.h4.toSpanStyle()
                        .copy(
                            color = MultimoneyTheme.colors.text,
                            fontWeight = FontWeight.SemiBold
                        )
                ) {
                    append(stringResource(id = R.string.smart_account_document_title))
                }
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            leadingIcon = R.drawable.ic_calendar,
            modifier = Modifier
                .padding(top = 32.dp),
            labelText = stringResource(id = R.string.smart_account_document_birthdate_title),
            placeHolder = stringResource(id = R.string.select),
            value = viewModel.uiState.birthdate,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ), keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.credit_job_date_required),
            onClick = {
                focusManager.clearFocus()
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context, { _, year, month, day ->
                        val date = getPickedDateAsString(
                            year,
                            month,
                            day,
                            SmartDocumentViewModel.DATE_FORMAT
                        )
                        viewModel.onUIEvent(OnBirthDateValueChange(date))
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

        CustomDropdown(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = Gender.Search.getGenderList(),
            value = viewModel.uiState.gender,
            onValueChange = { gender -> viewModel.onUIEvent(OnGenderChange(gender)) },
            labelText = stringResource(id = R.string.gender),
            placeHolder = stringResource(id = R.string.select)
        )

        CustomDropdown(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = Gender.Search.getGenderList(),
            value = viewModel.uiState.civilState,
            onValueChange = { viewModel.onUIEvent(OnCivilStateChange(it)) },
            labelText = stringResource(id = R.string.civil_state),
            placeHolder = stringResource(id = R.string.select)
        )

        CustomDropdown(
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentSize(Alignment.TopStart)
                .focusable(false),
            items = Gender.Search.getGenderList(),
            value = viewModel.uiState.profession,
            onValueChange = { viewModel.onUIEvent(OnProfessionChange(it)) },
            labelText = stringResource(id = R.string.profession),
            placeHolder = stringResource(id = R.string.select)
        )

        CustomOutlinedTextField(
            leadingIcon = R.drawable.ic_calendar,
            modifier = Modifier
                .padding(top = 32.dp),
            labelText = stringResource(id = R.string.smart_account_document_expiration_title),
            placeHolder = stringResource(id = R.string.select),
            value = viewModel.uiState.expirationDate,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ), keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.smart_account_document_expiration_date_required),
            onClick = {
                focusManager.clearFocus()
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context, { _, year, month, day ->
                        val date = getPickedDateAsString(
                            year,
                            month,
                            day,
                            SmartDocumentViewModel.DATE_FORMAT
                        )
                        viewModel.onUIEvent(UIEvent.OnExpirationDateValueChange(date))
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
                datePicker.datePicker.minDate = Date().time
                datePicker.show()
            },
            isClickable = true
        )
    }
}
