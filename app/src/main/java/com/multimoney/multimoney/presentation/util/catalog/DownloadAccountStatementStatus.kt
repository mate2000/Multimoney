package com.multimoney.multimoney.presentation.util.catalog

sealed class DownloadAccountStatementStatus(val value: String) {
    object Download : DownloadAccountStatementStatus("Download")
    object Error : DownloadAccountStatementStatus("Error")
    object Success : DownloadAccountStatementStatus("Success")
}
