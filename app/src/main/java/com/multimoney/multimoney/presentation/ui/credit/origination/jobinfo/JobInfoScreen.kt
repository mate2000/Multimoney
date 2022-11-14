package com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo

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
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.CreditViewModel.UIEvent.OnCallMutationSaveCreditFlowStep
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.Companion.DATE_FORMAT
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.Companion.JOB_DATE_MIN_DAY
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.Companion.JOB_DATE_MIN_MONTH
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.Companion.JOB_DATE_MIN_YEAR
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnCompanyNameValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnDateFirstJobValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnInitData
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.uielement.CustomDatePicker
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.util.getPickedDateAsString
import com.multimoney.multimoney.presentation.util.transformation.MaskVisualTransformation
import com.multimoney.multimoney.presentation.util.transformation.VisualTransformationMasks.PHONE_TRANSFORMATION_MASK

@Composable
fun JobInfoScreen(
    sharedViewModel: CreditViewModel,
    viewModel: JobInfoViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(
            CreditViewModel.UIEvent.OnSetNavigation(nextAction = {
                viewModel.onUIEvent(
                    OnNextActionClick(
                        user = sharedViewModel.email,
                        nextStepAction = {
                            sharedViewModel.onUIEvent(
                                OnCallMutationSaveCreditFlowStep
                            )
                        },
                        saveCreditStepsHelper = sharedViewModel.saveCreditStepsHelper
                    )
                )
            }, nextStep = CreditStep.Five.id, previousStep = CreditStep.Three.id)
        )
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
        viewModel.onUIEvent(
            OnInitData(
                idBrand = sharedViewModel.idBrand.toInt()
            )
        )
        viewModel.onUIEvent(OnLoadCreditSteps(sharedViewModel.saveCreditStepsHelper.inputTextInfoList))
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
            modifier = Modifier.padding(top = 24.dp),
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
            }
        )

        CustomDatePicker(
            context = context,
            modifier = Modifier.padding(top = 16.dp),
            labelText = stringResource(id = R.string.credit_job_joined_date),
            placeHolder = stringResource(id = R.string.credit_job_date_placeholder),
            value = viewModel.uiState.date,
            minYear = JOB_DATE_MIN_YEAR,
            minMonth = JOB_DATE_MIN_MONTH,
            minDay = JOB_DATE_MIN_DAY,
            leadingIcon = R.drawable.ic_calendar_voucher,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.clearFocus()
            }),
            onValueChange = { _, year, month, dayOfMonth ->
                viewModel.onUIEvent(
                    OnDateValueChange(
                        getPickedDateAsString(
                            year,
                            month,
                            dayOfMonth,
                            DATE_FORMAT
                        )
                    )
                )
            }
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
            modifier = Modifier.padding(top = 16.dp),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.credit_job_phone_required),
            customTransformation = MaskVisualTransformation(
                PHONE_TRANSFORMATION_MASK.mask,
                PHONE_TRANSFORMATION_MASK.maskChar
            )
        )

        if (sharedViewModel.idBrand.toInt() == Brand.CostaRica.id) {
            CustomDatePicker(
                context = context,
                modifier = Modifier.padding(top = 16.dp),
                labelText = stringResource(id = R.string.credit_job_joined_date_first_job),
                placeHolder = stringResource(id = R.string.credit_job_date_placeholder),
                value = viewModel.uiState.dateFirstJob,
                minYear = JOB_DATE_MIN_YEAR,
                minMonth = JOB_DATE_MIN_MONTH,
                minDay = JOB_DATE_MIN_DAY,
                leadingIcon = R.drawable.ic_calendar_voucher,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.clearFocus()
                }),
                onValueChange = { _, year, month, dayOfMonth ->
                    viewModel.onUIEvent(
                        OnDateFirstJobValueChange(
                            getPickedDateAsString(
                                year,
                                month,
                                dayOfMonth,
                                DATE_FORMAT
                            )
                        )
                    )
                }
            )
        }
    }
}
