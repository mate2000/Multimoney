package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MmWebView(url: String, context: Context) {
    AndroidView(factory = {
        WebView(context).apply {
            webViewClient = WebViewClient()

            loadUrl(url)
        }
    })
}