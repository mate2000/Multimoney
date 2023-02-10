package com.multimoney.multimoney.presentation.util

import com.multimoney.data.util.DataStorePreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CryptoHelper @Inject constructor(private val dataStorePreferences: DataStorePreferences) {

    suspend fun saveCryptoOrigin(origin: String) {
        dataStorePreferences.saveCryptoOrigin(origin)
    }

    suspend fun getCryptoOrigin() = dataStorePreferences.getCryptoOrigin().first()

    suspend fun saveEnableCryptoTransfer(enable: Boolean) {
        dataStorePreferences.saveEnableCryptoTransfer(enable)
    }

    suspend fun isCryptoTransferEnabled() = dataStorePreferences.isCryptoTransferEnabled().first()
}
