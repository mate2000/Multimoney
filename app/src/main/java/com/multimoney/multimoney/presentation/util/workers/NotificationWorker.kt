package com.multimoney.multimoney.presentation.util.workers

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.multimoney.multimoney.presentation.ui.MainActivity
import com.multimoney.multimoney.presentation.util.displayLocalNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit.SECONDS

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        displayLocalNotification(
            title = workerParams.inputData.getString(TITLE_PARAM) ?: "",
            body = workerParams.inputData.getString(BODY_PARAM) ?: "",
            intent = Intent(appContext, MainActivity::class.java),
            context = appContext,
            smallIconResource = workerParams.inputData.getInt(SMALL_ICON_PARAM, 0)
        )

        return Result.success()
    }
}

fun startTimedNotification(
    context: Context,
    title: String,
    body: String,
    smallIcon: Int
) {
    val worker = OneTimeWorkRequestBuilder<NotificationWorker>().setInitialDelay(10, SECONDS)
    val workData = Data.Builder()
        .putString(TITLE_PARAM, title)
        .putString(BODY_PARAM, body)
        .putInt(SMALL_ICON_PARAM, smallIcon)
        .build()

    worker.setInputData(workData)
    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            TIMED_NOTIFICATION_WORKER,
            ExistingWorkPolicy.REPLACE,
            worker.build()
        )
}

const val TITLE_PARAM = "title"
const val BODY_PARAM = "body"
const val SMALL_ICON_PARAM = "title"
const val TIMED_NOTIFICATION_WORKER = "timed_notification_worker"
