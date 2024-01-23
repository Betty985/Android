package com.example.androidtest

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class MyProvider : ContentProvider() {
    private val table1Dir = 0
    private val table1Item = 1
    private val table2Dir = 2
    private val table2Item = 3
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI("com.example.app.provider", "table1", table1Dir)
        uriMatcher.addURI("com.example.app.provider", "table1/#", table1Item)
        uriMatcher.addURI("com.example.app.provider", "table2", table2Dir)
        uriMatcher.addURI("com.example.app.provider", "table2/#", table2Item)
    }

    //    初始化ContentProvider时调用。通常在这里完成对数据库的创建和升级等操作。
    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    //   从ContentProvider中查询数据
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("Not yet implemented")
        when (uriMatcher.match(uri)) {
            table1Dir -> {
//                查询table1表中的所有数据
            }

            table1Item -> {
//                查询table1表中的单条数据
            }

            table2Dir -> {
//                查询table2表中的所有数据
            }

            table2Item -> {
//                查询table2表中的单条数据
            }
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
        table1Dir -> "vnd.android.cursor.dir/vnd.com.example.app.provider.table1"
        table1Item -> "vnd.android.cursor.item/vnd.com.example.app.provider.table1"
        table2Dir -> "vnd.android.cursor.dir/vnd.com.example.app.provider.table2"
        table2Item -> "vnd.android.cursor.item/vnd.com.example.app.provider.table2"
        else -> null
    }
}