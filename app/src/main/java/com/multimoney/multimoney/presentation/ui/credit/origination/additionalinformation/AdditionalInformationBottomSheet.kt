package com.multimoney.multimoney.presentation.ui.credit.origination.additionalinformation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdditionalInformationBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    onButtonClick: () -> Unit
) {
    CustomModalBottomSheet(
        title = R.string.credit_additional_information_bottom_sheet_title,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Row {
                Image(modifier = Modifier.padding(top = 2.dp), painter = painterResource(id = R.drawable.info_blue_icon), contentDescription = "info icon")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.credit_additional_information_bottom_sheet_description),
                    style = Typography.body2,
                    color = MultimoneyTheme.colors.quickActionLabelColor,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            CustomButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = {
                    onButtonClick()
                },
                text = stringResource(id = R.string.credit_additional_information_bottom_sheet_button_),
                buttonType = CustomButtonType.PrimaryPrimary
            )
        }
    }
}
