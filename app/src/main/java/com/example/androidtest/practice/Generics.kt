package com.example.androidtest.practice

import android.content.Context
import android.content.Intent
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

//对泛型进行实化：1.必须是内联函数，用inline修饰函数；2.在声明泛型的地方必须加上reified关键字
inline fun <reified T> getGenericType() = T::class.java
fun testReified() {
    val result1 = getGenericType<String>()
    val result2 = getGenericType<Int>()
    println("result1 is $result1")
    println("result2 is $result2")
}

// Intent.() 是Kotlin中的一个特性，叫做"函数字面值（Function Literals）"或者"带接收者的函数字面值（Function Literals with Receiver）"。
inline fun <reified T> startActivity(context: Context, block: Intent.() -> Unit) {
    val intent = Intent(context, T::class.java)
    intent.block()
    context.startActivity(intent)
}

// 协变
open class Person(val name: String, val age: Int)
class Student(name: String, age: Int) : Person(name, age)
class Teacher(name: String, age: Int) : Person(name, age)

fun handleSimpleData(data: SimpleData<Person>) {
    val teacher = Teacher("Jack", 35)
//    data.set(teacher)
    val personData=data.get()
}

// 泛型T的声明前面加上了out关键字意味着现在T只能出现在out位置不能出现在in位置。同时也意味着SimpleData在泛型T上是协变的
// ps：在泛型前面加上@UnsafeVariance注解可以让编译器理解我们这种操作是安全的，编译器就会允许泛型出现在in位置上
class SimpleData<out T>(private val data: T?) {
    //    private var data:T?=null
//    fun set(t:T?){
//        data=t
//    }
    fun get(): T? {
        return data
    }
}

fun covariation() {
    val student = Student("Tom", 19)
    val data = SimpleData<Student>(student)
//    data.set(student)
    handleSimpleData(data)
    val studentData = data.get()
}
// 逆变
interface Transformer<in T>{
    fun transform(t:T):String
}
fun main(){
    val trans=object:Transformer<Person>{
        override fun transform(t:Person):String{
            return "${t.name} ${t.age}"
        }
    }
    handleTransformer(trans)
}
fun handleTransformer(trans:Transformer<Student>){
    val student=Student("Tom",19)
    val result=trans.transform(student)
}