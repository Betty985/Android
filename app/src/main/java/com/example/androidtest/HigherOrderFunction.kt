package com.example.androidtest

// Kotlin中的Unit大致相当于Java中的void
fun example(func: (String, Int) -> Unit) {
    func("hello", 123)
}

// 内联函数  将内联函数中的代码在编译时自动替换到调用它的地方，消除Kotlin高阶函数Lambda表达式在底层被转换成匿名类的实现方式带来的运行时开销
inline fun num1AndNum2(num1: Int, num2: Int, operation: (Int, Int) -> Int): Int {
    return operation(num1, num2)
}

fun plus(num1: Int, num2: Int): Int {
    return num1 + num2
}

fun minus(num1: Int, num2: Int): Int {
    return num1 - num2
}

// 一个高阶函数中如果接收了两个或者更多函数类型的参数加上inline 关键字那么Kotlin编译器会自动将所有引用的Lambda表达式全部进行内联
//内联的函数类型参数在编译的时候会被进行代码替换，因此没有真正的参数属性。内联的函数类型参数只允许传递给另外一个内联函数。非内联的函数类型参数可以自由地传递给其他任何函数。
inline fun inlineTest(block1: () -> Unit, noinline block2: () -> Unit) {

}

inline fun printString(string: String, block: (String) -> Unit) {
    println("printString start")
    block(string)
    println("printString end")
}

inline fun runRunnable(crossinline block: () -> Unit){
    var runnable=Runnable{
        block()
    }
    runnable.run()
}
// 高阶函数允许让函数类型的参数来决定函数的执行逻辑
fun main() {
    println("main start")
    val str = ""
    printString(str){
        println("lambda start")
        if(str.isEmpty()) return@printString
        println(str)
        println("lambda end")
    }
    val num1 = 100
    val num2 = 80
//    ::fun是一种函数引用方式的写法，表示将函数作为参数传递
//    val result1 = num1AndNum2(num1, num2, ::plus)
//    val result2 = num1AndNum2(num1, num2, ::minus)
//    lambda表达式版本
    val result1 = num1AndNum2(num1, num2) { n1, n2 ->
        n1 + n2
    }
    val result2 = num1AndNum2(num1, num2) { n1, n2 ->
        n1 - n2
    }
    println("result1 is $result1")
    println("result2 is $result2")
    println("main end")
}