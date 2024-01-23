package com.example.androidtest

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf

class MyProviderActivity : AppCompatActivity() {
    var bookId: String? = null
    private lateinit var addData: Button
    private lateinit var queryData: Button
    private lateinit var updateData: Button
    private lateinit var deleteData: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider)
        addData = findViewById(R.id.addData)
        queryData = findViewById(R.id.queryData)
        updateData = findViewById(R.id.updateData)
        deleteData = findViewById(R.id.deleteData)
        addData.setOnClickListener {
            val uri = Uri.parse("content://com.example.androidtest.provider/book")
            val values = contentValuesOf(
                "name" to "A Clash of Kings",
                "author" to "George Martin",
                "pages" to 1040,
                "price" to 22.85
            )
            val newUri = contentResolver.insert(uri, values)
            bookId = newUri?.pathSegments?.get(1)
        }
        queryData.setOnClickListener {
            val uri = Uri.parse("content://com.example.androidtest.provider/book")
            contentResolver.query(uri, null, null, null, null)?.apply {
                val nameIndex = getColumnIndex("name")
                val authorIndex = getColumnIndex("author")
                val pagesIndex = getColumnIndex("pages")
                val priceIndex = getColumnIndex("price")

                while (moveToNext()) {
                    val name = if (nameIndex != -1) getString(nameIndex) else null
                    val author = if (authorIndex != -1) getString(authorIndex) else null
                    val pages = if (pagesIndex != -1) getString(pagesIndex) else null
                    val price = if (priceIndex != -1) getString(priceIndex) else null

                    Log.d("MyProviderActivity", "book name is $name")
                    Log.d("MyProviderActivity", "book author is $author")
                    Log.d("MyProviderActivity", "book pages is $pages")
                    Log.d("MyProviderActivity", "book price is $price")
                }
                close()
            }

        }
        updateData.setOnClickListener {
            bookId?.let {
                val uri = Uri.parse("content://com.example.androidtest.provider/book/$it")
                val values = contentValuesOf(
                    "name" to "A Storm of Swords",
                    "pages" to 1216,
                    "price" to 24.05
                )
                contentResolver.update(uri, values, null, null)
            }
        }
        deleteData.setOnClickListener {
            bookId?.let {
                val uri = Uri.parse("content://com.example.androidtest.provider/book/$it")
                contentResolver.delete(uri, null, null)
            }
        }
    }
}