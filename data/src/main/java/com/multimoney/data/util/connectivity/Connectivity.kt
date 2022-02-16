package com.multimoney.data.util.connectivity

import androidx.lifecycle.LiveData

interface Connectivity {
    val hasNetworkAccess: LiveData<Boolean>
}
