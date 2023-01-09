package com.multimoney.multimoney.presentation.uielement

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
            settings.javaScriptEnabled = true
            loadUrl(url)
        }
    })
}

@Composable
fun MmWebViewHtml(html: String, context: Context) {
    AndroidView(factory = {
        WebView(context).apply {
            webViewClient = WebViewClient()
            loadData(formatHtmlToDarkMode(html), "text/html", "UTF-8")
        }
    })
}

private fun formatHtmlToDarkMode(htmlString: String): String =
    HTML_PLACEHOLDER.replace(BODY_PLACEHOLDER, htmlString)

private const val BODY_PLACEHOLDER = "BODY_PLACEHOLDER"

/* A string that is used to format the html string to dark mode. */
private const val HTML_PLACEHOLDER = "<!DOCTYPE html>\n" +
        "<html>\n" +
        "  <head>\n" +
        "    <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Poppins\">\n" +
        "    <style>\n" +
        "      * {\n" +
        "        font-family: \"Poppins\", sans-serif !important;\n" +
        "        text-align: left !important;\n" +
        "      }\n" +
        "\n" +
        "      body {\n" +
        "        background-color: black !important;\n" +
        "      }\n" +
        "\n" +
        "      div {\n" +
        "        color: rgba(255, 255, 255, 0.9) !important;\n" +
        "      }\n" +
        "\n" +
        "      p {\n" +
        "        color: rgba(255, 255, 255, 0.7) !important;\n" +
        "        margin: 0in 0pt 0pt !important;" +
        "      }\n" +
        "    </style>\n" +
        "  </head>\n" +
        "  <body>$BODY_PLACEHOLDER</body>\n" +
        "</html>"
