package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.DefaultWhite
import com.multimoney.multimoney.presentation.theme.Primary200
import com.multimoney.multimoney.presentation.theme.Typography

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
    name: String = "",
    isTextVisible: Boolean = false
) {
    ConstraintLayout(modifier) {
        val (
            backgroundRef,
            cardNumberOneRef,
            dividerOneRef,
            cardNumberTwoRef,
            dividerTwoRef,
            cardNumberThreeRef,
            dividerThreeRef,
            cardNumberFourRef,
            dateRef,
            dateLabelRef,
            cvvRef,
            cvvLabelRef,
            nameRef) = createRefs()
        Image(
            painter = painterResource(R.drawable.ic_mm_visa),
            contentDescription = "",
            contentScale = ContentScale.Inside,
            modifier = Modifier.constrainAs(backgroundRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        if (isTextVisible) {
            Text(
                text = cardNumberOne,
                color = DefaultWhite,
                style = Typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(cardNumberOneRef) {
                        top.linkTo(backgroundRef.top, margin = 24.dp)
                        start.linkTo(dividerOneRef.start)
                        end.linkTo(dividerOneRef.end)
                        width = Dimension.fillToConstraints
                    }
            )
            Image(
                painter = painterResource(R.drawable.ic_visa_divider),
                contentDescription = "",
                contentScale = ContentScale.Inside,
                modifier = Modifier.constrainAs(dividerOneRef) {
                    top.linkTo(cardNumberOneRef.bottom)
                    end.linkTo(backgroundRef.end, margin = 24.dp)
                }
            )
            Text(
                text = cardNumberTwo,
                color = DefaultWhite,
                style = Typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(cardNumberTwoRef) {
                        top.linkTo(dividerOneRef.bottom)
                        start.linkTo(dividerOneRef.start)
                        end.linkTo(dividerOneRef.end)
                        width = Dimension.fillToConstraints
                    }
            )
            Image(
                painter = painterResource(R.drawable.ic_visa_divider),
                contentDescription = "",
                contentScale = ContentScale.Inside,
                modifier = Modifier.constrainAs(dividerTwoRef) {
                    top.linkTo(cardNumberTwoRef.bottom)
                    start.linkTo(dividerOneRef.start)
                    end.linkTo(dividerOneRef.end)
                }
            )
            Text(
                text = cardNumberThree,
                color = DefaultWhite,
                style = Typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(cardNumberThreeRef) {
                        top.linkTo(dividerTwoRef.bottom)
                        start.linkTo(dividerOneRef.start)
                        end.linkTo(dividerOneRef.end)
                        width = Dimension.fillToConstraints
                    }
            )
            Image(
                painter = painterResource(R.drawable.ic_visa_divider),
                contentDescription = "",
                contentScale = ContentScale.Inside,
                modifier = Modifier.constrainAs(dividerThreeRef) {
                    top.linkTo(cardNumberThreeRef.bottom)
                    start.linkTo(dividerOneRef.start)
                    end.linkTo(dividerOneRef.end)
                }
            )
            Text(
                text = cardNumberFour,
                color = DefaultWhite,
                style = Typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(cardNumberFourRef) {
                        top.linkTo(dividerThreeRef.bottom)
                        start.linkTo(dividerOneRef.start)
                        end.linkTo(dividerOneRef.end)
                        width = Dimension.fillToConstraints
                    }
            )
            Text(
                text = stringResource(id = R.string.visa_date_label),
                color = Primary200,
                style = Typography.caption,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .constrainAs(dateLabelRef) {
                        top.linkTo(cardNumberFourRef.bottom)
                        start.linkTo(dividerOneRef.start)
                        end.linkTo(dateRef.start)
                        width = Dimension.fillToConstraints
                    }
            )
            Text(
                text = date,
                color = DefaultWhite,
                style = Typography.caption,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .constrainAs(dateRef) {
                        top.linkTo(dateLabelRef.top)
                        bottom.linkTo(dateLabelRef.bottom)
                        start.linkTo(dateLabelRef.end)
                        end.linkTo(dividerOneRef.end)
                        width = Dimension.fillToConstraints
                    })
            Text(
                text = stringResource(id = R.string.visa_cvv_label),
                color = Primary200,
                style = Typography.caption,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .constrainAs(cvvLabelRef) {
                        top.linkTo(dateLabelRef.bottom, margin = 14.dp)
                        start.linkTo(dividerOneRef.start)
                        end.linkTo(cvvRef.start)
                        width = Dimension.fillToConstraints
                    })
            Text(
                text = cvv,
                color = DefaultWhite,
                style = Typography.caption,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .constrainAs(cvvRef) {
                        top.linkTo(cvvLabelRef.top)
                        bottom.linkTo(cvvLabelRef.bottom)
                        start.linkTo(cvvLabelRef.end)
                        end.linkTo(dividerOneRef.end)
                        width = Dimension.fillToConstraints
                    })
            Text(
                text = name,
                color = Primary200,
                style = Typography.body1,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .constrainAs(nameRef) {
                        top.linkTo(cvvLabelRef.bottom, margin = 14.dp)
                        end.linkTo(dividerOneRef.end)
                    }
                    .wrapContentSize())
        }
    }
}