package com.multimoney.multimoney.presentation.ui.credit.creditbank

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.ui.credit.CreditViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDropdown
import com.multimoney.multimoney.presentation.uielement.CustomInformativeChip
import com.multimoney.multimoney.presentation.uielement.Size

@Composable
@Preview
fun CreditBankScreen(
    sharedViewModel: CreditViewModel = hiltViewModel(),
) {
    var title = R.string.empty
    if (sharedViewModel.idBrand.isNotEmpty()) {
        title = when (sharedViewModel.idBrand.toInt()) {
            Brand.Guatemala.id -> R.string.credit_bank_title_gt
            else -> R.string.credit_bank_title
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 40.dp),
            text = stringResource(id = title),
            style = Typography.h5.copy(
                color = MultimoneyTheme.colors.text,
                fontWeight = FontWeight.SemiBold
            )
        )

        CustomInformativeChip(
            text = stringResource(id = R.string.credit_bank_condition),
            textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.labelText),
            modifier = Modifier.padding(top = 16.dp),
            shape = RoundedCornerShape(24.dp),
            background = WhiteTransparency10,
            startIcon = R.drawable.ic_information,
            startIconTint = MultimoneyTheme.colors.textInformation,
            size = Size.Large
        )

        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 16.dp),
            items = arrayListOf(),
            onValueChange = {},
            labelText = stringResource(id = R.string.credit_bank_account_destiny),
            value = "",
            placeHolder = stringResource(id = R.string.credit_bank_select)
        )

        CustomDropdown(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .focusable(false)
                .padding(top = 16.dp),
            items = arrayListOf(),
            onValueChange = {},
            labelText = stringResource(id = R.string.credit_bank_account_type),
            value = "",
            placeHolder = stringResource(id = R.string.credit_bank_select)
        )
    }
}