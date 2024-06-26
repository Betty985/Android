package com.example.androidtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

fun Cursor.safeGetString(columnName: String): String? {
    val index = getColumnIndex(columnName)
    return if (index != -1) getString(index) else null
}

fun Cursor.safeGetInt(columnName: String): Int? {
    val index = getColumnIndex(columnName)
    return if (index != -1) getInt(index) else null
}

fun Cursor.safeGetDouble(columnName: String): Double? {
    val index = getColumnIndex(columnName)
    return if (index != -1) getDouble(index) else null
}

open class BaseActivity : AppCompatActivity() {
    private lateinit var receiver: ForceOfflineReceiver

    inner class ForceOfflineReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            AlertDialog.Builder(context).apply {
                setTitle("Warning")
                setMessage("You are forced to be offline.Please try to login again")
                setCancelable(false)
                setPositiveButton("OK") { _, _ ->
//                    销毁所有Activity
                    ActivityCollector.finishAll()
                    val i = Intent(context, LoginActivity::class.java)
//                    重新启动LoginActivity
                    context.startActivity(i)
                }
                show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        isDarkTheme(this)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.androidtest.FORCE_OFFLINE")
        receiver = ForceOfflineReceiver()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun isDarkTheme(context: Context): Boolean {
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        LogUtils.d("isDARK", (flag == Configuration.UI_MODE_NIGHT_YES).toString())
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}