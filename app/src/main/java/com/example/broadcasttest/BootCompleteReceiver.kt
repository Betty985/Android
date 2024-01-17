package com.example.broadcasttest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        TODO("开机广播似乎不支持了")
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        TODO("BootCompleteReceiver.onReceive() is not implemented")
        Log.d("MYTEST","BootCompleteReceiver")
        Toast.makeText(context,"Boot Complete",Toast.LENGTH_LONG).show()
    }
}