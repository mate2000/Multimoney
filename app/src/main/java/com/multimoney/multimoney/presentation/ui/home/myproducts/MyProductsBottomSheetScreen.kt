package com.multimoney.multimoney.presentation.ui.home.myproducts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyProductsBottomSheetScreen(
    shareViewModel: HomeViewModel,
    modalBottomSheetState: ModalBottomSheetState
) {
    ModalBottomSheetLayout(
        sheetBackgroundColor = Color.Transparent,
        sheetState = modalBottomSheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MultimoneyTheme.colors.bottomSheetBackgroundColor,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
            ) {
               Column(Modifier.padding(top = 20.dp)) {
                   Text(text = )
               }
            }
        },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(MultimoneyTheme.colors.bottomSheetBackgroundColor)
        )
    }
}