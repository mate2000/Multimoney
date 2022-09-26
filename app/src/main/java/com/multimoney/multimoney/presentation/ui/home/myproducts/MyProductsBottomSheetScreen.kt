package com.multimoney.multimoney.presentation.ui.home.myproducts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyProductsBottomSheetScreen(
    shareViewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    CustomModalBottomSheet(
        title = string.quick_action_bottom_sheet_title,
        closeIcon = drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 40.dp)
        ) {
            Text(
                text = "My Products",
                style = Typography.h4,
                color = MultimoneyTheme.colors.text,
                textAlign = TextAlign.Center
            )
        }
    }
}