package com.multimoney.multimoney.presentation.ui.home.product.credit.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.credit.QueryAccountStatementUseCase
import javax.inject.Inject

class DownloadAccountStatementWorkerProvider @Inject constructor(
    private val preferences: DataStorePreferences,
    private val queryAccountStatementUseCase: QueryAccountStatementUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return DownloadAccountStatementWorker(
            context = appContext,
            workerParameters = workerParameters,
            preferences = preferences,
            queryAccountStatementUseCase = queryAccountStatementUseCase
        )
    }
}
