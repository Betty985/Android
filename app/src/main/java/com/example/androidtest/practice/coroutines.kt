package com.example.androidtest.practice


import com.example.androidtest.AppService
import com.example.androidtest.HttpCallbackListener
import com.example.androidtest.HttpUtil
import com.example.androidtest.ServiceCreator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

fun test() {
//    创建一个协程的作用域，但可以保证在协程作用域内的所有代码和子协程没有全部执行完之前一直阻塞当前线程
    runBlocking {
        println("codes run in coroutine scope1")
        delay(1)
        println("codes run in coroutine scope finished1")
    }
//    Global.launch函数可以创建一个协程的作用域，但每次创建的都是一个顶层协程，当应用程序运行结束时会跟着一起结束。
//    日志还没来得及打印，应用程序就结束了
    GlobalScope.launch {
        println("codes run in coroutine scope")
//        非阻塞式的挂起函数，只会挂起当前协程
        delay(10)
        println("codes run in coroutine scope finished")
    }
//    阻塞当前线程
    Thread.sleep(10)
}


fun repeat1() {
    val start = System.currentTimeMillis()
    runBlocking {
        repeat(100000) {
            launch {
                println("$it")
            }
        }
    }
    val end = System.currentTimeMillis()
    println(end - start)
}

//在launch函数中编写的代码是拥有协程作用域的，但单独提取到一个单独的函数中就没有协程作用域了
fun scope() {
    fun test1() {
        // 尝试使用delay，将会失败，因为这里没有协程作用域
//        delay(10) // 错误：Suspend function 'delay' should be called only from a coroutine or another suspend function
        println("延迟执行1")
    }

    fun test2(scope: CoroutineScope) {
//        1.传递CoroutineScope作为参数
        scope.launch {
            delay(10)
            println("延迟执行2")
        }
    }

    fun test3() {
//        2.GlobalScope
        GlobalScope.launch {
            delay(10)
            println("延迟执行3")
        }
    }
    GlobalScope.launch {
        test1()
        test2(this) // 在协程作用域内调用
        test3()
    }
    Thread.sleep(100)
}

// launch必须在协程作用域内调用


//suspend可以将任意函数声明成挂起函数，但是suspend无法提供协程作用域
// coroutineScope函数是挂起函数，会继承外部的协程的作用域并创建一个子协程。可以保证其作用域内的所有代码和子协程在全部执行完之前，外部的协程会一直挂起。
// 挂起函数只能在协程或其他挂起函数中被调用。
suspend fun printDot() = coroutineScope {
    launch {
        println(".")
//    delay是挂起函数
        delay(10)
    }
}

// runBlocking和Global.launch可以在任意地方调用，coroutineScope可以在协程作用域或挂起函数中调用，launch只能在协程作用域中调用。
fun corountineTest() {
    runBlocking {
        for (i in 1..10) {
            printDot()
        }
        println("coroutineScope finished")
    }
    println("runBlocking finished")
}
// Global.launch和launch会返回一个Job对象，只需要调用Job对象的cancel方法就可以取消协程了

fun cancel() {
    val job = Job()
//    所有调用CoroutineScope的launch创建的协程都会被关联在Job对象的作用域下面。
    val scope = CoroutineScope(job)
    scope.launch {

    }
    job.cancel()
}

fun asyncTest() {
    runBlocking {
        val start = System.currentTimeMillis()
//       调用async函数后，代码块中的代码会立即开始执行。await方法会将当前协程阻塞住直到可以获得async函数的执行结果。
        val result = async {
            delay(1000)
            5 + 5
        }
        val result2 = async {
            delay(1000)
            3 + 4
        }
//        在需要用到async函数的执行结果时才调用await()方法进行获取，可以使async函数并行运行
        println("result is ${result.await().toString()} ${result2.await().toString()}")
        val end = System.currentTimeMillis()
        println("cost ${end - start} ms")
    }
}

// 简化回调写法
suspend fun request(address: String): String {
//    suspendCoroutine函数会使当前协程被立刻挂起，lambda表达式中的代码会在普通线程中执行
    return suspendCoroutine { continuation ->
        HttpUtil.sendHttpRequest(
            address, object : HttpCallbackListener {
                //                通过传统回调的方式监听请求结果
                override fun onFinish(response: String) {
                    //    恢复被挂起的协程
                    continuation.resume(response)
                }

                override fun onError(e: Exception) {
                    //    恢复被挂起的协程
                    continuation.resumeWithException(e)
                }
            }
        )
    }
}

suspend fun getBaiduResponse() {
    try {
        val response = request("https://www.baidu.com/")
        println("返回的数据： $response")
    } catch (e: Exception) {
        println("error: ${e.toString()}")
    }
}

suspend fun <T> Call<T>.await(): T {
    return suspendCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body != null) continuation.resume(body)
                else continuation.resumeWithException(
                    RuntimeException("response body is null")
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}

suspend fun getAppData() {
    try {
        val appList = ServiceCreator.create<AppService>(AppService::class.java)
            .getAppData(user = "eee", page = 1, token = "ddd").await()
        println(appList)
    } catch (e: Exception) {
        println("wrong : ${e.toString()}")
    }
}

fun main() {
    if (true) {
        // Kotlin 类
        class MyClass

        // 获取 Kotlin 类的 KClass 引用
        val kClass: KClass<MyClass> = MyClass::class
        println("Kotlin class: $kClass")

        // 获取 Java 类的 Class 引用
        val javaClass: Class<MyClass> = MyClass::class.java
        println("Java class: $javaClass")

    } else {
        runBlocking {
//        withContext是一个挂起函数，会将最后一行执行结果作为返回值
            val result = withContext(Dispatchers.Default) {
                5 + 4
            }
//        getBaiduResponse()
            getAppData()
            println(result)
        }
    }
}