package com.multimoney.multimoney.presentation.util.catalog

sealed class SmartTransferTypes(val id: Int) {
    object SmartToIban : SmartTransferTypes(1)
    object SmartToSmart : SmartTransferTypes(2)
    object SmartToMobile : SmartTransferTypes(3)
    object SmartToOtherBank : SmartTransferTypes(4)
    object IbanToSmart : SmartTransferTypes(5)
    object VisaToSmart : SmartTransferTypes(6)
    object SmartToContact : SmartTransferTypes(7)
}

data class DisplayAccount(
    val sheetLabel: Int,
    val sheetTitle: String? = null,
    val sheetSubtitle: String? = null,
    val sheetSubtitle2: String? = null,
    val sheetTitleResource: Int? = null,
    val sheetSubtitleResource: Int? = null,
    val sheetSubtitleResource2: Int? = null,
    val icon: Int? = null
)
