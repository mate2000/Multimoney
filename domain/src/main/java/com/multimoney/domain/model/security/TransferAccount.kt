package com.multimoney.domain.model.security

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransferAccount(
    val account: String?,
    val bank: String?,
    val typeTransfer: String?,
    val beneficiaryName: String?
) : Parcelable
