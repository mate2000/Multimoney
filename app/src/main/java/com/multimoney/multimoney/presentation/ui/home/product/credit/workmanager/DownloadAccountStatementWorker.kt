package com.multimoney.multimoney.presentation.ui.home.product.credit.workmanager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.credit.QueryAccountStatementUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.DATE_TIME_DOCUMENTS_FORMAT
import com.multimoney.multimoney.presentation.util.catalog.DownloadAccountStatementStatus
import com.multimoney.multimoney.presentation.util.getCurrentDateTimeString
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

@HiltWorker
class DownloadAccountStatementWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val preferences: DataStorePreferences,
    private val queryAccountStatementUseCase: QueryAccountStatementUseCase
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val creditNumber = inputData.getString(CREDIT_NUMBER).orEmpty()
        val user = inputData.getString(USER).orEmpty()
        val idBrand = inputData.getString(ID_BRAND).orEmpty()
        sendNotification(DownloadAccountStatementStatus.Download)
        return if (preferences.getAuthToken().first().isNotEmpty()) {
            queryAccountStatementUseCase.invoke(
                creditNumber = creditNumber,
                user = user,
                idBrand = idBrand.toInt()
            ).collectLatest { result ->
                result.onSuccess {
                    it?.bytePdf?.let { bytePdf ->
                        getFileDownloadUri(bytePdf)?.let { uri ->
                            sendNotification(DownloadAccountStatementStatus.Success, uri)
                            Result.success()
                        } ?: run {
                            sendNotification(DownloadAccountStatementStatus.Error)
                            Result.failure()
                        }
                    } ?: run {
                        sendNotification(DownloadAccountStatementStatus.Error)
                        Result.failure()
                    }
                }.onMessage {
                    sendNotification(DownloadAccountStatementStatus.Error)
                    Result.failure()
                }.onFailure {
                    sendNotification(DownloadAccountStatementStatus.Error)
                    Result.failure()
                }
            }
            Result.success()
        } else {
            sendNotification(DownloadAccountStatementStatus.Error)
            Result.failure()
        }
    }

    private fun getFileDownloadUri(bytePdf: ByteArray): Uri? {
        val privateDir = context.filesDir
        val downloadedFile = downloadToDir(bytePdf, privateDir)
        return copyFileToDownloads(context, downloadedFile)
    }

    private fun downloadToDir(bytePdf: ByteArray, privateDir: File): File {
        val file = File(
            privateDir,
            context.getString(
                R.string.download_account_statement_worker_file_name,
                getCurrentDateTimeString(DATE_TIME_DOCUMENTS_FORMAT)
            )
        )
        file.outputStream().use { output ->
            output.write(bytePdf)
            output.close()
            output.flush()
        }
        return file
    }

    private fun copyFileToDownloads(context: Context, downloadedFile: File): Uri? {
        val resolver = context.contentResolver
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, downloadedFile.name)
                put(MediaStore.MediaColumns.MIME_TYPE, downloadedFile.extension)
                put(MediaStore.MediaColumns.SIZE, downloadedFile.totalSpace)
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val authority = "${context.packageName}$PROVIDER_TYPE"
            val destinyFile = File(downloadDir, downloadedFile.name)
            FileProvider.getUriForFile(context, authority, destinyFile)
        }?.also { downloadedUri ->
            resolver.openOutputStream(downloadedUri).use { outputStream ->
                val brr = ByteArray(BYTE_ARRAY_SIZE)
                var len: Int
                val bufferedInputStream = BufferedInputStream(FileInputStream(downloadedFile.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
        }
        downloadedFile.delete()
        return uri
    }

    /**
     * creating notification channel for android devices with OS equals and higher than Android 8
     */
    private fun createNotificationChannel(notificationManager: NotificationManagerCompat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.download_account_statement_worker_channel_name)
            val importance = IMPORTANCE_HIGH

            val channel = NotificationChannel(DOWNLOAD_ACCOUNT_STATEMENT_CHANNEL_ID, channelName, importance).apply {
                description = context.getString(R.string.download_account_statement_worker_channel_description)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getIntentByUri(uri: Uri?): Intent {
        var intent = Intent()
        uri?.let { uriSafe ->
            intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uriSafe, MIME_TYPE)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } ?: run {
            Toast.makeText(
                context,
                context.getString(R.string.download_account_statement_worker_push_toast_description_error),
                Toast.LENGTH_LONG
            ).show()
        }
        return intent
    }

    @SuppressLint("InlinedApi")
    private fun sendNotification(status: DownloadAccountStatementStatus, uri: Uri? = null) {
        val notificationManager = NotificationManagerCompat.from(context)
        createNotificationChannel(notificationManager)

        var titleTextResource = R.string.empty
        var contentTextResource = R.string.empty
        var isAutoCancel = false

        var intent = Intent()

        when (status) {
            DownloadAccountStatementStatus.Download -> {
                titleTextResource = R.string.download_account_statement_worker_push_title_download
                contentTextResource = R.string.download_account_statement_worker_push_description_download
                isAutoCancel = false
            }
            DownloadAccountStatementStatus.Error -> {
                titleTextResource = R.string.download_account_statement_worker_push_title_error
                contentTextResource = R.string.download_account_statement_worker_push_description_error
                isAutoCancel = true
            }
            DownloadAccountStatementStatus.Success -> {
                titleTextResource = R.string.download_account_statement_worker_push_title_success
                contentTextResource = R.string.download_account_statement_worker_push_description_success
                isAutoCancel = true
                intent = getIntentByUri(uri)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, DOWNLOAD_ACCOUNT_STATEMENT_CHANNEL_ID)
            .apply {
                setSmallIcon(R.drawable.ic_download)
                setContentTitle(context.getString(titleTextResource))
                setContentText(context.getString(contentTextResource))
                priority = NotificationCompat.PRIORITY_HIGH
                setContentIntent(pendingIntent)
                setAutoCancel(isAutoCancel)
            }.build()

        notificationManager.notify(DOWNLOAD_ACCOUNT_STATEMENT_NOTIFICATION_ID, notificationBuilder)
    }

    companion object {
        const val BYTE_ARRAY_SIZE = 1024
        const val DOWNLOAD_ACCOUNT_STATEMENT_CHANNEL_ID = "download_account_statement_channel_id"
        const val DOWNLOAD_ACCOUNT_STATEMENT_NOTIFICATION_ID = 34
        const val DOWNLOAD_ACCOUNT_STATEMENT_WORKER_NAME = "download_account_statement_worker"
        const val PROVIDER_TYPE = ".provider"
        const val MIME_TYPE = "application/pdf"
    }
}
