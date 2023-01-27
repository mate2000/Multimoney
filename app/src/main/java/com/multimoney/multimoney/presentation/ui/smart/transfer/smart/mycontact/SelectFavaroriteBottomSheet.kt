package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontact

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
fun SelectFavoriteContactBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    onSelectClick: () -> Unit,
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
                title = stringResource(R.string.smart_my_contacts_select_favorite_account),
                startIcon = R.drawable.ic_star,
                onClick = {
                    coroutineScope.launch {
                        onSelectClick()
                        modalBottomSheetState.hide()
                    }
                }
            )
        }
        BackHandler { onBackClick() }
    }
}