package com.multimoney.domain.model.accountsmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RelatedContact(val phoneNumber: String) : Parcelable
