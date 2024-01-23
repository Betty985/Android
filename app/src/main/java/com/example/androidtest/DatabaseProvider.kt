package com.example.androidtest

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class DatabaseProvider : ContentProvider() {
    private val bookDir = 0
    private val bookItem = 1
    private val categoryDir = 2
    private val categoryItem = 3
    private val authority = "com.example.androidtest.provider"
    private var dbHelper: MyDatabaseHelper? = null
    private val uriMatcher by lazy {
        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(authority, "book", bookDir)
        matcher.addURI(authority, "book/#", bookItem)
        matcher.addURI(authority, "category", categoryDir)
        matcher.addURI(authority, "category", categoryItem)
//        在Kotlin中，如果一个函数或代码块的最后一行是一个表达式，那么这个表达式就是函数或代码块的返回值，无需使用return关键字。
        matcher
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int =
        dbHelper?.let {
//        删除数据
            val db = it.writableDatabase
            val deletedRows = when (uriMatcher.match(uri)) {
                bookDir -> db.delete("Book", selection, selectionArgs)
                bookItem -> {
                    val bookId = uri.pathSegments[1]
                    db.delete("Book", "id=?", arrayOf(bookId))
                }

                categoryDir -> db.delete("Category", selection, selectionArgs)
                categoryItem -> {
                    val categoryId = uri.pathSegments[1]
                    db.delete("Book", "id=?", arrayOf(categoryId))
                }

                else -> 0
            }
            deletedRows
        } ?: 0

    override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
        bookDir -> "vnd.android.cursor.dir/vnd.com.example.androidtest.provider.book"
        bookItem -> "vnd.android.cursor.item/vnd.com.example.androidtest.provider.book"
        categoryDir -> "vnd.android.cursor.dir/vnd.com.example.androidtest.provider.category"
        categoryItem -> "vnd.android.cursor.item/vnd.com.example.androidtest.provider.category"
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = dbHelper?.let {
//        添加数据
        val db = it.writableDatabase
        val uriReturn = when (uriMatcher.match(uri)) {
            bookDir, bookItem -> {
                val newBookId = db.insert("Book", null, values)
                Uri.parse("content://$authority/book/$newBookId")
            }

            categoryDir, categoryItem -> {
                val newCategoryId = db.insert("Category", null, values)
                Uri.parse("content://$authority/category/$newCategoryId")
            }

            else -> null
        }
        uriReturn
    }

    override fun onCreate(): Boolean = context?.let {
        dbHelper = MyDatabaseHelper(it, "BookStore.db", 2)
        true
    } ?: false

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = dbHelper?.let {
//        查询数据
        val db = it.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            bookDir -> db.query("Book", projection, selection, selectionArgs, null, null, sortOrder)
            bookItem -> {
                val bookId = uri.pathSegments[1]
                db.query("Book", projection, "id=?", arrayOf(bookId), null, null, sortOrder)
            }

            categoryDir -> db.query(
                "Category",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )

            categoryItem -> {
                val categoryId = uri.pathSegments[1]
                db.query("Category", projection, "id=?", arrayOf(categoryId), null, null, sortOrder)
            }

            else -> null
        }
        cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?
    ): Int = dbHelper?.let {
        val db = it.writableDatabase
        val updateRows = when (uriMatcher.match(uri)) {
            bookDir -> db.update("Book", values, selection, selectionArgs)
            bookItem -> {
                val bookId = uri.pathSegments[1]
                db.update("Book", values, "id=?", arrayOf(bookId))
            }

            categoryDir -> db.update("Category", values, selection, selectionArgs)
            categoryItem -> {
                val categoryId = uri.pathSegments[1]
                db.update("Category", values, "id=?", arrayOf(categoryId))
            }

            else -> 0
        }
        updateRows
    } ?: 0
}