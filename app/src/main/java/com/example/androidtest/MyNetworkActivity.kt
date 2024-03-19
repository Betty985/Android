package com.example.androidtest

import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

fun webViewTest() {
//        val webview = findViewById<WebView>(R.id.webView)
//        webview.settings.javaScriptEnabled = true
//        webview.webViewClient = WebViewClient()
//        webview.loadUrl("https://www.baidu.com")
}

class MyNetworkActivity : AppCompatActivity() {
    private fun sendRequestWithHttpURLConnection() {
//        开启线程发起网络请求
        thread {
            var connection: HttpURLConnection? = null
            try {
                val response = StringBuilder()
                val url = URL("https://www.baidu.com")
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val input = connection.inputStream
//               对获取到的数据流进行读取
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)
                    }
                }
                showResponse(response.toString())
                fun postData() {
                    connection.requestMethod = "POST"
                    val output = DataOutputStream(connection.outputStream)
                    output.writeBytes("username=admin&word=123")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun showResponse(response: String) {
        runOnUiThread {
//          进行UI操作
            val responseText = findViewById<TextView>(R.id.responseText)
            responseText.text = response
            val webview = findViewById<WebView>(R.id.webView)
//            webview.loadData("<html><body><h1>Hello, world!</h1></body></html>", "text/html", "UTF-8")
            webview.loadData(response, "text/html", "UTF-8")
            webview.settings.javaScriptEnabled = true
        }
    }

    private fun sendRequestWithOkHttp() {
        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url("https://www.baidu.com").build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
                    showResponse(responseData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_network)
        val sendRequestBtn = findViewById<Button>(R.id.sendRequestBtn)
        sendRequestBtn.setOnClickListener {
//            sendRequestWithHttpURLConnection()
            sendRequestWithOkHttp()
        }
    }
}