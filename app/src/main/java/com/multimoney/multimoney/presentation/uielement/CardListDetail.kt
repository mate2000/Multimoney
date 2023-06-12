package com.multimoney.multimoney.presentation.uielement

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography

@Composable
@Preview
fun CardListDetail(
    titleResource: Int = R.string.empty,
    listItems: List<CardVisaDirect?> = listOf(),
    onEndIconClick: (CardVisaDirect?) -> Unit = {},
    requireIcon: Boolean = false
) {
    Text(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        text = stringResource(id = titleResource),
        style = Typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
        color = MultimoneyTheme.colors.subTitleText,
        textAlign = TextAlign.Left
    )
    LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        items(listItems) { card ->
            CustomInfoButton(
                modifier = Modifier
                    .fillMaxWidth(),
                imageModifier = Modifier.size(48.dp),
                startIcon = R.drawable.ic_visa_card_item,
                title = card?.detail ?: "",
                subtitle = stringResource(
                    id = R.string.visa_card_masked_number,
                    card?.cardMaskedNumber?.takeLast(4) ?: 0
                ),
                titleIcon = if (requireIcon) R.drawable.ic_green_warning else null,
                endIcon = R.drawable.ic_option_points,
                onEndIconClick = {
                    onEndIconClick(card)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
