package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NfcHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

    fun isNfcSupported() = nfcAdapter != null

    fun isNfcEnabled(): Boolean {
        return if (isNfcSupported()) {
            nfcAdapter.isEnabled
        } else {
            false
        }
    }

    fun getIntentToRequestActivateNfc(): Intent {
        return Intent(Settings.ACTION_NFC_SETTINGS)
    }
}
