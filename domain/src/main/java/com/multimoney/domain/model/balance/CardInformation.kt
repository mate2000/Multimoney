package com.multimoney.domain.model.balance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardInformation(
    val cardToken: String?,
    val cardNumber: String?,
    val expDate: String?,
    val holderName: String?,
    val status: String?,
    val blockType: BlockType?,
    val cValidation: String?,
    val type: String?
) : Parcelable
