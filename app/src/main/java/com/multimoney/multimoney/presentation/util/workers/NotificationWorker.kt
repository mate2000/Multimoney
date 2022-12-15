package com.multimoney.multimoney.presentation.util.workers

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
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
        Log.d("TestLog", "NotificationWorker")
        val bitmapIcon = BitmapFactory.decodeResource(
            appContext.resources,
            workerParams.inputData.getInt("largeIcon", 0)
        )

        displayLocalNotification(
            workerParams.inputData.getString("title") ?: "",
            workerParams.inputData.getString("body") ?: "",
            Intent(),
            appContext,
            bitmapIcon,
            workerParams.inputData.getInt("smallIcon", 0)
        )

        return Result.success()
    }
}

fun startTimedNotification(
    context: Context,
    title: String,
    body: String,
    largeIcon: Int,
    smallIcon: Int
) {
    val worker = OneTimeWorkRequestBuilder<NotificationWorker>().setInitialDelay(10, SECONDS)
    val workData = Data.Builder()
        .putString("title", title)
        .putString("body", body)
        .putInt("largeIcon", largeIcon)
        .putInt("smallIcon", smallIcon)
        .build()

    worker.setInputData(workData)
    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            TIMED_NOTIFICATION_WORKER,
            ExistingWorkPolicy.REPLACE,
            worker.build()
        )
}

const val TIMED_NOTIFICATION_WORKER = "timed_notification_worker"
