package com.multimoney.multimoney.presentation.ui.smart.origin.sourceofincome

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.smart.origin.sourceofincome.SourceOfIncome
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.ui.smart.origin.sourceofincome.SourceOfIncomeViewModel.UIEvent.OnCallQueryGetSourceOfIncome
import com.multimoney.multimoney.presentation.uielement.CustomCatalogItem
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator

@Composable
fun SourceOfIncomeScreen(
    viewModel: SourceOfIncomeViewModel = hiltViewModel(),
    sharedViewModel: SignUpViewModel = hiltViewModel() // TODO, pass the correct sharedViewModel
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallQueryGetSourceOfIncome)
        sharedViewModel.onUIEvent(SignUpViewModel.UIEvent.OnContinueVisible(false))
    }

    viewModel.apply {
        uiState.sourceOfIncomeList?.let { sourceOfIncomeList ->
            SourceOfIncomeContent(
                sourceOfIncomeList = sourceOfIncomeList,
                isLoading = uiState.isLoading
            )
        }
    }
}

@Composable
@Preview
fun SourceOfIncomeContent(
    sourceOfIncomeList: List<SourceOfIncome?> = listOf(),
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        LoadingIndicator(isLoading)
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
            items(sourceOfIncomeList) { sourceOfIncome ->
                CustomCatalogItem(
                    iconId = sourceOfIncome?.iconId ?: 0,
                    label = sourceOfIncome?.label ?: "",
                    onClick = {
                        // TODO, navigate to other screens from here
                        Toast.makeText(context, "${sourceOfIncome?.label}", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }
    }
}
