package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.Typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomModalBottomSheet(
    title: Int,
    closeIcon: Int,
    closeAction: () -> Unit = {},
    closeIconVisible: Boolean = true,
    shape: Shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    modalBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    content: @Composable () -> Unit
) {
    val backgroundColor: Color
    val titleColor: Color
    if (isSystemInDarkTheme()) {
        backgroundColor = GrayScale700
        titleColor = DefaultWhite
    } else {
        backgroundColor = DefaultWhite
        titleColor = GrayScale600
    }

    ModalBottomSheetLayout(
        sheetBackgroundColor = Color.Transparent,
        sheetState = modalBottomSheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        backgroundColor,
                        shape = shape
                    )
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 24.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = if (closeIconVisible) {
                            Arrangement.SpaceBetween
                        } else {
                            Arrangement.Center
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = title),
                            style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                            color = titleColor
                        )
                        if (closeIconVisible) {
                            Image(
                                painter = painterResource(id = closeIcon),
                                contentDescription = "",
                                modifier = Modifier.clickable {
                                    coroutineScope.launch {
                                        closeAction()
                                        modalBottomSheetState.hide()
                                    }
                                }
                            )
                        }
                    }
                    content()
                }
            }
        }
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(backgroundColor)
        )
    }
}
