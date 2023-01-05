package com.multimoney.multimoney.presentation.ui.crypto.graphics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.util.FilterDateByDays

// date filter with days, week, month and year (DWMY)
@Composable
fun DateFilterDWMYSection(
    selectedDateFilter: Long,
    onDateFilterSelected: (Long) -> Unit
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        FilterDateByDays.values().filter {
            it != FilterDateByDays.LAST_180_DAYS
                    && it != FilterDateByDays.LAST_90_DAYS
                    && it != FilterDateByDays.HOUR
        }.forEach { filterDateByDays ->
            item {
                DateFilterItem(
                    day = filterDateByDays.timeDescription,
                    avbDay = filterDateByDays.timeAbv,
                    days = filterDateByDays.time,
                    isSelected = selectedDateFilter == filterDateByDays.time,
                    onDateFilterSelected = onDateFilterSelected
                )
            }
        }
    }
}

// date filter with days, weeks, 1 month, 3 months, 6 months and a year
@Composable
fun FullDateFilterSection(
    selectedDateFilter: Long,
    onDateFilterSelected: (Long) -> Unit
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        FilterDateByDays.values().forEach { filterDateByDays ->
            item {
                DateFilterItem(
                    day = filterDateByDays.timeDescriptionExtended,
                    avbDay = filterDateByDays.timeAbvExtended,
                    days = filterDateByDays.time,
                    isSelected = selectedDateFilter == filterDateByDays.time,
                    onDateFilterSelected = onDateFilterSelected
                )
            }
        }
    }
}

@Composable
fun DateFilterItem(
    day: String,
    avbDay: String,
    days: Long,
    isSelected: Boolean,
    onDateFilterSelected: (Long) -> Unit
) {

    CustomButton(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = if (isSelected) day else avbDay,
        onClick = {
            onDateFilterSelected(days)
        },
        buttonType = if (isSelected) CustomButtonType.PrimaryQuinary else CustomButtonType.PrimaryQuaternary
    )
}