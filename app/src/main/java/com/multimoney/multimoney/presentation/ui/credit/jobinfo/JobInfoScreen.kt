package com.multimoney.multimoney.presentation.ui.credit.jobinfo

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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStep
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.Companion.DATE_FORMAT
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.Companion.JOB_DATE_MIN_DAY
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.Companion.JOB_DATE_MIN_MONTH
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.Companion.JOB_DATE_MIN_YEAR
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.UIEvent.OnCompanyNameValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.UIEvent.OnDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobinfo.JobInfoViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.VisualTransformationMasks.PHONE_TRANSFORMATION_MASK
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation
import java.util.Calendar
import java.util.Date

@Composable
fun JobPlaceScreen(
    sharedViewModel: CreditViewModel,
    viewModel: JobInfoViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
            viewModel.onUIEvent(
                JobInfoViewModel.UIEvent.OnNextActionClick(
                    user = sharedViewModel.email,
                    nextStepAction = {
                        sharedViewModel.onUIEvent(
                            CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
                        )
                    }, saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                )
            )
        }, nextStep = CreditStep.Five.id, previousStep = CreditStep.Three.id))
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormCompleted -> {
                    sharedViewModel.onUIEvent(CreditViewModel.UIEvent.OnContinueEnable(event.isFormCompleted))
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.onUIEvent(OnValidForm)
        viewModel.onUIEvent(JobInfoViewModel.UIEvent.OnLoadCreditSteps(sharedViewModel.saveCreditStepsHelper.inputTextInfoList))
    }

    val title = when (sharedViewModel.idBrand.toInt()) {
        Brand.Guatemala.id -> R.string.credit_job_title_gt
        else -> R.string.credit_job_title
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = title),
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
            leadingIcon = R.drawable.ic_calendar,
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
                focusManager.clearFocus()
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context, { _, year, month, day ->
                        viewModel.onUIEvent(
                            OnDateValueChange(
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
                calendar.set(JOB_DATE_MIN_YEAR, JOB_DATE_MIN_MONTH, JOB_DATE_MIN_DAY)
                datePicker.datePicker.minDate = calendar.timeInMillis
                datePicker.datePicker.maxDate = Date().time
                datePicker.show()
            },
            isClickable = true
        )

        val phonePlaceHolder = when (sharedViewModel.idBrand.toInt()) {
            Brand.ElSalvador.id -> R.string.credit_job_phone_placeholder_sv
            Brand.CostaRica.id -> R.string.credit_job_phone_placeholder_cr
            Brand.Guatemala.id -> R.string.credit_job_phone_placeholder_gt
            else -> R.string.empty
        }

        CustomOutlinedTextField(
            leadingIcon = R.drawable.ic_phone,
            value = viewModel.uiState.phoneNumber,
            placeHolder = stringResource(id = phonePlaceHolder),
            onValueChange = { phoneNumber ->
                viewModel.onUIEvent(OnPhoneNumberValueChange(phoneNumber))
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