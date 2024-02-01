package com.example.androidtest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread
import kotlinx.coroutines.*
class DownloadTask{
private val job:Job?=null
    fun preExecute(){}
}
//class DownloadTask:Asynctask<Unit,Int,Boolean>(){
//    override fun onPreExecute(){
//        progressDialog.show()
//    }
//    override fun doInBackground(vararg params:Unit?)=try {
//        while(true){}
//    }
//}

class MyThreadTest : AppCompatActivity() {
    val updateText = 1
    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
//            进行UI操作
            super.handleMessage(msg)
            when (msg.what) {
                updateText -> {
                    val textView = findViewById<TextView>(R.id.textView)
                    textView.text = "Nice to meet you"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_thread_test)
        val changeTextBtn = findViewById<Button>(R.id.changeTextBtn)
        val textView = findViewById<TextView>(R.id.textView)
        changeTextBtn.setOnClickListener {
            thread {
//            textView.text = "Nice to meet you"
                val msg=Message()
                msg.what=updateText
                handler.sendMessage(msg) //将Message对象发送出去

            }
        }
    }
}