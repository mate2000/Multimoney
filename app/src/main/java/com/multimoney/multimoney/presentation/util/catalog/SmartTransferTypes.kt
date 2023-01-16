package com.multimoney.multimoney.presentation.util.catalog

sealed class SmartTransferTypes(val id: Int) {
    object SmartToIban : SmartTransferTypes(1)
    object SmartToSmart : SmartTransferTypes(2)
    object SmartToMobile : SmartTransferTypes(3)
    object SmartToOtherBank : SmartTransferTypes(4)
    object IbanToSmart : SmartTransferTypes(5)
    object VisaToSmart : SmartTransferTypes(6)

    object Labels {
        fun getBottomSheetLabel(id: Int) = when (id) {
            SmartToIban.id -> {}
            SmartToSmart.id -> {}
            SmartToMobile.id -> {}
            SmartToOtherBank.id -> {}
            IbanToSmart.id -> {}
            VisaToSmart.id -> {}
            else -> {}
        }

        fun getSuccessSheetLabel(id: Int) = when (id) {
            SmartToIban.id -> {}
            SmartToSmart.id -> {}
            SmartToMobile.id -> {}
            SmartToOtherBank.id -> {}
            IbanToSmart.id -> {}
            VisaToSmart.id -> {}
            else -> {}
        }
    }
}

data class DisplayAccount(
    val sheetLabel: Int,
    val sheetTitle: String? = null,
    val sheetSubtitle: String? = null,
    val sheetTitleResource: Int? = null,
    val sheetSubtitleResource: Int? = null,
    val icon: Int
)
