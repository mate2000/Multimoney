package com.multimoney.multimoney.presentation.ui.credit.movements.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.credit.QueryAccountStatementUseCase
import javax.inject.Inject

class DownloadCreditMovementsWorkerProvider @Inject constructor(
    private val preferences: DataStorePreferences,
    private val queryAccountStatementUseCase: QueryAccountStatementUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return DownloadCreditMovementsWorker(
            context = appContext,
            workerParameters = workerParameters,
            preferences = preferences,
            queryAccountStatementUseCase = queryAccountStatementUseCase
        )
    }
}
