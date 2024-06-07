package com.example.androidtest.practice

import android.content.Context
import android.widget.Toast
import com.example.androidtest.MyApplication
import kotlin.math.max

// vararg允许方法接收任意多个同等类型的参数
fun max(vararg nums: Int): Int {
    var maxNum = Int.MIN_VALUE
    for (num in nums) {
        maxNum = max(maxNum, num)
    }
    return maxNum
}

fun <T : Comparable<T>> max(vararg nums: T): T {
    if (nums.isEmpty()) throw RuntimeException("Params can't be empty")
    var maxNum = nums[0]
    for (num in nums) {
        if (num > maxNum) {
            maxNum = num
        }
    }
    return maxNum
}
// 添加扩展函数，并在里面封装弹出Toast的具体逻辑
fun String.showToast(duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(MyApplication.appContext,this,duration).show()
}
// 所有类型的数字都是可比较的，因此必须实现compareble接口
fun main() {
    val a = 10.0
    val b = 15.1
    val c = 5.4
    val larger = max(a, b, c)
    println("max is : ${larger}")
}