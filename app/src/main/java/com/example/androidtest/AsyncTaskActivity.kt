package com.example.androidtest

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AsyncTaskActivity : AppCompatActivity() {
    private lateinit var alertDialog: AlertDialog
    private lateinit var progressBar: ProgressBar
    private val job = Job()

    //    suspend 用于声明协程挂起函数
    private suspend fun download() {
        var progress = 0
        Log.d("协程", "download")
        while (progress <= 100) {
            delay(1000)
            progress += 10
//            切换到主线程
            withContext(Dispatchers.Main) {
                Log.d("协程", progress.toString())
                progressBar.progress = progress
                alertDialog.setTitle("Loading...         $progress%")
                if (progress > 100) {
                    alertDialog.dismiss() // 关闭对话框
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async_task)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        progressBar.max = 100 // 设置最大进度
        alertDialog = AlertDialog.Builder(this).setTitle("Loading...")
            .setView(progressBar)
            .create()
    }

    override fun onStart() {
        super.onStart()
        alertDialog.show()
        GlobalScope.launch {
            download()
        }
    }

    override fun onStop() {
        super.onStop()
        job.cancel() // 取消协程
    }
}