package com.example.androidtest

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.concurrent.thread

// service非常适合执行那些不需要和用户交互而且要求长期运行的任务。
class MyService : Service() {
    private val mBinder = DownloadBinder()

    class DownloadBinder : Binder() {
        fun startDownload() {
            Log.d("MyService", "startDownload executed")
        }

        fun getProgress(): Int {
            Log.d("MyService", "getProgress executed")
            return 0
        }
    }

    //    第一次创建调用
    override fun onCreate() {
        super.onCreate()
        Log.d("MyService", "onCreate executed")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_service", "前台Service通知", NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MyServiceActivity::class.java)
        val pi = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )
        val notification = NotificationCompat.Builder(this, "my_service")
            .setContentTitle("This is a foreground service").setContentText("This is content text")
            .setSmallIcon(androidx.customview.R.drawable.notification_bg).setLargeIcon(
                BitmapFactory.decodeResource(
                    resources, R.drawable.ic_launcher_background
                )
            ).setContentIntent(pi).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, MyService::class.java))
//            startForeground方法是Service类的方法，只能在Service或其子类中使用。
            startForeground(1, notification)
        } else {
            startService(Intent(this, MyService::class.java))
        }
    }

    // 每次启动Service时调用
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService", "onStartCommand executed")
        thread {
//            Service在执行完毕后自动停止
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService", "onDestroy executed")
    }

    //    onBind是Service 里唯一的抽象方法必须在子类实现
    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }
}

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        // 在这里执行后台任务
//        在Worker的doWork()方法中，你需要返回一个Result对象，这个对象表示任务的执行结果。
//        如果任务执行成功，你应该返回Result.success()；
//        如果任务执行失败，你应该返回Result.failure()；
//        如果你希望任务在失败后重新执行，你应该返回Result.retry()。
        return Result.success()
    }
}

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 在这里进行全局的初始化操作
    }

    companion object {
        private var instance: MyApplication? = null

        fun getContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }
}

fun demo() {
    // 在需要的地方启动任务
    val myWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
    WorkManager.getInstance(MyApplication.getContext()).enqueue(myWorkRequest)
}



