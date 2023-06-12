package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.GrayScale600
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomModalWarningBottomSheet(
    titleResource: Int = R.string.empty,
    titleText: String = "",
    descriptionResource: Int = R.string.empty,
    descriptionText: AnnotatedString = buildAnnotatedString { },
    buttonTextResource: Int = R.string.understood,
    buttonText: String = "",
    enableButton: Boolean = true,
    onButtonClick: () -> Unit = {},
    closeIcon: Int = R.drawable.ic_close_bottom_sheet,
    closeAction: () -> Unit = {},
    titleIsVisible: Boolean = true,
    descriptionIsVisible: Boolean = true,
    buttonIsVisible: Boolean = true,
    closeIconVisible: Boolean = true,
    shape: Shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    modalBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope
) {
    val backgroundColor: Color
    val textColor: Color
    if (isSystemInDarkTheme()) {
        backgroundColor = GrayScale700
        textColor = WhiteTransparency70
    } else {
        backgroundColor = GrayScale700
        textColor = WhiteTransparency70
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
                        .padding(top = 16.dp, start = 24.dp, end = 16.dp, bottom = 24.dp)
                ) {
                    if (closeIconVisible) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                        Column(modifier = Modifier.padding(end = 8.dp)) {
                            if (titleIsVisible) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    modifier = Modifier.wrapContentHeight(),
                                    text = if (titleText != "") titleText else stringResource(id = titleResource),
                                    style = Typography.h4.copy(fontWeight = FontWeight.Black),
                                    color = textColor
                                )
                            }
                            if (descriptionIsVisible) {
                                Spacer(modifier = Modifier.height(24.dp))
                                if (descriptionText.isNotEmpty()) {
                                    Text(
                                        modifier = Modifier.wrapContentHeight(),
                                        text = descriptionText,
                                        color = textColor
                                    )
                                } else {
                                    Text(
                                        text = stringResource(id = descriptionResource),
                                        color = textColor
                                    )
                                }
                            }
                            if (buttonIsVisible) {
                                Spacer(modifier = Modifier.height(40.dp))
                                CustomButton(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .fillMaxWidth(),
                                    onClick = {
                                        onButtonClick()
                                        coroutineScope.launch {
                                            modalBottomSheetState.hide()
                                        }
                                    },
                                    text = if (buttonText != "") titleText else stringResource(id = buttonTextResource),
                                    buttonType = CustomButtonType.PrimaryPrimary,
                                    enable = enableButton
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
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
