package com.example.androidtest

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MyWorkManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_work_manager)
        val doWorkBtn = findViewById<Button>(R.id.doWorkBtn)
        doWorkBtn.setOnClickListener {
            val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
//               setInitialDelay可以让后台任务在指定的延迟时间后运行
                .setInitialDelay(5, TimeUnit.MINUTES)
//               给后台任务请求添加标签
                .addTag("simple")
//                重新执行任务 第一个参数用于指定如果任务再次执行失败，下次重试的时间应该以什么样的形式延迟。第二三个参数用于指定在多久之后重新执行任务
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(this).enqueue(request)
        }
//        通过标签来取消后台任务请求
        WorkManager.getInstance(this).cancelAllWorkByTag("simple")
//        通过id来取消后台任务请求
        WorkManager.getInstance(this).cancelWorkById(request.id)
//        一次性取消所有后台任务请求
        WorkManager.getInstance(this).cancelAllWork()
//        观察数据变化监听后台任务的运行结果
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
            .observe(this) { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Log.d("MyWorkManagerActivity", "do work succeeded")
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Log.d("MyWorkManagerActivity", "do work failed")
                }
            }
//        beginWith开启一个链式任务，用then方法连接后台任务。在前一个后台任务运行成功之后，下一个后台任务才会运行。
//        WorkManager.getInstance(this).beginWith(sync).then(compress).then(upload).enqueue()
    }
}
