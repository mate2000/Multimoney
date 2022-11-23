package com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.uielement.SimpleItemRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditBeneficiaryBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    CustomModalBottomSheet(
        title = R.string.common_options,
        closeIcon = R.drawable.ic_close_bottom_sheet,
        modalBottomSheetState = modalBottomSheetState,
        coroutineScope = coroutineScope
    ) {
        Column {
            SimpleItemRow(
                title = stringResource(R.string.smart_account_edit_beneficiary),
                startIcon = R.drawable.ic_edit_bg,
                onClick = {
                    coroutineScope.launch {
                        onEditClick()
                        modalBottomSheetState.hide()
                    }
                }
            )
            SimpleItemRow(
                title = stringResource(R.string.smart_account_remove_beneficiary),
                startIcon = R.drawable.ic_delete_bg,
                onClick = {
                    coroutineScope.launch {
                        onRemoveClick()
                        modalBottomSheetState.hide()
                    }
                }
            )
        }
        BackHandler { onBackClick() }
    }
}
