package com.example.androidtest

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

@Suppress("UNUSED_EXPRESSION")
class MainActivity : BaseActivity() {
    private lateinit var timeChangeReceiver: TimeChangeReceiver

    inner class TimeChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MYTEST", "TimeChangeReceiver")
            Toast.makeText(context, "Time has changed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crud() {
        val createDatabase = findViewById<Button>(R.id.createDatabase)
        val dbHelper = MyDatabaseHelper(this, "BookStore.db", 3)
        createDatabase.setOnClickListener {
            dbHelper.writableDatabase
        }
        val addData = findViewById<Button>(R.id.addData)
        addData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values1 = ContentValues().apply {
//                开始组装第一条数据
                put("name", "The Da Vinci Code")
                put("author", "Dan Brown")
                put("pages", 454)
                put("price", 16.96)
            }
            db.insert("Book", null, values1)
            val values2 = ContentValues().apply {
//                开始组装第二条数据
                put("name", "The Lost Symbol")
                put("author", "Dan Brown")
                put("pages", 510)
                put("price", 19.95)
            }
            db.execSQL(
                "insert into Book(name,author,pages,price) values(?,?,?,?)",
                arrayOf("The Da Vinci Code", "Dan Brown", "454", "16.96")
            )
            db.execSQL(
                "insert into Book (name,author,pages,price) values(?,?,?,?)",
                arrayOf("The Lost Symbol", "Dan Brown", "510", "19.95")
            )
//            db.insert("Book", null, values2)
        }
        val updateData = findViewById<Button>(R.id.updateData)
        updateData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values = ContentValues()
            values.put("price", 10.99)
//            db.update("Book", values, "name=?", arrayOf("The Da Vinci Code"))
            db.execSQL(
                "update Book set price=? where name=?", arrayOf("10.99", "The Da Vinci Code")
            )
        }
        val deleteData = findViewById<Button>(R.id.deleteData)
        deleteData.setOnClickListener {
            val db = dbHelper.writableDatabase
//            db.delete("Book", "pages>?", arrayOf("500"))
            db.execSQL("delete from Book where pages>?", arrayOf("500"))
        }
        val queryData = findViewById<Button>(R.id.queryData)
        queryData.setOnClickListener {
            val db = dbHelper.writableDatabase
//            查询Book表中所有的数据
//            val cursor = db.query("Book", null, null, null, null, null, null)
            val cursor = db.rawQuery("select * from Book", null)
            if (cursor.moveToFirst()) {
//                    遍历Cursor对象，取出数据并打印
                do {
                    val name = cursor.safeGetString("name")
                    val author = cursor.safeGetString("author")
                    val pages = cursor.safeGetInt("pages")
                    val price = cursor.safeGetDouble("price")

                    name?.let { Log.d("MainActivity", "book name is $it") }
                    author?.let { Log.d("MainActivity", "book author is $it") }
                    pages?.let { Log.d("MainActivity", "book pages is $it") }
                    price?.let { Log.d("MainActivity", "book price is $it") }
                } while (cursor.moveToNext())
            }

//            避免造成内存泄露
            cursor.close()
        }

        val replaceData = findViewById<Button>(R.id.replaceData)
        replaceData.setOnClickListener {
            val db = dbHelper.writableDatabase
//            开启事务
            db.beginTransaction()
            try {
                db.delete("Book", null, null)
                if (true) {
//                    手动抛出一个异常，让事务失败
//                    throw NullPointerException()
                }
                val values = ContentValues().apply {
                    put("name", "Game of Thrones")
                    put("author", "George Martin")
                    put("pages", 720)
                    put("price", 20.85)
                }
                db.insert("Book", null, values)
//                事务已经执行成功
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.endTransaction()
            }
        }
    }

    private fun sendNotice() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("normal", "Normal", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
            val channe2=NotificationChannel("important","Important",NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channe2)
        }
        val sendNotice = findViewById<Button>(R.id.sendNotice)
        sendNotice.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notification =
                NotificationCompat.Builder(this, "normal").setContentTitle("This is content title")
//                    .setContentText(
//                        "This is content text. NotificationCompat.Builderthis,normal setContentTitle notification_bg -a " +
//                                "android.intent.action.MAIN -c android.intent.category.LAUNCHER "
//                    )
//                    在通知中展示长文本
                    .setStyle(NotificationCompat.BigTextStyle().bigText("This is content text. NotificationCompat.Builderthis,normal setContentTitle notification_bg -a " +
                            "android.intent.action.MAIN -c android.intent.category.LAUNCHER "))
//                    通知里展示大图片无法展示 Android版本低于16
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.unsplash
                        )
                    ))
                    .setSmallIcon(androidx.core.R.drawable.notification_bg).setLargeIcon(
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.unsplash
                        )
                    )
                    .setContentIntent(pi)
//                    点击通知时通知自动取消
                    .setAutoCancel(true)
                    .build()
            manager.notify(1, notification)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call()
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun call() {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:10086")
            startActivity(intent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val makeCall = findViewById<Button>(R.id.makeCall)
        makeCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
//                请求码是唯一值
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else {
                call()
            }
        }
        val forceOffline = findViewById<Button>(R.id.forceOffline)
        forceOffline.setOnClickListener {
            val intent = Intent("com.example.androidtest.FORCE_OFFLINE")
            sendBroadcast(intent)
        }
        crud()
        sendNotice()
        timeChangeReceiver = TimeChangeReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.TIME_TICK")
        registerReceiver(timeChangeReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeChangeReceiver)
    }
}