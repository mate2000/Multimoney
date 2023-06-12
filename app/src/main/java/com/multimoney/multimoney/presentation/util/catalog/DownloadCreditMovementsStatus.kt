package com.multimoney.multimoney.presentation.util.catalog

sealed class DownloadCreditMovementsStatus(val value: String) {
    object Download : DownloadCreditMovementsStatus("Download")
    object Error : DownloadCreditMovementsStatus("Error")
    object Success : DownloadCreditMovementsStatus("Success")
}
