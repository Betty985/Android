package com.example.androidtest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.concurrent.thread

// 定义实体类
@Entity
data class User(var firstName: String, var lastName: String, var age: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

object Repository {
    fun getUser(userId: String): LiveData<User> {
        val liveData = MutableLiveData<User>()
        liveData.value = User(userId, userId, 0)
        return liveData
    }

    fun refresh(): MutableLiveData<Int> {
        var liveData = MutableLiveData<Int>()
        return liveData
    }
}

class MainViewModel(countReserved: Int) : ViewModel() {
    //    LiveData是一种响应式编程组件，可以包含任何类型的数据，并在数据发生变化的时候通知给观察者
//    三种读写数据的方法，getValue()、setValue()和postValue()方法。
//    getValue()方法获取到的数据可能是空的
//    postValue()方法用于在非主线程中给liveData设置数据。
    private val _counter = MutableLiveData<Int>()
    val counter: LiveData<Int> = _counter
    private val userLivedata = MutableLiveData<User>(User("firstName", "lastName", 2))
    val userName: LiveData<String> = userLivedata.map { user ->
        "${user.firstName} ${user.lastName}"
    }
    private val userIdLiveData = MutableLiveData<String>()
    private val refreshLiveData = MutableLiveData<Any?>()
    val refreshResult = refreshLiveData.switchMap {
        Repository.refresh()
    }

    //    switchMap将转换函数中的LiveData对象转换成一个可观察的LiveData对象
    val user: LiveData<User> = userIdLiveData.switchMap { userId ->
        Repository.getUser(userId)
    }

    init {
        //    var counter = countReserved
        _counter.value = countReserved
    }

    //   fun getUser(userId: String): LiveData<User> {
////       getUser每次都返回新的LiveData实例
//       return Repository.getUser(userId)
//   }
    fun plusOne() {
        val count = _counter.value ?: 0
        _counter.value = count + 1
    }

    fun clear() {
        _counter.value = 0
    }

    fun getUser(userId: String) {
        userIdLiveData.value = userId
    }

    fun refresh() {
//        只要调用了setValue和postValue方法就一定会触发数据变化事件
        refreshLiveData.value = refreshLiveData.value
    }
}

// 可以向ViewModel的构造函数中传递参数
class MainViewModelFactory(private val countReserved: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(countReserved) as T
    }
}

class MyObserver {
    fun activityStart() {}

    fun activityStop() {}
}

// lifecycle入参可以是MyObserver主动获取当前的生命周期状态。
class MyLifecycleObserver(val lifecycle: Lifecycle) : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("lifecycle", "onStart")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("lifecycle", "onStop")
    }
}
// 继承自AppCompatActivity的activity或者继承自android.fragment.app.Fragment的fragment本身就是一个lifecycleOwner实例
// lifecycleOwner.lifecycle.addObserver(MyObserver())

class MyViewModelActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var sp: SharedPreferences
    private lateinit var observer: MyObserver
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_view_model)
        lifecycle.addObserver(MyLifecycleObserver(lifecycle))
//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        sp = getPreferences(Context.MODE_PRIVATE)
        observer = MyObserver()
        val countReserved = sp.getInt("count_reserved", 0)
        viewModel = ViewModelProvider(
            this, MainViewModelFactory(countReserved)
        )[MainViewModel::class.java]
        Log.d("transformations", viewModel.userName.value.toString())
        val plusOneBtn = findViewById<Button>(R.id.plusOneBtn)
        plusOneBtn.setOnClickListener {
            count++
            viewModel.plusOne()
            refreshCounter()
        }
        val clearBtn = findViewById<Button>(R.id.clearBtn)
        clearBtn.setOnClickListener {
            viewModel.clear()
            refreshCounter()
        }
        val getUserBtn = findViewById<Button>(R.id.getUserBtn)
        getUserBtn.setOnClickListener {
            val userId = (0..1000).random().toString()
            viewModel.getUser(userId)
        }
        val infoTextSwitch = findViewById<TextView>(R.id.infoTextSwitch)

        viewModel.user.observe(this) {
            infoTextSwitch.text = "外部 liveData: ${it.firstName}"
        }
        val infoTextV = findViewById<TextView>(R.id.infoTextV)
        viewModel.counter.observe(
            this,
        ) { count ->
            infoTextV.text = "liveData: ${count.toString()}"
        }
//        viewModel.counter.observe(this, Observer {
//            it
//            infoTextV.text = "liveData: $it"
//        })
        refreshCounter()
        userRoom()
        Log.d("viewModel", "onCreate")
    }

    private fun refreshCounter() {
        val infoText = findViewById<TextView>(R.id.infoText)
//        val infoTextV = findViewById<TextView>(R.id.infoTextV)
        infoText.text = count.toString()
//        infoTextV.text = "viewModel:" + viewModel.counter.toString()
    }

    private fun userRoom() {
        val addUserRoomBtn = findViewById<Button>(R.id.addDataRoomBtn)
        val updateDataRoomBtn = findViewById<Button>(R.id.updateDataRoomBtn)
        val deleteDataRoomBtn = findViewById<Button>(R.id.deleteDataRoomBtn)
        val queryDataRoomBtn = findViewById<Button>(R.id.queryDataRoomBtn)
        val userDao = AppDatabase.getDatabase(this).userDao()
        var user1 = User("Tom", "Brady", 40)
        var user2 = User("Lily", "White", 43)
        addUserRoomBtn.setOnClickListener {
            thread {
                user1.id = userDao.insertUser(user1)
                user2.id = userDao.insertUser(user2)
            }
        }
        updateDataRoomBtn.setOnClickListener{
            thread {
                user1.age=42
                userDao.updateUser(user1)
            }
        }
        deleteDataRoomBtn.setOnClickListener{
            thread {
                userDao.deleteUserByLastName("Brady")
            }
        }
        queryDataRoomBtn.setOnClickListener {
            thread {
                for (user in userDao.loadAllUsers()){
                    Log.d("MainActivity",user.toString())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        observer.activityStart()
        Log.d("viewModel", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("viewModel", "onResume")
    }

    override fun onPause() {
        super.onPause()
        sp.edit {
            putInt("count_reserved", viewModel.counter.value ?: 0)
        }
        Log.d("viewModel", "onPause")
    }

    override fun onStop() {
        super.onStop()
        observer.activityStop()
        Log.d("viewModel", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("viewModel", "onDestroy")
    }

}