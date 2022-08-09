package com.multimoney.multimoney.presentation.ui.credit.jobplace

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnCompanyNameValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.VisualTransformationMasks.PHONE_TRANSFORMATION_MASK
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation
import java.util.Calendar
import java.util.Date

@Composable
fun JobPlaceScreen(
    sharedViewModel: CreditViewModel,
    viewModel: JobPlaceViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val dateFormat = "dd-MM-yyyy"
    val jobDateMinYear = 1972
    val jobDateMinMonth = 0
    val jobDateMinDay = 1

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
            viewModel.onUIEvent(JobPlaceViewModel.UIEvent.OnNextActionClick {
                sharedViewModel.onUIEvent(
                    CreditViewModel.UIEvent.OnNextStep
                )
            })
        }, nextStep = CreditStep.Three.id, previousStep = CreditStep.Two.id))
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormCompleted -> {
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(event.isFormCompleted))
                }
            }
        }
    }

    LaunchedEffect(true){
        viewModel.onUIEvent(OnValidForm)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.credit_job_title),
            modifier = Modifier.padding(top = 32.dp),
            style = Typography.h5.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
            color = MultimoneyTheme.colors.labelText
        )

        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 32.dp),
            placeHolder = stringResource(id = R.string.credit_job_workplace_label),
            value = viewModel.uiState.companyName,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.credit_job_workplace_required),
            onValueChange = {
                viewModel.onUIEvent(OnCompanyNameValueChange(it))
            },
        )

        CustomOutlinedTextField(
            modifier = Modifier
                .padding(top = 32.dp),
            labelText = stringResource(id = R.string.credit_job_joined_date),
            placeHolder = stringResource(id = R.string.credit_job_date_placeholder),
            value = viewModel.uiState.date,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ), keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.credit_job_date_required),
            onClick = {
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context, { _, year, month, day ->
                        viewModel.onUIEvent(
                            OnDateValueChange(
                                getPickedDateAsString(
                                    year,
                                    month,
                                    day,
                                    dateFormat
                                )
                            )
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                calendar.set(jobDateMinYear, jobDateMinMonth, jobDateMinDay)
                datePicker.datePicker.minDate = calendar.timeInMillis
                datePicker.datePicker.maxDate = Date().time
                datePicker.show()
            },
            isClickable = true
        )

        CustomOutlinedTextField(
            value = viewModel.uiState.phoneNumber,
            placeHolder = stringResource(id = R.string.credit_job_phone_placeholder),
            onValueChange = { phoneNumber ->
                if (phoneNumber.length <= 12) {
                    viewModel.onUIEvent(OnPhoneNumberValueChange(phoneNumber))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            labelText = stringResource(id = R.string.credit_job_phone_number),
            modifier = Modifier.padding(top = 44.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.credit_job_phone_required),
            customTransformation = MaskVisualTransformation(
                PHONE_TRANSFORMATION_MASK.mask,
                PHONE_TRANSFORMATION_MASK.maskChar
            )
        )
    }
}