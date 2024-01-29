广播分为两种类型：标准广播和有序广播

根据自己感兴趣的广播自由地注册BroadcastReceiver,这样当有相应的广播发出时，
BroadcastReceiver就能收到该广播，并可以在内部进行逻辑处理。
注册BroadcastReceiver的方式一般有两种：

- 动态注册：在代码中注册。可以自由地控制注册与注销，在灵活性方面有很大优势。但是必须在程序启动之后才能接收广播。
- 静态注册：在AndroidManifest.xml中注册

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

# bug

```
在 Android 中，registerForActivityResult 是用于注册一个回调，以处理从启动的 Activity 返回的结果。这个注册过程需要在 Activity 的生命周期状态为 STARTED 之前完成，也就是在 onCreate、onStart 或 onResume 方法中进行。

当你点击按钮时，Activity 已经处于 RESUMED 状态，此时再调用 registerForActivityResult 就会违反其使用规则，因此会抛出 IllegalStateException 异常
```

# other

`.gitignore` 文件的更改需要在添加文件到暂存区之前生效。

