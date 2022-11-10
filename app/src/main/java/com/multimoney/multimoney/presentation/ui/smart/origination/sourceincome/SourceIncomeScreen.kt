package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome

import OwnBusinessOnPersonalBasisScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.SourceIncomeOptionsScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.independentprofessional.IndProfessionalScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.otherincome.OtherIncomeScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusiness.SmartOwnBusinessSvScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartCrSalaryScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.SmartRetiredScreen
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType

/**
 * This is the host screen of options, the content inside should be replaceable for the
 * selected screen. e.g. FreelancerScreen, OwnBusinessScree, etc.
 */
@Composable
fun SourceIncomeScreen(
    viewModel: SourceIncomeViewModel = hiltViewModel(),
    sharedViewModel: SmartViewModel = hiltViewModel(),
) {
    ShowSelectedSourceIncomeOption(
        selectedOption = viewModel.uiState.selectedOption,
        sharedViewModel = sharedViewModel,
        sourceIncomeSharedViewModel = viewModel
    )
}

/**
 * This composable function is intended to replace the composable content depending
 * on the selected option.
 */
@Composable
fun ShowSelectedSourceIncomeOption(
    selectedOption: Int,
    sharedViewModel: SmartViewModel,
    sourceIncomeSharedViewModel: SourceIncomeViewModel,
) {
    when (selectedOption) {
        SourceIncomeOptionType.OwnBusiness.id -> SmartOwnBusinessSvScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.FormalSalaried.id -> SmartCrSalaryScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.Retired.id -> SmartRetiredScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.OtherSV.id,
        SourceIncomeOptionType.OtherCR.id -> OtherIncomeScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.OwnBusinessOnPersonalBasis.id -> OwnBusinessOnPersonalBasisScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )
        SourceIncomeOptionType.FreeLancer.id -> IndProfessionalScreen(
            sharedViewModel = sharedViewModel,
            sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
        )

        else -> {
            // if no option gets selected whenever tapping an item from the list, then it means
            // we should show the main source of income options screen.
            SourceIncomeOptionsScreen(
                sharedViewModel = sharedViewModel,
                sourceIncomeSharedViewModel = sourceIncomeSharedViewModel
            )
        }
    }
}
