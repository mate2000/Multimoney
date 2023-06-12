package com.multimoney.data.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat

object ClipboardUtil {
    fun copy(context: Context, text: String) {
        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        clipboard?.setPrimaryClip(
            ClipData.newPlainText("Crypto Address", text)
        )
    }
}