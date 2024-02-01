package com.example.androidtest

import kotlin.concurrent.thread

class MyThread : Thread() {
    override fun run() {
        super.run()
        println("MyThread run")
    }
}

fun main() {
    val myThread = MyThread()
    Thread(myThread).start()
    Thread {
        println("Thread run")
    }.start()
    thread {
        println("thread run")
    }
}
