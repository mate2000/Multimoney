package com.multimoney.multimoney.presentation.ui.smart.origination.beneficiary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryOptionChange
import com.multimoney.multimoney.presentation.uielement.CustomRadioButtonsLayout

@Composable
fun SmartBeneficiaryScreen(
    viewModel: BeneficiariesViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
) {

    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueVisible(true))
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueEnable(false))

        sharedViewModel.onUIEvent(
            SmartViewModel.UIEvent.OnSetNavigation(
                nextStep = SmartSteps.Five.id,
                previousStep = SmartSteps.Three.id
            )
        )
    }

    if (viewModel.uiState.addBeneficiaryOption) {
        BeneficiariesScreen(viewModel, sharedViewModel)
    } else {
        val radioOptions = stringArrayResource(id = R.array.options_yes_no)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = stringResource(id = R.string.smart_beneficiary_title),
                modifier = Modifier.padding(top = 16.dp),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )
            Text(
                text = stringResource(id = R.string.smart_beneficiary_header),
                modifier = Modifier.padding(top = 24.dp),
                style = Typography.subtitle1.copy(fontSize = 17.sp, letterSpacing = (-0.41).sp),
                color = MultimoneyTheme.colors.labelText
            )
            CustomRadioButtonsLayout(
                modifier = Modifier.padding(top = 24.dp),
                radioOptions.toList()
            ) { optionSelected ->
                sharedViewModel.onUIEvent(
                    SmartViewModel.UIEvent.OnContinueVisible(
                        true,
                        R.string.save
                    )
                )
                sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueEnable(true))
                if (optionSelected == radioOptions.firstOrNull()) {
                    sharedViewModel.onUIEvent(
                        SmartViewModel.UIEvent.OnSetNavigation(
                            nextAction = {
                                viewModel.onUIEvent(OnAddBeneficiaryOptionChange(true))
                            },
                            nextStep = SmartSteps.Five.id,
                            previousStep = SmartSteps.Three.id
                        )
                    )
                } else {
                    sharedViewModel.onUIEvent(
                        SmartViewModel.UIEvent.OnSetNavigation(
                            nextStep = SmartSteps.Five.id,
                            previousStep = SmartSteps.Three.id
                        )
                    )
                }
            }
        }
    }
}
