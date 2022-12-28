package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.font
import com.multimoney.multimoney.presentation.theme.GrayScale600

@Composable
@Preview
fun CustomCardVisaVertical(
    modifier: Modifier = Modifier,
    cardNumberOne: String = "",
    cardNumberTwo: String = "",
    cardNumberThree: String = "",
    cardNumberFour: String = "",
    date: String = "",
    cvv: String = "",
    isTextVisible: Boolean = false
) {
    val forzaFontFamily = FontFamily(
        Font(font.forza)
    )

    val textStyleLabel = TextStyle(
        fontFamily = forzaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 8.sp,
        letterSpacing = (1).sp
    )

    val textStyleSubtitle = TextStyle(
        fontFamily = forzaFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp,
        letterSpacing = (4).sp
    )

    val textStyleBody = TextStyle(
        fontFamily = forzaFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 10.sp,
        letterSpacing = (1.5).sp
    )

    val textColor = if (isSystemInDarkTheme()) {
        GrayScale600
    } else {
        GrayScale600
    }

    ConstraintLayout(modifier) {
        val (
            backgroundRef,
            cardNumberOneRef,
            cardNumberTwoRef,
            cardNumberThreeRef,
            cardNumberFourRef,
            dateLabelRef,
            dateRef,
            cvvLabelRef,
            cvvRef
        ) = createRefs()
        Image(
            painter = painterResource(R.drawable.ic_mm_visa),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.constrainAs(backgroundRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        if (isTextVisible) {
            Text(
                text = cardNumberOne,
                color = textColor,
                style = textStyleSubtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(cardNumberOneRef) {
                        start.linkTo(backgroundRef.start, margin = 12.dp)
                        bottom.linkTo(dateLabelRef.top, margin = 7.dp)
                    }
            )
            Text(
                text = cardNumberTwo,
                color = textColor,
                style = textStyleSubtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(cardNumberTwoRef) {
                        start.linkTo(cardNumberOneRef.end, margin = 8.dp)
                        top.linkTo(cardNumberOneRef.top)
                        bottom.linkTo(cardNumberOneRef.bottom)
                    }
            )
            Text(
                text = cardNumberThree,
                color = textColor,
                style = textStyleSubtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(cardNumberThreeRef) {
                        start.linkTo(cardNumberTwoRef.end, margin = 8.dp)
                        top.linkTo(cardNumberOneRef.top)
                        bottom.linkTo(cardNumberOneRef.bottom)
                    }
            )
            Text(
                text = cardNumberFour,
                color = textColor,
                style = textStyleSubtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(cardNumberFourRef) {
                        start.linkTo(cardNumberThreeRef.end, margin = 8.dp)
                        top.linkTo(cardNumberOneRef.top)
                        bottom.linkTo(cardNumberOneRef.bottom)
                    }
            )
            Text(
                text = stringResource(id = R.string.visa_date_label),
                color = textColor,
                style = textStyleLabel,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .constrainAs(dateLabelRef) {
                        bottom.linkTo(backgroundRef.bottom, margin = 16.dp)
                        start.linkTo(parent.start, margin = 15.dp)
                    }
            )
            Text(
                text = date,
                color = textColor,
                style = textStyleBody,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .constrainAs(dateRef) {
                        start.linkTo(dateLabelRef.end, margin = 12.dp)
                        top.linkTo(dateLabelRef.top)
                        bottom.linkTo(dateLabelRef.bottom)
                    }
            )
            Text(
                text = stringResource(id = R.string.visa_cvv_label),
                color = textColor,
                style = textStyleLabel,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .constrainAs(cvvLabelRef) {
                        bottom.linkTo(backgroundRef.bottom, margin = 16.dp)
                        start.linkTo(dateRef.end, margin = 32.dp)
                    }
            )
            Text(
                text = cvv,
                color = textColor,
                style = textStyleBody,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .constrainAs(cvvRef) {
                        start.linkTo(cvvLabelRef.end, margin = 12.dp)
                        top.linkTo(cvvLabelRef.top)
                        bottom.linkTo(cvvLabelRef.bottom)
                    }
            )
        }
    }
}
