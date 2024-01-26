package com.example.androidtest

import kotlin.reflect.KProperty

// 泛型类
class MyClass<T> {
    fun method(param: T): T {
        return param
    }
}

val myClass = MyClass<Int>()
val result = myClass.method(123)

// 泛型方法
class MyClass1 {
    fun <T : Number> method(param: T): T {
        return param
    }
}

fun <T> T.build(block: T.() -> Unit): T {
    block()
    return this
}

// 委托
class MySet<T>(val helperSet: HashSet<T>) : Set<T> {
    override val size: Int
        get() = helperSet.size

    override fun isEmpty(): Boolean = helperSet.isEmpty()

    override fun iterator(): Iterator<T> = helperSet.iterator()

    override fun contains(element: T): Boolean = helperSet.contains(element)

    override fun containsAll(elements: Collection<T>) = helperSet.containsAll(elements)
}

class MyClass2 {
    //    p属性的具体实现委托给Delegate类实现
    val p by Delegate()
}

class Delegate {
    private var propValue: Any? = null

    //    KProperty是Kotlin中的一个属性操作类
    operator fun getValue(myClass: MyClass2, prop: KProperty<*>): Any? = propValue

    //    如果属性是使用val声明的就没必要实现setValue方法
    operator fun setValue(myClass: MyClass2, prop: KProperty<*>, value: Any?) {
        propValue = value
    }
}

// 实现lazy函数
class Later<T>(val block: () -> T) {
    private var value: Any? = null
    operator fun getValue(any: Any?, prop: KProperty<*>): T {
        if (value == null) {
            value = block()
        }
        return value as T
    }
}

fun <T> later(block: () -> T) = Later(block)
