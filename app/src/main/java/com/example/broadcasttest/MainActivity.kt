package com.example.broadcasttest

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : BaseActivity() {
    private lateinit var timeChangeReceiver: TimeChangeReceiver

    inner class TimeChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MYTEST", "TimeChangeReceiver")
            Toast.makeText(context, "Time has changed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val forceOffline = findViewById<Button>(R.id.forceOffline)
        forceOffline.setOnClickListener {
            val intent = Intent("com.example.broadcasttest.FORCE_OFFLINE")
            sendBroadcast(intent)
        }
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
                "update Book set price=? where name=?",
                arrayOf("10.99", "The Da Vinci Code")
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
                do {
//                    遍历Cursor对象，取出数据并打印
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val author = cursor.getString(cursor.getColumnIndex("author"))
                    val pages = cursor.getInt(cursor.getColumnIndex("pages"))
                    val price = cursor.getDouble(cursor.getColumnIndex("price"))
                    Log.d("MainActivity", "book name is $name")
                    Log.d("MainActivity", "book author is $author")
                    Log.d("MainActivity", "book pages is $pages")
                    Log.d("MainActivity", "book price is $price")
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