package com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.accountsmart.GeneralEconomicActivity
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.SourceIncomeViewModel
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.SourceIncomeViewModel.UIEvent.OnNavigateToSelectedSourceOfIncomeOption
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.options.SourceIncomeOptionsViewModel.UIEvent.OnCallQueryGetSourceOfIncome
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceincome.options.SourceIncomeOptionsViewModel.UIState
import com.multimoney.multimoney.presentation.uielement.CustomCatalogItem
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator

@Composable
fun SourceIncomeOptionsScreen(
    viewModel: SourceIncomeOptionsViewModel = hiltViewModel(),
    sourceIncomeSharedViewModel: SourceIncomeViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel() // TODO, pass the correct sharedViewModel
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallQueryGetSourceOfIncome)
        sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnContinueVisible(false))
    }

    viewModel.uiState.apply {
        SourceIncomeContent(
            generalEconomicActivityList = generalEconomicActivityList ?: listOf(),
            isLoading = isLoading,
            onItemClick = { sourceOfIncome ->
                sourceOfIncome?.id?.let {
                    sourceIncomeSharedViewModel.onUIEvent((OnNavigateToSelectedSourceOfIncomeOption(it)))
                }
            }
        )
        ShowCustomDialog(this)
    }
}

@Composable
@Preview
fun SourceIncomeContent(
    generalEconomicActivityList: List<GeneralEconomicActivity?> = listOf(),
    isLoading: Boolean = false,
    onItemClick: (GeneralEconomicActivity?) -> Unit = { }
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.smart_origin_main_income_title),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(2.dp)
        ) {
            items(generalEconomicActivityList) { sourceOfIncome ->
                CustomCatalogItem(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 140.dp)
                        .height(140.dp)
                        .fillMaxWidth()
                        .padding(10.dp),
                    iconId = sourceOfIncome?.iconCode ?: 0,
                    label = sourceOfIncome?.description ?: "",
                    onClick = { onItemClick(sourceOfIncome) }
                )
            }
        }
        LoadingIndicator(isLoading)
    }
}

@Composable
fun ShowCustomDialog(uiState: UIState) {
    if (uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(uiState.openDialog.titleResource),
            message = stringResource(uiState.openDialog.descriptionResource).ifEmpty { uiState.openDialog.description },
            positiveButtonText = stringResource(uiState.openDialog.positiveResource),
            openDialogCustom = uiState.openDialog.isActive,
            onPositiveAction = uiState.openDialog.positiveAction
        )
    }
}
