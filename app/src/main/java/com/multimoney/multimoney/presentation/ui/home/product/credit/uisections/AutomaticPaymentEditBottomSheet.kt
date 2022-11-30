package com.multimoney.multimoney.presentation.ui.home.product.credit.uisections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomThreePointsTextButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AutomaticPaymentEditBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    CustomModalBottomSheet(
        title = string.automatic_payment_edit_bottom_sheet_title,
        closeIcon = drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 16.dp)
        ) {
            CustomThreePointsTextButton(
                textResource = R.string.automatic_payment_edit_bottom_sheet_edit,
                startIconResource = R.drawable.ic_edit_green_background,
                onClick = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                    onEditClick()
                }
            )
            CustomThreePointsTextButton(
                modifier = Modifier.padding(top = 32.dp),
                textResource = R.string.automatic_payment_edit_bottom_sheet_delete,
                startIconResource = R.drawable.ic_delete_background,
                onClick = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                    onDeleteClick()
                }
            )
        }
    }
}
