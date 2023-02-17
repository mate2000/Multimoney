package com.multimoney.data.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

object ShareUtil {
    fun createIntent(context: Context, text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(
            context,
            Intent.createChooser(shareIntent, "Share via"),
            null
        )
    }
}