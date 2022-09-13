package com.multimoney.multimoney.presentation.ui.credit.creditamount.skeleton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.solver.widgets.Optimizer
import com.multimoney.multimoney.presentation.uielement.ShimmerBoxView
import com.multimoney.multimoney.presentation.uielement.ShimmerItemView

@Composable
@Preview(heightDp = 800)
fun CreditAmountScreenSkeleton() {
    ShimmerBoxView {
        Column(Modifier.fillMaxSize()) {
            ConstraintLayout(
                modifier = Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp),
                optimizationLevel = Optimizer.OPTIMIZATION_DIRECT
            ) {
                val (
                    titleOne, titleTwo, input, sliderLine, sliderSelectionCircle, sliderMinAmount, sliderMaxAmount,
                    dividerOne, chip, iconOne, textInfoOne, iconTwo, textInfoTwo, iconThree, textInfoThree,
                ) = createRefs()

                ShimmerItemView(
                    modifier = Modifier
                        .size(120.dp, height = 30.dp)
                        .constrainAs(titleOne) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        }
                )
                ShimmerItemView(
                    modifier = Modifier
                        .size(160.dp, height = 30.dp)
                        .constrainAs(titleTwo) {
                            start.linkTo(parent.start)
                            top.linkTo(titleOne.bottom, margin = 4.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .constrainAs(input) {
                            top.linkTo(titleTwo.bottom, margin = 42.dp)
                        },
                    radius = 40.dp
                )

                ShimmerItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .constrainAs(sliderLine) {
                            top.linkTo(input.bottom, margin = 50.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(32.dp)
                        .constrainAs(sliderSelectionCircle) {
                            start.linkTo(parent.start)
                            top.linkTo(sliderLine.top)
                            bottom.linkTo(sliderLine.bottom)
                        },
                    radius = 16.dp
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(34.dp, 18.dp)
                        .constrainAs(sliderMinAmount) {
                            start.linkTo(parent.start)
                            top.linkTo(sliderLine.bottom, margin = 18.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(50.dp, 18.dp)
                        .constrainAs(sliderMaxAmount) {
                            end.linkTo(parent.end)
                            top.linkTo(sliderMinAmount.top)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .constrainAs(dividerOne) {
                            top.linkTo(sliderMinAmount.bottom, margin = 32.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(138.dp, 36.dp)
                        .constrainAs(chip) {
                            start.linkTo(parent.start)
                            top.linkTo(dividerOne.bottom, margin = 16.dp)
                        },
                    radius = 24.dp
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(16.dp, 16.dp)
                        .constrainAs(iconOne) {
                            start.linkTo(parent.start)
                            top.linkTo(chip.bottom, margin = 12.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(140.dp, 20.dp)
                        .constrainAs(textInfoOne) {
                            top.linkTo(iconOne.top)
                            start.linkTo(iconOne.end, margin = 8.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(16.dp, 16.dp)
                        .constrainAs(iconTwo) {
                            start.linkTo(parent.start)
                            top.linkTo(iconOne.bottom, margin = 12.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(110.dp, 20.dp)
                        .constrainAs(textInfoTwo) {
                            top.linkTo(iconTwo.top)
                            start.linkTo(iconTwo.end, margin = 8.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(16.dp, 16.dp)
                        .constrainAs(iconThree) {
                            start.linkTo(parent.start)
                            top.linkTo(iconTwo.bottom, margin = 12.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(120.dp, 20.dp)
                        .constrainAs(textInfoThree) {
                            top.linkTo(iconThree.top)
                            start.linkTo(iconThree.end, margin = 8.dp)
                        }
                )

            }
            ConstraintLayout(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {

                val (iconFour, textInfoFour, dividerTwo, termsAndConditionOne, termsAndConditionTwo) = createRefs()

                ShimmerItemView(
                    modifier = Modifier
                        .size(16.dp, 16.dp)
                        .constrainAs(iconFour) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top, margin = 12.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(180.dp, 20.dp)
                        .constrainAs(textInfoFour) {
                            top.linkTo(iconFour.top)
                            start.linkTo(iconFour.end, margin = 8.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .constrainAs(dividerTwo) {
                            top.linkTo(iconFour.bottom, margin = 16.dp)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .size(18.dp)
                        .constrainAs(termsAndConditionOne) {
                            top.linkTo(dividerTwo.bottom, margin = 34.dp)
                            start.linkTo(parent.start)
                        }
                )

                ShimmerItemView(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth(0.90f)
                        .constrainAs(termsAndConditionTwo) {
                            start.linkTo(termsAndConditionOne.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            top.linkTo(termsAndConditionOne.top)
                        }
                )
            }
        }
    }
}