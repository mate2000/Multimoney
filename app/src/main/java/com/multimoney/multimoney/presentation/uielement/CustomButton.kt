package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultBlack
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.GrayScale200
import com.multimoney.multimoney.presentation.theme.GrayScale400
import com.multimoney.multimoney.presentation.theme.GrayScale500
import com.multimoney.multimoney.presentation.theme.GrayScale700
import com.multimoney.multimoney.presentation.theme.GrayScale800
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Primary200
import com.multimoney.multimoney.presentation.theme.Primary400
import com.multimoney.multimoney.presentation.theme.Primary500
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency10
import com.multimoney.multimoney.presentation.theme.WhiteTransparency12
import com.multimoney.multimoney.presentation.theme.WhiteTransparency20
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiaryUnderLined

/**
 * CustomButton: Button to match design style across the whole app, in order to use it.
 *
 * Parameters:
 * @param modifier: Apply style.
 * @param buttonType: Button type sealed class to change color.
 * @param text: Set button text.
 * @param enable: Enable button.
 * @param enableArrowIcon: Display arrow icon.
 * @param onClick: Action to perform on click triggered.
 */

@Composable
@Preview
fun CustomButton(
    modifier: Modifier = Modifier,
    buttonType: CustomButtonType = CustomButtonType.PrimaryPrimary,
    text: String = stringResource(id = R.string.button_continue),
    enable: Boolean = true,
    visible: Boolean = true,
    elevation: ButtonElevation = ButtonDefaults.elevation(),
    enableArrowIcon: Boolean = false,
    trailingIcon: Int? = null,
    onClick: () -> Unit = {}
) {
    // Handle pressed state
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Parameters
    lateinit var buttonColor: ButtonColors
    val arrowIconTint: Color
    var borderStroke: BorderStroke? = null

    val textColor: Color
    var underLined = false

    when (buttonType) {
        CustomButtonType.PrimaryPrimary -> {
            if (isSystemInDarkTheme()) {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        Primary500
                    } else {
                        Primary400
                    },
                    disabledBackgroundColor = WhiteTransparency20,
                    disabledContentColor = GrayScale500
                )
                textColor = GrayScale800
                arrowIconTint = if (enable) {
                    GrayScale800
                } else {
                    GrayScale500
                }
            } else {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        Primary400
                    } else {
                        Primary500
                    },
                    disabledBackgroundColor = WhiteTransparency20,
                    disabledContentColor = GrayScale500
                )
                textColor = GrayScale800
                arrowIconTint = if (enable) {
                    GrayScale800
                } else {
                    GrayScale500
                }
            }
        }
        CustomButtonType.PrimarySecondary -> {
            if (isSystemInDarkTheme()) {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        WhiteTransparency10
                    } else {
                        DefaultBlack
                    },
                    disabledBackgroundColor = DefaultBlack,
                    disabledContentColor = GrayScale400
                )
                textColor = DefaultWhite
                if (enable) {
                    borderStroke = BorderStroke(1.dp, Primary400)
                    arrowIconTint = DefaultWhite
                } else {
                    borderStroke = BorderStroke(1.dp, GrayScale400)
                    arrowIconTint = GrayScale400
                }
            } else {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        Primary400
                    } else {
                        DefaultWhite
                    },
                    disabledBackgroundColor = DefaultWhite,
                    disabledContentColor = GrayScale400
                )
                textColor = Primary500
                if (enable) {
                    borderStroke = BorderStroke(1.dp, Primary400)
                    arrowIconTint = Primary500
                } else {
                    borderStroke = BorderStroke(1.dp, GrayScale400)
                    arrowIconTint = GrayScale400
                }
            }
        }
        CustomButtonType.PrimaryTertiary -> {
            if (isSystemInDarkTheme()) {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        WhiteTransparency12
                    } else {
                        Transparent
                    },
                    disabledBackgroundColor = Transparent,
                    disabledContentColor = GrayScale400
                )
                textColor = Primary400
                arrowIconTint = if (enable) {
                    Primary400
                } else {
                    GrayScale400
                }
            } else {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        Primary200
                    } else {
                        DefaultWhite
                    },
                    disabledBackgroundColor = DefaultWhite,
                    disabledContentColor = GrayScale400
                )
                textColor = Primary500
                arrowIconTint = if (enable) {
                    Primary500
                } else {
                    GrayScale400
                }
            }
        }
        PrimaryTertiaryUnderLined -> {
            if (isSystemInDarkTheme()) {
                underLined = true
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        WhiteTransparency12
                    } else {
                        Transparent
                    },
                    disabledBackgroundColor = Transparent,
                    disabledContentColor = GrayScale400
                )
                textColor = Primary400
                arrowIconTint = if (enable) {
                    DefaultWhite
                } else {
                    GrayScale400
                }
            } else {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        Primary200
                    } else {
                        DefaultWhite
                    },
                    disabledBackgroundColor = DefaultWhite,
                    disabledContentColor = GrayScale400
                )
                textColor = Primary500
                arrowIconTint = if (enable) {
                    Primary500
                } else {
                    GrayScale400
                }
            }
        }
        CustomButtonType.PrimaryQuaternary -> {
            if (isSystemInDarkTheme()) {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        WhiteTransparency12
                    } else {
                        GrayScale700
                    },
                    disabledBackgroundColor = GrayScale700,
                    disabledContentColor = GrayScale700,
                )
                textColor = DefaultWhite
                arrowIconTint = if (enable) {
                    DefaultWhite
                } else {
                    GrayScale700
                }
            } else {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        WhiteTransparency12
                    } else {
                        DefaultWhite
                    },
                    disabledBackgroundColor = DefaultWhite,
                    disabledContentColor = GrayScale400,
                )
                textColor = DefaultBlack
                arrowIconTint = if (enable) {
                    GrayScale700
                } else {
                    GrayScale400
                }
            }
        }
        else -> {
            if (isSystemInDarkTheme()) {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        WhiteTransparency12
                    } else {
                        DefaultBlack
                    },
                    disabledBackgroundColor = DefaultBlack,
                    disabledContentColor = GrayScale400
                )
                textColor = DefaultWhite
                arrowIconTint = if (enable) {
                    DefaultWhite
                } else {
                    GrayScale400
                }
            } else {
                buttonColor = ButtonDefaults.buttonColors(
                    backgroundColor = if (isPressed) {
                        Primary200
                    } else {
                        Primary500
                    },
                    disabledBackgroundColor = Primary500,
                    disabledContentColor = GrayScale200
                )
                textColor = DefaultWhite
                arrowIconTint = if (enable) {
                    Primary500
                } else {
                    GrayScale200
                }
            }
        }
    }

    if (visible) {
        Button(
            modifier = modifier,
            onClick = onClick,
            colors = buttonColor,
            shape = RoundedCornerShape(50),
            border = borderStroke,
            elevation = elevation,
            enabled = enable,
            interactionSource = interactionSource
        ) {
            Text(
                text = text,
                style = if (underLined) {
                    Typography.button.copy(textDecoration = TextDecoration.Underline)
                } else {
                    Typography.button
                },
                color = textColor
            )
            if (enableArrowIcon) {
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "",
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    tint = arrowIconTint
                )
            } else if (trailingIcon != null) {
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = "",
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    tint = arrowIconTint
                )
            }
        }
    }
}

sealed class CustomButtonType() {
    object PrimaryPrimary : CustomButtonType()
    object PrimarySecondary : CustomButtonType()
    object PrimaryTertiary : CustomButtonType()
    object PrimaryTertiaryUnderLined : CustomButtonType()
    object PrimaryQuaternary : CustomButtonType()
}