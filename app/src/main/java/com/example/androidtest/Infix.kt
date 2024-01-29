package com.example.androidtest

infix fun String.beginsWith(prefix: String) = startsWith(prefix)

val list = listOf<String>("Apple", "Banana", "Orange", "Pear", "Grape")

fun main() {
    infix fun <T> Collection<T>.has(element: T) = contains(element)
    if (list has "Banana") {
        println("HHH HAS")
    }
    infix fun <A, B> A.with(that: B): Pair<A, B> = Pair(this, that)
    val map= mapOf<String,Number>("Apple" with 1,"Banana" with  2)
    println(map)
}
