package com.example.androidtest

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Update
    fun updateUser(newUser: User)

    @Query("select * from User")
    fun loadAllUsers(): List<User>

    @Query("select * from User where age>:age")
    fun loadUserOlderThan(age: Int): List<User>

    @Delete
    fun deleteUser(user: User)

    @Query("delete from User where lastName=:lastName")
    fun deleteUserByLastName(lastName: String): Int
}

//定义数据库版本号，包含的实体类以及提供Dao层的访问实例
@Database(version = 3, entities = [User::class, Book::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao

    companion object {
        private var instance: AppDatabase? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table Book (" +
                        "id integer primary key autoincrement not null," +
                        "name text not null," +
                        "pages integer not null)")
            }
        }
        private val MIGRATION_2_3=object :Migration(2,3){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("alter table Book add column author text not null default 'unknown'")
            }
        }
        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
//            数据库操作属于耗时操作，room默认不允许在主线程中进行数据库操作。
//            但可以在创建AppDatabase实例时加入一个allowMainThreadQueries()方法，这样Room就允许在主线程中进行数据库操作了（测试环境）
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).allowMainThreadQueries().build().apply {
                instance = this
            }
//            开发测试阶段可以使用fallbackToDestructiveMigration升级，它会将当前的数据库销毁然后再重新创建
//            return Room.databaseBuilder(
//                context.applicationContext, AppDatabase::class.java, "app_database"
//            ).fallbackToDestructiveMigration().build()
        }

    }
}

@Entity
data class Book(var name: String, var pages: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

@Dao
interface BookDao {
    @Insert
    fun insertBook(book: Book): Long

    @Query("select * from Book")
    fun loadAllBooks(): List<Book>
}