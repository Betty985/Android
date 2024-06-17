package com.example.androidtest

import android.util.Log

enum class LogLevel(val level: Int) {
    VERBOSE(1), DEBUG(2), INFO(3), WARN(4), ERROR(5), NONE(6)
}
object LogUtils {
    private var level = LogLevel.VERBOSE

    // 将message参数类型从lambda表达式改为String
    private inline fun log(level: LogLevel, tag: String, message: String) {
        if (this.level.level <= level.level) {
            when (level) {
                LogLevel.VERBOSE -> Log.v(tag, message)
                LogLevel.DEBUG -> Log.d(tag, message)
                LogLevel.INFO -> Log.i(tag, message)
                LogLevel.WARN -> Log.w(tag, message)
                LogLevel.ERROR -> Log.e(tag, message)
                LogLevel.NONE -> {}
            }
        }
    }

    // 提供外部调用的方法，直接接受String类型的message参数
    fun v(tag: String, message: String) = log(LogLevel.VERBOSE, tag, message)
    fun d(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)
    fun i(tag: String, message: String) = log(LogLevel.INFO, tag, message)
    fun w(tag: String, message: String) = log(LogLevel.WARN, tag, message)
    fun e(tag: String, message: String) = log(LogLevel.ERROR, tag, message)
}
