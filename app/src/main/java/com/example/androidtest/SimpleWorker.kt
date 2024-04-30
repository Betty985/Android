package com.example.androidtest

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

//1. 定义后台任务，实现具体逻辑
// 后台任务继承自worker类
class SimpleWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    //    doWork不会运行在主线程中
    override fun doWork(): Result {
        Log.d("SimpleWorker", "do work in SimpleWorker")
        return Result.success()
    }
}
//2. 配置该后台任务的运行条件和约束信息，并构建后台任务请求。
// OneTimeWorkRequest.Builder是WorkRequest.Builder的子类，用于构建单次运行的后台任务请求。
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).build()

// PeriodicWorkRequest.Builder可用于构建周期性运行的后台任务请求，但是为了降低设备性能消耗运行周期间隔不能短于15分钟
val request1 = PeriodicWorkRequest.Builder(SimpleWorker::class.java, 15, TimeUnit.MINUTES).build()

//3. 将该后台任务请求传入WorkManager的enqueue方法中，系统会在合适的时间运行。

