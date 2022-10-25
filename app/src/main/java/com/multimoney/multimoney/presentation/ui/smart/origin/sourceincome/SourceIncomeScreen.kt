package com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.options.SourceIncomeOptionsScreen
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.ownbusiness.SmartOwnBusinessSvScreen
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeTypeOption

/**
 * This is the host screen of options, the content inside should be replaceable for the
 * selected screen. e.g. FreelancerScreen, OwnBusinessScree, etc.
 */
@Composable
fun SourceIncomeScreen(
    viewModel: SourceIncomeViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel() // TODO, pass the correct sharedViewModel
) {
    ShowSelectedSourceIncomeOption(
        selectedOption = viewModel.uiState.selectedOption,
        sharedViewModel = sharedViewModel,
        sourceIncomeMainSharedViewModel = viewModel
    )
}

/**
 * This composable function is intended to replace the composable content depending
 * on the selected option.
 */
@Composable
fun ShowSelectedSourceIncomeOption(
    selectedOption: Int,
    sharedViewModel: SignUpViewModel,
    sourceIncomeMainSharedViewModel: SourceIncomeViewModel
) {
    when (selectedOption) {
        SourceIncomeTypeOption.OwnBusiness.id -> SmartOwnBusinessSvScreen(sharedViewModel = sharedViewModel)
        else -> {
            // if no option gets selected whenever tapping an item from the list, then it means
            // we should show the main source of income options screen.
            SourceIncomeOptionsScreen(
                sharedViewModel = sharedViewModel,
                sourceIncomeSharedViewModel = sourceIncomeMainSharedViewModel
            )
        }
    }
}
