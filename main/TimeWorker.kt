package com.example.myapplication

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequest

class TimeWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // 여기에서 실제 데이터 업데이트 작업을 수행
        getBG()
        Log.d("TimeWorker", "Executed")
        // 5분 후에 다음 작업 예약
        val nextWork = OneTimeWorkRequest.Builder(TimeWorker::class.java)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(nextWork)

        return Result.success()
    }

    private fun getBG() {
        val bgData = BGData(applicationContext)
        bgData.get_EntireBGInfo()
    }
}
