package com.example.androidtest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MyServiceActivity : AppCompatActivity() {
    lateinit var downloadBinder: MyService.DownloadBinder

    //    ServiceConnection是一个用于监听与服务连接状态的接口
//    创建一个匿名对象并实现ServiceConnection接口
    private val connection = object : ServiceConnection {
        //        Activity与Service成功绑定的时候调用
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadBinder = service as MyService.DownloadBinder
            downloadBinder.startDownload()
            downloadBinder.getProgress()
        }

        //      只有Service的创建进程崩溃或者被杀掉的时候才会调用
        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_service)
        val startServiceBtn = findViewById<Button>(R.id.startServiceBtn)
        val stopServiceBtn = findViewById<Button>(R.id.stopServiceBtn)
        val bindServiceBtn = findViewById<Button>(R.id.bindServiceBtn)
        val unbindServiceBtn = findViewById<Button>(R.id.unbindServiceBtn)
        startServiceBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            startService(intent)//启动Service
        }
        stopServiceBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            stopService(intent)//停止Service
        }
        bindServiceBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
//            绑定Service
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        unbindServiceBtn.setOnClickListener {
//             解绑Service
            unbindService(connection)
        }
    }
}