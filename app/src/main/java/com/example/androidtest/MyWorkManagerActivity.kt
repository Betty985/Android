package com.example.androidtest

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.androidtest.practice.showToast
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.concurrent.TimeUnit

// Serializable表示将一个对象转换成可存储或可传输的状态。序列化后的对象可以在网络上进行传输，也可以存储到本地。
// 实现：实现Serializable接口
class Person : Serializable {
    var name = ""
    var age = 0
    override fun toString(): String {
        return "Person(name='$name', age=$age)"
    }
}

// parcelable将一个完整的对象进行分解，而分解后的每一部分都是intent所支持的数据类型，这样就能实现传递对象的功能了
class Company : Parcelable {
    var cname = ""
    var salary = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(cname)
        dest.writeInt(salary)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Company(cname='$cname', salary=$salary)"
    }

    companion object CREATOR : Parcelable.Creator<Company> {
        override fun createFromParcel(source: Parcel): Company {
            val company = Company()
            company.cname = source.readString() ?: ""
            company.salary = source.readInt()
            return company
        }

        override fun newArray(size: Int): Array<Company?> {
            return arrayOfNulls(size)
        }
    }

}

@Parcelize
data class Region(var rname: String, var code: Int) : Parcelable
class MyWorkManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_work_manager)
        val doWorkBtn = findViewById<Button>(R.id.doWorkBtn)
        doWorkBtn.setOnClickListener {
            val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
//               setInitialDelay可以让后台任务在指定的延迟时间后运行
                .setInitialDelay(5, TimeUnit.MINUTES)
//               给后台任务请求添加标签
                .addTag("simple")
//                重新执行任务 第一个参数用于指定如果任务再次执行失败，下次重试的时间应该以什么样的形式延迟。第二三个参数用于指定在多久之后重新执行任务
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS).build()
            WorkManager.getInstance(this).enqueue(request)
            intentPutExtra()
        }
//        通过标签来取消后台任务请求
        WorkManager.getInstance(this).cancelAllWorkByTag("simple")
//        通过id来取消后台任务请求
        WorkManager.getInstance(this).cancelWorkById(request.id)
//        一次性取消所有后台任务请求
        WorkManager.getInstance(this).cancelAllWork()
//        观察数据变化监听后台任务的运行结果
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
            .observe(this) { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Log.d("MyWorkManagerActivity", "do work succeeded")
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Log.d("MyWorkManagerActivity", "do work failed")
                }
            }
//        beginWith开启一个链式任务，用then方法连接后台任务。在前一个后台任务运行成功之后，下一个后台任务才会运行。
//        WorkManager.getInstance(this).beginWith(sync).then(compress).then(upload).enqueue()
        LogUtils.d("LogUtils","msg")
        LogUtils.e("LogUtils","msg")
    }

    private fun intentPutExtra() {
        "This is a Toast".showToast()
        val person = Person()
        person.name = "Tom"
        person.age = 20
        val company = Company()
        company.cname = "巴啦啦"
        company.salary = 1000000
        val region = Region("earth",1000)
        val intent = Intent(this, MaterialMainActivity::class.java)
        Log.d("Person", "put $person $company $region")

        intent.putExtra("person_data", person)
        intent.putExtra("company_data", company)
        intent.putExtra("region_data",region)
        startActivity(intent)
    }
}
