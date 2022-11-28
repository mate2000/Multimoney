package com.multimoney.multimoney.presentation.ui.smart.origination.beneficiary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.data.util.catalog.SmartSteps.Search
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryOptionChange
import com.multimoney.multimoney.presentation.uielement.CustomRadioButtonsLayout
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters

@Composable
fun SmartBeneficiaryScreen(
    viewModel: BeneficiariesViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        sharedViewModel.onUIEvent(
            SmartViewModel.UIEvent.OnContinueVisible(true, R.string.save)
        )
        sharedViewModel.onUIEvent(SmartViewModel.UIEvent.OnContinueEnable(false))

        sharedViewModel.onUIEvent(
            SmartViewModel.UIEvent.OnSetNavigation(
                nextStep = SmartSteps.Five.id,
                previousStep = SmartSteps.Three.id
            )
        )
    }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.smart_account_beneficiaries_title),
                style = Typography.h6.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MultimoneyTheme.colors.labelText
                )
            )
            IconButton(onClick = {
                sharedViewModel.onUIEvent(
                    SmartViewModel.UIEvent.OnOpenDialogValueChange(
                        DialogParameters(
                            isActive = mutableStateOf(true),
                            titleResource = R.string.smart_account_beneficiaries_title,
                            descriptionResource = R.string.smart_account_beneficiary_info_description
                        )
                    )
                )
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_information_chip),
                    contentDescription = stringResource(R.string.content_description_more_info),
                    tint = Color.Unspecified
                )
            }
        }

        if (viewModel.uiState.addBeneficiaryOption) {
            BeneficiariesScreen(viewModel, sharedViewModel)
        } else {
            val radioOptions = stringArrayResource(id = R.array.options_yes_no)
            Text(
                text = stringResource(id = R.string.smart_beneficiary_header),
                modifier = Modifier.padding(top = 24.dp),
                style = Typography.subtitle1,
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
                            nextAction = {
                                sharedViewModel.onUIEvent(
                                    OnCallMutationUpdateGlobalRequestUseCase(
                                        accountSmartData = sharedViewModel.accountSmartData?.copy(
                                            listBeneficiaries = viewModel.uiState.beneficiaryList,
                                            currentStep = Search.getNameById(
                                                sharedViewModel.uiState.currentStep
                                            )
                                        )
                                    )
                                )
                            },
                            nextStep = SmartSteps.Five.id,
                            previousStep = SmartSteps.Three.id
                        )
                    )
                }
            }
        }
    }
}
