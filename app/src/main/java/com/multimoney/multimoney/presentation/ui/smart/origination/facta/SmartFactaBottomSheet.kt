package com.multimoney.multimoney.presentation.ui.smart.origination.facta

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnClickBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartFactaBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    viewModel: SmartViewModel
) {
    CustomModalBottomSheet(
        title = R.string.smart_facta_article_15_info_title,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(R.drawable.info_blue_icon),
                    contentDescription = stringResource(R.string.info),
                    tint = MultimoneyTheme.colors.textInformation,
                    modifier = Modifier.padding(end = 8.dp, top = 16.dp)
                )
                Text(
                    text = stringResource(R.string.smart_facta_article_15_info),
                    color = GrayScale500,
                    style = Typography.subtitle1,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            CustomButton(
                onClick = { viewModel.onUIEvent(OnClickBottomSheet) },
                text = stringResource(id = R.string.button_continue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = PrimaryPrimary
            )
        }
        if (viewModel.uiState.bottomModalSheet.isVisible) {
            BackHandler {
                viewModel.onUIEvent(OnClickBottomSheet)
            }
        }
    }
}
