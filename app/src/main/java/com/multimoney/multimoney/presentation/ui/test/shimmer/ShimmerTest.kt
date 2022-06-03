package com.multimoney.multimoney.presentation.ui.test.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.GrayScale300
import com.multimoney.multimoney.presentation.uielement.ShimmerBoxView

@Composable
fun ShimmerTest() {
    ShimmerBoxView {
        Column {
            repeat(3) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(
                        modifier = Modifier
                            .size(128.dp)
                            .background(GrayScale300)
                    )
                    Column(
                        Modifier
                            .padding(start = 18.dp)
                    ) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(GrayScale300)
                        )
                        Spacer(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(GrayScale300)

                        )
                        Spacer(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(GrayScale300)
                        )
                    }
                }
            }
        }
    }
}
