package com.multimoney.domain.model.virtualcard

import android.os.Parcelable
import com.multimoney.domain.model.util.error.MessageError
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayCreditVisaDirect(
    val paymentDocument: String?,
    val paymentStatus: String?,
    val paymentAmount: Double?,
    val ticket: String?,
    val referenceAuthorization: String?,
    val nextPayDate: String?,
    val quotaNumber: Long?,
    val observations: String?,
    val referenceAuthorizationVisa: String?,
    val messageError: MessageError
) : Parcelable
