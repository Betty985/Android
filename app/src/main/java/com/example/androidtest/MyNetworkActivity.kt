package com.example.androidtest

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import retrofit2.Call as RCall

fun webViewTest() {
//        val webview = findViewById<WebView>(R.id.webView)
//        webview.settings.javaScriptEnabled = true
//        webview.webViewClient = WebViewClient()
//        webview.loadUrl("https://www.baidu.com")
}

data class App(val id: String, val name: String, val version: String)
class Data(val id: String, val content: String)
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
//                val url = "http://localhost:3000/json"
                val url = "http://服务器本地ip:3000/json"
//                val url="https://www.baidu.com/"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
//                    showResponse(responseData)
//                    parseJSONWithJSONObject(responseData)
                    parseJSONWithGSON(responseData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseJSONWithJSONObject(jsonData: String) {
        try {
            val jsonArray = JSONArray(jsonData)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val name = jsonObject.getString("name")
                val version = jsonObject.getString("version")
                Log.d("NetWorkActivity", "id is $id")
                Log.d("NetWorkActivity", "name is $name")
                Log.d("NetWorkActivity", "version is $version")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseJSONWithGSON(jsonData: String) {
        val gson = Gson()
        val typeOf = object : TypeToken<appType>() {}.type
        val appList = gson.fromJson<appType>(jsonData, typeOf)
        for (app in appList) {
            val (id, name, version) = app
            Log.d("NetWorkActivity", "id is $id")
            Log.d("NetWorkActivity", "name is $name")
            Log.d("NetWorkActivity", "version is $version")
        }
    }

    private fun getAppData() {
        val getAppDataBtn = findViewById<Button>(R.id.getAppDataBtn)
        getAppDataBtn.setOnClickListener {
            val retrofit =
                Retrofit.Builder().baseUrl("http://服务器本地ip地址:3000").addConverterFactory(
                    GsonConverterFactory.create()
                ).build()
            val appService = retrofit.create(AppService::class.java)
            appService.getAppData(0, "", "").enqueue(object : retrofit2.Callback<appType> {
                override fun onResponse(
                    call: retrofit2.Call<appType>,
                    response: retrofit2.Response<appType>
                ) {
                    val list = response.body()
                    if (list != null) {
                        for (app in list) {
                            val (id, name, version) = app
                            Log.d("NetWorkActivity", "id is $id")
                            Log.d("NetWorkActivity", "name is $name")
                            Log.d("NetWorkActivity", "version is $version")
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<appType>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_network)
        getAppData()
        val sendRequestBtn = findViewById<Button>(R.id.sendRequestBtn)
        sendRequestBtn.setOnClickListener {
//            sendRequestWithHttpURLConnection()
            sendRequestWithOkHttp()
        }
    }
}

typealias appType = List<App>

interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: Exception)
}

object HttpUtil {

    fun sendHttpRequest(address: String, listener: HttpCallbackListener) {
//        子线程无法通过return语句返回数据
        thread {
            var connection: HttpURLConnection? = null
            try {
                val response = StringBuilder()
                val url = URL(address)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val input = connection.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)
                    }
                }
                listener.onFinish(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                listener.onError(e)
            } finally {
                connection?.disconnect()
            }
        }
    }

    fun sendOkHttpRequest(address: String, callback: Callback) {
        val client = OkHttpClient()
        val request = Request.Builder().url(address).build()
        client.newCall(request).enqueue(callback)
    }
}

interface AppService {
    @GET("{page}/json")
    fun getAppData(
        @Path("page") page: Int = 0,
        @Query("u") user: String,
        @Query("token") token: String
    ): retrofit2.Call<appType>

    @DELETE("data/{id}")
    fun deleteData(@Path("id") id: String): RCall<ResponseBody>

    @POST("data/create")
    fun createData(@Body data: Data): RCall<ResponseBody>

    @Headers("User-Agent:okhttp", "Cache-Control:max-age=0")
    fun getData(): RCall<Data>

    @GET("json")
    fun dynamicHeaders(
        @Header("User-Agent") userAgent: String,
        @Header("Cache-Control") cacheControl: String
    ): RCall<Data>
}

object ServiceCreator {
    private const val BASE_URL = "http://172.30.112.177:3000"
    private val retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}

val appService = ServiceCreator.create(AppService::class.java)
fun main() {
//    回调接口都是在子线程中运行的，因此不可以在这里执行UI操作，除非借助runOnUiThread()方法来进行线程转换。
    HttpUtil.sendHttpRequest("https://www.baidu.com/", object : HttpCallbackListener {
        override fun onFinish(response: String) {
            println(response)
        }

        override fun onError(e: Exception) {
            println(e.toString())
        }
    })
    HttpUtil.sendOkHttpRequest("https://www.baidu.com/", object : Callback {
        override fun onResponse(call: Call, response: Response) {
            val responseData = response.body?.string()
            println(responseData)
        }

        override fun onFailure(call: Call, e: IOException) {
            println(e.toString())
        }
    })
}