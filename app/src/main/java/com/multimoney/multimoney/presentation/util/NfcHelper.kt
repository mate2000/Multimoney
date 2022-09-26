package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.nfc.NfcAdapter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NfcHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

    fun isNfcSupported() = nfcAdapter != null
}