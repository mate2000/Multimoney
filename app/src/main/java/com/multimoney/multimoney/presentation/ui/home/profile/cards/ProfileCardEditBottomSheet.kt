package com.multimoney.multimoney.presentation.ui.home.profile.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.uielement.CustomModalBottomSheet
import com.multimoney.multimoney.presentation.uielement.CustomThreePointsTextButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileCardEditBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
    onEditClick: (card: CardVisaDirect?) -> Unit,
    onDeleteClick: (card: CardVisaDirect?) -> Unit,
    card: CardVisaDirect?
) {
    CustomModalBottomSheet(
        title = string.profile_cards_bottom_sheet_title,
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
                textResource = if (card?.verified == true) string.profile_cards_bottom_sheet_edit else string.payment_cards_resume_bottom_sheet,
                startIconResource = if (card?.verified == true) drawable.ic_edit_green_background else drawable.ic_verified_bottom_sheet_icon,
                onClick = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                    onEditClick(card)
                }
            )
            if (card?.verified == true) {
                CustomThreePointsTextButton(
                    modifier = Modifier.padding(top = 32.dp),
                    textResource = string.profile_cards_bottom_sheet_delete,
                    startIconResource = drawable.ic_delete_background,
                    onClick = {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                        }
                        onDeleteClick(card)
                    }
                )
            }
        }
    }
}
