# 广播

广播分为两种类型：标准广播和有序广播

根据自己感兴趣的广播自由地注册BroadcastReceiver,这样当有相应的广播发出时，
BroadcastReceiver就能收到该广播，并可以在内部进行逻辑处理。
注册BroadcastReceiver的方式一般有两种：

- 动态注册：在代码中注册。可以自由地控制注册与注销，在灵活性方面有很大优势。但是必须在程序启动之后才能接收广播。
- 静态注册：在AndroidManifest.xml中注册

# `::class.java`

在 Kotlin 中，`::class` 是一种语法，用于获取一个类的 `KClass` 类型的引用。`KClass` 是 Kotlin
的反射API的一部分，代表 Kotlin 类。当你需要在运行时获取关于类的信息时，这会非常有用。

然而，由于 Kotlin 与 Java 完全互操作，有时你可能需要获取 Java 类的 `Class` 类型的引用，而不是 Kotlin
的 `KClass`。为了实现这一点，你可以在 `::class` 后面加上 `.java`。这样，`::class.java` 就提供了一个指向
Java `Class` 的引用，这与 Java 中的 `ClassName.class` 相似。

这在与需要 Java 类引用的 Java 库或 API 交互时特别有用。例如，当你使用 Retrofit 这样的库定义接口时，你可能需要传递一个
Java 类型的 `Class` 对象。

下面是一个简单的示例，展示了如何在 Kotlin 中使用 `::class.java`：

```kotlin
import kotlin.reflect.KClass

// Kotlin 类
class MyClass

fun main() {
    // 获取 Kotlin 类的 KClass 引用
    val kClass: KClass<MyClass> = MyClass::class
    println("Kotlin class: $kClass")

    // 获取 Java 类的 Class 引用
    val javaClass: Class<MyClass> = MyClass::class.java
    println("Java class: $javaClass")
}
```

在这个示例中，`MyClass::class` 获取了 `MyClass` 的 `KClass` 类型的引用，而 `MyClass::class.java`
获取了相应的 Java `Class` 类型的引用。这种方式使得 Kotlin 代码能够方便地与 Java 代码或需要 Java
类型引用的库进行互操作。

# 高阶函数

> 在Kotlin中，内联函数（inline
> function）的主要目的是减少函数调用的开销。当你将一个函数标记为inline时，编译器会在每个调用处将函数体复制一份，而不是真正地调用函数。这样可以减少函数调用的开销，但会增加代码的大小。

>
对于内联函数中的Lambda参数，编译器会将Lambda表达式的代码也一起复制到调用处。这就意味着，你可以在Lambda表达式中使用return关键字来直接返回外部函数，就像Lambda表达式是外部函数的一部分一样。这种行为被称为非局部返回（non-local
return）。

>
在你的例子中，return@printString是一个标签返回，它表示从printString这个内联函数返回。因为printString函数是内联的，所以return@printString实际上会直接返回main函数，而不仅仅是从Lambda表达式返回。这就是为什么return@printString在内联函数中依然生效的原因。

> 需要注意的是，非局部返回只在内联函数的Lambda参数中有效。如果你试图在非内联函数的Lambda参数中使用return关键字，编译器会报错。

在Kotlin中，非内联函数的Lambda参数在运行时被实现为匿名类，这些匿名类并不具有对它们被调用的位置的上下文信息。因此，它们不能直接返回到调用它们的函数。这就是为什么在非内联函数的Lambda参数中使用`return`
关键字会导致编译错误。

另一方面，内联函数的Lambda参数在编译时会被直接插入到调用它们的位置，因此它们可以访问调用它们的函数的上下文，包括使用`return`
关键字直接返回到调用它们的函数。这就是为什么在内联函数的Lambda参数中可以使用`return`关键字进行非局部返回。

总的来说，这是由于内联函数和非内联函数的Lambda参数在运行时的实现方式不同导致的。

# 持久化技术

Android系统中主要提供了3种方式用于简单地实现数据持久化功能：文件存储、SharedPreference存储以及数据库存储。

# ContentProvider

主要用于在不同应用程序之间实现数据共享的功能，它提供了一套完整的机制7
，允许一个程序访问另一个程序中的数据，同时还能保证被访问数据的安全性。

ContentProvider的用法一般有两种：使用现有的ContentProvider读取和操作相应程序中的数据；另一种是创建自己的ContentProvider给程序的数据提供外部访问接口。

ContentResolver中的增删改查方法都是不接收表名参数的，而是使用一个Uri参数代替。这个参数被称为内容URI。内容URI给ContentProvider中的数据建立了唯一标识符，它主要由两部
分组成：authority和path。
authority是用于对不同的应用程序做区分的，一般为避免冲突，会采用应用包名的方式进行命名。
path则是用于对同一应用程序中不同的表做区分的，通常会添加到authority的后面。为辨认内容URI还需要在字符串的头部加上协议声明。
`*`表示匹配任意长度的任意字符，`#`表示匹配任意长度的数字。

`getType()`方法是所有ContentProvider都必须提供的一个方法，用于获取Uri对象所对应的MIME类型。一个内容URI所对应的MIME字符串主要由3部分组成，

- 必须以`vnd`开头
- 如果内容URI以路径结尾，则后接`android.cursor.dir/`；如果内容URI以id结尾，则后接`android.cursor.item/`
- 最后接上`vnd.<authority>.<path>`

# 泛型

在一般的编程模式下，需要给任何一个变量指定一个具体的类型，而泛型允许我们在不指定具体类型的情况下进行编程，这样编写出来的代码将会有更好的扩展性。
泛型有两种定义方式：定义泛型类；定义泛型方法。
可以通过指定上界的方式对泛型放入类型进行约束。

# 委托

一种设计模式：操作对象自己不会去处理某段逻辑，而是会把工作委托给另外一个辅助对象去处理。Kotlin中委托功能分为两种：类委托和委托属性。
大部分的方法实现调用辅助对象中的方法，少部分的方法实现由自己来重写，甚至加入一些自己独有的方法。
类委托的核心思想是将一个类的具体实现委托给另一个类去完成，委托属性的核心思想是将一个属性（字段）的具体实现委托给另一个类去完成。

# 通知

应用程序希望向用户发出一些提示信息，而该应用程序又不在前台运行时，就可以借助通知来实现。一般只有当程序进入后台的时候才需要使用通知。
Android
8.0引入了通知渠道这个概念，每条通知属于一个对应的渠道。每个应用程序都可以自由地创建当前应用拥有哪些通知渠道，但是这些通知渠道的控制权是掌握在用户手上的。用户可以自由地选择通知渠道的重要程度。

# infix

- `infix`函数是不能定义成顶层函数的，它必须是某个类的成员函数，可以使用扩展函数的方式将它定义到某个类当中；
- 必须接收且只能接收一个参数，不限制参数类型。

# git

`git checkout`可以撤销未add过的文件；

`git reset HEAD`可以撤销add过的文件

`git log`id查看某一条记录，-1表示想看到最后一次提交。

# Service

Service是Android中实现程序后台运行的解决方法，它非常适合那些不需要和用户交互而且还要求长期运行的任务。Service的运行不依赖于任何用户界面，即使程序被切换到后台或用户打开了另外一个应用程序，Service仍然能够保持正常运行。

```
ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
Android的UI操作必须在主线程（也称为UI线程）中进行。
```

## 异步消息处理机制

- Message。Message是在线程之间传递的消息，它可以在内部携带少量信息，用于在不同线程之间传递数据。
- Handler主要用于发送和处理消息。发送消息一般使用Handler的sendMessage方法等，发出的消息经过一系列地辗转处理后，最终会传递到Handler的handleMessage方法中。
- MessageQueue主要用于存放所有通过Handler发送的消息。这部分消息会一直存在于消息队列中，等待被处理。每个线程中只会有一个MessageQueue对象。
-
Looper是每个线程中MessageQueue的管家，调用Looper的loop方法后，就会进入到一个无限循环中，每当发现MessageQueue中存在一条消息时就会将它取出，并传递到Handler的handleMessage方法中。每个线程中只会有一个Looper对象。

# 协变和逆变

在类型系统中，协变（Covariance）和逆变（Contravariance）是描述类型转换规则的术语。

1. 协变（Covariance）：如果A是B的子类型，那么List<A>就是List<B>的子类型。在Kotlin中，使用`out`
   关键字表示协变，在TypeScript中，使用`extends`关键字表示协变。

Kotlin示例：

```kotlin
interface Producer<out T> {
    fun produce(): T
}
```

TypeScript示例：

```typescript
interface Producer<T extends Animal> {
    produce(): T;
}
```

2. 逆变（Contravariance）：如果A是B的子类型，那么Consumer<B>就是Consumer<A>的子类型。在Kotlin中，使用`in`
   关键字表示逆变，在TypeScript中，使用`super`关键字表示逆变。

Kotlin示例：

```kotlin
interface Consumer<in T> {
    fun consume(item: T)
}
```

TypeScript示例：

```typescript
interface Consumer<T super Dog> {
    consume(item: T): void;
}
```

注意：TypeScript中并没有直接的`super`
关键字来表示逆变，上述TypeScript示例并不能在实际代码中运行，只是为了说明逆变的概念。在实际的TypeScript代码中，我们通常不需要（也不能）显式地声明逆变。

# 类型擦除

类型擦除是Java中的一个概念，主要是指在编译时期，Java会将泛型的类型信息擦除，只保留原始类型。
这是因为Java在引入泛型之前已经有了大量的代码，为了保证这些代码的兼容性，Java选择了类型擦除的方式来实现泛型。

# kotlin不存在类似js的函数声明提升

在Kotlin中，不存在类似JavaScript中的函数声明提升（Hoisting）。

在JavaScript中，函数声明提升是指无论函数在哪里声明，都会被提升到当前作用域的顶部。这意味着你可以在声明函数之前调用函数。

然而，在Kotlin中，函数必须在被调用之前声明。如果你试图在声明函数之前调用函数，Kotlin编译器会报错。
在Kotlin的Android开发中，你可能会看到一些看似存在"提升"
现象的代码，比如在onCreate方法中使用了在后面定义的函数或者变量。实际上，这并不是因为Kotlin有函数提升的特性，而是因为整个Activity类的构造过程和生命周期方法的调用顺序。

当一个Activity被创建时，它的构造函数和初始化块会首先被执行，然后才会调用onCreate方法。
因此，即使你在onCreate方法中使用了在代码后面定义的函数或者变量，只要它们是在构造函数或初始化块中定义的，那么在onCreate方法被调用时，这些函数和变量已经被初始化了。
例如：

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 在onCreate方法中调用在后面定义的函数
        sayHello()
    }

    // 在onCreate方法后面定义的函数
    fun sayHello() {
        println("Hello, world!")
    }
}
```

在这个例子中，sayHello函数虽然在代码中位于onCreate方法后面，但是在onCreate方法被调用时，sayHello函数已经被定义了，所以这段代码可以正常运行。这并不是因为Kotlin有函数提升的特性，而是因为Activity的生命周期方法的调用顺序。

# corountineScope不能创建顶层协程

`coroutineScope`
函数确实是一个挂起函数，它会创建一个新的协程作用域，并在这个作用域内启动子协程。这个新的作用域会继承外部协程的上下文（包括Job和其他元素），但是它会创建一个新的作用域限制，确保在`coroutineScope`
内启动的所有协程都完成后，`coroutineScope`才会返回。

如果在没有外部协程作用域的情况下调用`coroutineScope`，这种情况实际上是不可能的，因为`coroutineScope`
是一个挂起函数，它只能在协程中或其他挂起函数中被调用。换句话说，你不能在常规的非协程代码块中直接调用`coroutineScope`
，因为它需要一个协程上下文来挂起和恢复执行。

因此，`coroutineScope`创建的子协程永远不会是顶层协程。顶层协程是通过协程构建器（如`launch`、`async`
等）在某个`CoroutineScope`中直接启动的协程。`coroutineScope`用于在已有的协程中创建一个新的作用域，而不是创建一个独立的顶层协程。

如果你需要在没有现有协程作用域的地方启动一个协程，你应该使用全局作用域`GlobalScope`
或者自定义的`CoroutineScope`实例，并使用`launch`或`async`等构建器来启动顶层协程。例如：

```kotlin
GlobalScope.launch {
    // 这是一个顶层协程
    coroutineScope {
        // 这是一个子协程作用域，不是顶层协程
        launch {
            // 在coroutineScope内部启动的子协程
        }
    }
}
```

# 创建协程的方式

在Kotlin中，创建协程主要依赖于协程构建器。协程构建器是一些顶级函数，用于在协程作用域内启动协程。下面是几种常用的创建协程的方式：
除coroutineScope函数外，其他所有的函数都可以指定线程参数。withContext强制要求指定。

### 1. `launch` 构建器

`launch` 是最常用的协程构建器之一，它启动一个新的协程并返回一个`Job`对象，但不阻塞当前线程。`launch`
通常用于执行不需要返回结果的协程任务。

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking { // 这个表达式将主线程转换为协程
    launch { // 在后台启动一个新的协程并继续
        delay(1000L) // 非阻塞的等待1秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    println("Hello,") // 协程已在等待时主线程还在继续
}
```

### 2. `async` 构建器

`async` 与`launch`类似，但它返回一个`Deferred`
对象，这是一个轻量级的非阻塞的future，表示一个可能尚未完成的异步计算。`async`用于需要并发执行任务并返回结果时。
async只能在协程作用域中调用。

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val deferred: Deferred<Int> = async {
        // 执行一些异步计算并返回结果
        delay(1000L)
        42 // 返回计算结果
    }
    println("计算结果：${deferred.await()}") // 使用await()等待异步计算的结果
}
```

### 3. `runBlocking` 构建器

`runBlocking`
是一个特殊的协程构建器，它会阻塞当前线程来等待，直到协程内的所有代码和子协程执行完毕。`runBlocking`
主要用于桥接阻塞代码和协程代码，比如在`main`函数或测试中。

```kotlin
import kotlinx.coroutines.*

fun main() {
    runBlocking { // 开始执行主协程
        launch {
            delay(200L)
            println("Task from runBlocking")
        }

        coroutineScope { // 创建一个协程作用域
            launch {
                delay(500L)
                println("Task from nested launch")
            }

            delay(100L)
            println("Task from coroutine scope") // 这一行会在内嵌launch之前输出
        }

        println("Coroutine scope is over") // 这一行在内嵌launch执行完毕后才输出
    }
}
```

### 4. `coroutineScope` 函数

`coroutineScope`
函数也可以用来启动协程，它会创建一个新的协程作用域，并且只有在所有启动的子协程都完成时才会完成。与`runBlocking`
不同，`coroutineScope`不会阻塞当前线程，而是挂起，直到所有子协程执行结束。

```kotlin
import kotlinx.coroutines.*

suspend fun main() = coroutineScope { // 使用coroutineScope构建协程作用域
    launch {
        delay(1000)
        println("World!")
    }
    println("Hello,")
}
```

### 5.  `withContext`函数

`withContext`函数是一个挂起函数。调用withContext()
函数之后，会立即执行代码块中的代码，同时将外部协程挂起。当代码块中的代码全部执行完之后，会将最后一行的执行结果作为
withContext()函数的返回值返回

```kotlin
fun main() {
    runBlocking {
        val result = withContext(Dispatchers.Default) {
            5 + 5
        }
    }
    println(result)
}
```

总结，`coroutineScope`
用于在已有协程中创建子协程作用域，它不能用于创建顶层协程。顶层协程的创建需要使用`GlobalScope`
或自定义的`CoroutineScope`实例。

# viewModel

可以帮助Activity分担一部分工作，它是专门用于存在与界面相关的数据的。

```kotlin
ViewModelProvider(< Activity 或Fragment实例 >).get(< ViewMdodel >::class.java)
```

## 为什么在屏幕旋转时不会销毁

`ViewModel` 在屏幕旋转时不被销毁的机制主要依赖于 `ViewModelStore` 和 `ViewModelProvider`
。这两个组件协同工作，确保 `ViewModel` 在 `Activity` 或 `Fragment` 的配置变化（如屏幕旋转）时仍然保持存活。下面是这一机制的简要说明：

### ViewModelStore

`ViewModelStore` 是一个容器，用于存储和管理 `ViewModel` 实例。每个 `Activity` 和 `Fragment`
都有自己的 `ViewModelStore`。当 `Activity` 或 `Fragment`
被销毁时，如果是因为配置变化（如屏幕旋转），系统会保留其 `ViewModelStore`
；如果是因为用户显式地关闭 `Activity` 或系统因资源紧张而销毁 `Activity`，则会清除其 `ViewModelStore`。

### ViewModelProvider

`ViewModelProvider` 负责为 `Activity` 或 `Fragment` 提供 `ViewModel` 实例。当你从 `ViewModelProvider`
请求一个 `ViewModel` 实例时，它会首先检查当前 `Activity` 或 `Fragment` 的 `ViewModelStore`
中是否已经有了该类型的 `ViewModel`
实例。如果有，它会直接返回现有的实例；如果没有，它会创建一个新的实例，然后存储在 `ViewModelStore` 中。

### 实现机制

当屏幕旋转导致 `Activity` 被销毁并重新创建时，虽然 `Activity`
的实例是新的，但系统会将原来的 `ViewModelStore` 传递给新的 `Activity` 实例。因此，当新的 `Activity`
实例通过 `ViewModelProvider` 请求 `ViewModel` 时，由于 `ViewModelStore` 中已经有了之前的 `ViewModel`
实例，`ViewModelProvider` 就会直接返回它，而不是创建一个新的实例。这就是 `ViewModel` 能够在配置变化时保持存活的原因。

### 示例代码

```kotlin
class MyActivity : AppCompatActivity() {
    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)

        // 使用 ViewModelProvider 获取 ViewModel 实例
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
    }
}
```

在这个示例中，无论 `MyActivity` 因屏幕旋转而被销毁多少次，`viewModel`
都会保持不变，因为它是从 `ViewModelStore` 中获取的，而这个 `ViewModelStore` 在配置变化时被保留了下来。

# withContext()的线程参数

## Dispatchers.Default

使用默认低并发的线程策略，当你要执行的代码属于计算密集型任务时，开启过高的并发反而可能影响任务的运行效率。

## Dispatchers.IO

较高并发的线程策略，要执行的代码大多数时间是在阻塞和等待中，能支持更高的并发数量。

## Dispatchers.Main

不会开启子线程而是在Android主线程中执行代码，但这个值只能在Android项目中使用，纯Kotlin程序使用这种类型的线程参数会出现错误。

# 在非Activity的类中感知Activity的生命周期

- 在Activity中嵌入一个隐藏的Fragment来进行感知
- 手写监听器的方式来进行感知
- LifecycleObserver接口
# tips

- [快速生成drawable](https://blog.csdn.net/m0_60352504/article/details/126392050)
- CollapsingToolbarLayout只能作为AppBarLayout的直接子布局，而AppBarLayout必须是CoordinatorLayout的子布局，
# Room
## 架构
ORM(object relational mapping)将面向对象的语言和面向关系的数据库建立一种映射关系。
- Entity。用于定义封装实际数据的实体类，每个实体类都会在数据库中有一张对应的表，并且表中的列是根据实体类中的字段自动生成的。
- Dao。Dao是数据访问对象的意思，通常会在这里对数据库的各项操作进行封装，在实际编程的时候，逻辑层就不需要和底层数据库打交道了，直接和Dao层进行交互即可。
- Database。用于定义数据库中的关键信息，包括数据库的版本号，包含哪些实体类以及提供Dao层的访问实例。



如果你在 Room 数据库操作中使用了一个实体，但这个实体没有在数据库定义中通过 `entities` 属性包含进来，将会发生编译时错误。Room 在编译时会检查所有的数据库操作，确保使用的实体都已经在数据库定义中声明。如果发现有未声明的实体被使用，编译器将无法生成相应的代码，导致编译失败。

错误信息通常会指出问题所在，比如提示某个 DAO 中引用了未知的实体，或者某个实体没有在数据库中声明。这样的错误信息有助于开发者快速定位并修正问题。

### 示例

假设你有一个 `Book` 实体和一个相应的 `BookDao` 接口，但你忘记在 `AppDatabase` 类中声明这个实体：

```kotlin
@Entity
data class Book(...)

@Dao
interface BookDao {
    @Query("SELECT * FROM Book")
    fun getAllBooks(): List<Book>
}

@Database(version = 1, entities = [User::class]) // Book 实体未声明
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    // abstract fun bookDao(): BookDao // 即使这里声明了 BookDao，由于 Book 实体未在 entities 中声明，也会导致编译错误
}
```

在这种情况下，当你尝试编译项目时，Room 会因为 `Book` 实体没有在 `AppDatabase` 的 `entities` 中声明而报错。错误信息可能会提示 `Book` 类型未知或者 `BookDao` 中的查询引用了未知的表。

### 解决方法

要解决这个问题，你需要确保所有使用的实体都已经在 `@Database` 注解的 `entities` 属性中正确声明：

```kotlin
@Database(version = 1, entities = [User::class, Book::class]) // 现在包含了 Book 实体
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao // 确保 DAO 也被正确声明
}
```

总之，确保数据库定义中包含了所有使用的实体是使用 Room 持久化库的基本要求。这有助于 Room 在编译时生成正确的代码，避免运行时错误，并确保数据库的操作能够顺利进行。
# WorkManager
Service是Android系统的四大组件之一，在没有被销毁的情况下一直保持在后台运行。
WorkManager只是一个处理定时任务的工具，它可以保证即使在应用退出甚至手机重启的情况下，之前注册的任务仍然将会得到执行。
- 定义一个后台任务，并实现具体的任务逻辑；
- 配置该后台任务的运行条件和约束信息，并构建后台任务请求；
- 将该后台任务请求传入workManager的enqueue()方法中，系统会在合适的时间运行。

<text style="color:red;font-weight:600">国产手机上不能依赖workManager实现什么核心功能</text>
国产手机厂商在进行Android系统定制的时候会增加一键关闭功能，允许用户一键杀死所有非白名单的应用程序。而被杀死的应用程序既无法接收广播，也无法运行WorkManager的后台任务。
# DSL
DSL（领域特定语言，Domain-Specific Language）是为某一特定领域、问题或任务设计的计算机程序设计语言。与通用编程语言（如Java、Python等）相比，DSL专注于特定领域，提供了更为简洁、高效的解决方案。DSL可以分为两大类：

1. **内部DSL（Internal DSLs）**：这种DSL是在宿主语言（如Java、Kotlin等）中实现的，利用宿主语言的语法特性来模拟DSL。因此，内部DSL也被称为嵌入式DSL。内部DSL的优点是可以直接利用宿主语言的生态系统和工具链，但其表达能力受限于宿主语言的语法。

2. **外部DSL（External DSLs）**：这种DSL拥有自定义的语法和解析器，与宿主语言独立。外部DSL需要专门的解析器来解析和执行，因此开发成本较高，但它提供了更大的灵活性和定制能力。

DSL的应用非常广泛，例如：

- **SQL（结构化查询语言）**：用于数据库查询的DSL。
- **HTML（超文本标记语言）**：用于定义网页内容的DSL。
- **CSS（层叠样式表）**：用于描述网页布局和样式的DSL。
- **Gradle构建脚本**：基于Groovy或Kotlin的内部DSL，用于自动化构建项目。

DSL的优点包括：

- **提高生产效率**：DSL提供了针对特定领域的抽象，使得开发者可以更快地实现领域内的功能。
- **提高代码的可读性**：DSL的语法通常更接近自然语言或领域专业术语，使得代码更易于理解。
- **易于维护**：由于DSL专注于特定领域，其设计通常更为简洁，从而降低了维护成本。

使用DSL时，需要权衡其带来的好处和潜在的复杂性。在某些情况下，使用通用编程语言加上良好的设计模式可能是更合适的选择。
# tips
## 获取全局context
在`MyApplication`类中，通过定义`companion object`并在其中实现一个静态方法`getContext()`来获取全局的`Context`，与直接在`onCreate()`方法中通过`applicationContext`获取`Context`，本质上是为了达到相同的目的——获取一个应用级别的`Context`。但是，这两种方式在实现细节和使用场景上有所不同。

### 通过`companion object`获取`Context`：

```kotlin
companion object {
    private var instance: MyApplication? = null

    fun getContext(): Context {
        return instance!!.applicationContext
    }
}
```

- **实现方式**：在`MyApplication`类中定义了一个静态的`instance`变量来持有`MyApplication`的实例，并在`onCreate()`方法中对其进行初始化。`getContext()`方法通过这个`instance`变量来提供一个全局可访问的`Context`。
- **使用场景**：这种方式允许你在应用的任何地方通过`MyApplication.getContext()`静态方法来获取`Context`，而不需要直接访问`Application`实例或传递`Context`。
- **注意事项**：这种方式虽然方便，但需要注意避免在`MyApplication`的`instance`未被初始化之前调用`getContext()`方法，否则会引发`NullPointerException`。此外，静态持有`Context`的引用需要谨慎处理，以避免潜在的内存泄漏（虽然`Application`的`Context`通常不会导致泄漏）。

### 在`onCreate()`方法中通过`applicationContext`获取`Context`：

```kotlin
override fun onCreate() {
    super.onCreate()
    context = applicationContext
}
```

- **实现方式**：在`Application`的`onCreate()`方法中，通过`applicationContext`获取到应用级别的`Context`并将其存储在某个变量中。
- **使用场景**：这种方式通常用于在`Application`类内部进行初始化操作时需要使用`Context`，例如初始化第三方库。
- **注意事项**：由于`onCreate()`方法在`Application`生命周期的开始阶段就会被调用，因此在`onCreate()`方法之后，`applicationContext`可以安全地用于任何需要应用级别`Context`的场景。

### 区别总结：

- **获取方式**：通过`companion object`提供的静态方法获取`Context`更加灵活，可以在没有`Application`实例的情况下直接使用。而在`onCreate()`中获取`Context`更适合于应用启动时的初始化操作。
- **使用场景**：如果你需要在应用的多个地方频繁地访问`Context`，使用`companion object`可能更方便。如果仅在应用启动时需要`Context`进行一些初始化设置，直接在`onCreate()`中使用`applicationContext`即可。
- **安全性**：两种方式都是安全的，因为它们都是在获取应用级别的`Context`，不会导致内存泄漏。但是，通过`companion object`的方式需要确保正确地管理`instance`的初始化和访问，以避免`NullPointerException`。
内存泄漏通常发生在长生命周期的对象持有短生命周期对象的引用，导致短生命周期对象无法被垃圾回收器回收。
## `companion object`与静态属性
在Kotlin中，`companion object`用于在类内部定义伴生对象，这允许你在没有类实例的情况下访问类内部的属性和方法，类似于Java中的静态属性和方法。然而，Kotlin本身并没有直接的`static`关键字，这是Kotlin设计者故意为之，以提供更多的灵活性和表达力。`companion object`是Kotlin提供的解决方案之一，用于实现类似静态成员的功能。

### `companion object`的基本用法：

```kotlin
class MyClass {
    companion object {
        val staticVal = "静态属性"
        fun staticMethod() = "静态方法"
    }
}
```

在这个例子中，`MyClass`有一个伴生对象，伴生对象内部定义了一个静态属性`staticVal`和一个静态方法`staticMethod()`。你可以不创建`MyClass`的实例而直接访问这些属性和方法：

```kotlin
val myVal = MyClass.staticVal
val myMethodResult = MyClass.staticMethod()
```

### `companion object`与Java静态属性和方法的对比：

- **访问方式**：在Kotlin中，通过`companion object`定义的属性和方法可以像访问静态成员一样直接通过类名访问。这与Java中的静态属性和方法访问方式相似。
- **实现原理**：在编译时，Kotlin会将伴生对象内的属性和方法编译为外部类的静态字段和方法。因此，从Java代码中访问Kotlin的伴生对象成员时，它们就像是静态成员一样。
- **灵活性**：与Java的静态成员相比，`companion object`更灵活。伴生对象本身可以实现接口，可以有扩展函数和属性，还可以被继承，这些都是Java静态成员所不具备的。

### 注意事项：

- **命名**：伴生对象可以有可选的名称，如果没有指定名称，默认名称是`Companion`。
- **单例**：每个类只能有一个伴生对象。伴生对象本身在类加载时就被初始化，遵循单例模式。
- **访问限制**：虽然伴生对象的成员类似于静态成员，但它们实际上仍然是实例成员。如果需要，你可以通过伴生对象的实例来访问这些成员。

总的来说，`companion object`提供了一种在不创建类实例的情况下，访问类内部属性和方法的机制，同时提供了比Java静态成员更多的灵活性和功能。
# bug

## 生命周期

```
在 Android 中，registerForActivityResult 是用于注册一个回调，以处理从启动的 Activity 返回的结果。这个注册过程需要在 Activity 的生命周期状态为 STARTED 之前完成，也就是在 onCreate、onStart 或 onResume 方法中进行。

当你点击按钮时，Activity 已经处于 RESUMED 状态，此时再调用 registerForActivityResult 就会违反其使用规则，因此会抛出 IllegalStateException 异常
```
## `Transformations`无法解析
[关于使用Transformations.map/switchMap的标红报错问题](https://juejin.cn/post/7264437782332293178)
直接调用LiveData的map和switchMap方法
```kotlin
@JvmName("map")
@MainThread
@CheckResult
@Suppress("UNCHECKED_CAST")
fun <X, Y> LiveData<X>.map(
    transform: (@JvmSuppressWildcards X) -> (@JvmSuppressWildcards Y)
): LiveData<Y> {
    val result = MediatorLiveData<Y>()
    if (isInitialized) {
        result.value = transform(value as X)
    }
    result.addSource(this) { x -> result.value = transform(x) }
    return result
}
```
## localhost请求没有发送

```text
localhost 是一个回环地址，指向本地设备。在你的代码中，当你在 Android 设备上运行时，localhost 实际上指的是设备本身，而不是你的服务器。
为了解决这个问题，你需要将 localhost 替换为你服务器的实际 IP 地址或域名。确保你的设备和服务器在同一网络中，并且使用正确的 IP 地址或域名来替换 localhost。
```

# other

`.gitignore` 文件的更改需要在添加文件到暂存区之前生效。

