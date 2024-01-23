package com.example.androidtest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class LoginActivity : BaseActivity() {
    private lateinit var accountEdit: EditText
    private lateinit var passwordEdit: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val login = findViewById<Button>(R.id.login)
        accountEdit = findViewById(R.id.accountEdit)
        passwordEdit = findViewById(R.id.passwordEdit)
        val prefs = getPreferences(Context.MODE_PRIVATE)
        val remember = findViewById<CheckBox>(R.id.rememberPassword)
        val isRemember = prefs.getBoolean("remember_password", false)
        remember.isChecked = isRemember
        if (isRemember) {
//            将账号和密码设置到文本框中
            val account = prefs.getString("account", "")
            val password = prefs.getString("password", "")
            accountEdit.setText(account)
            passwordEdit.setText(password)
        }
        login.setOnClickListener {
            val account = accountEdit.text.toString()
            val password = passwordEdit.text.toString()
            if (account == "admin" && password == "123456") {
                val editor = prefs.edit()
                if (remember.isChecked) {
                    prefs.open {
                        putBoolean("remember_password", true)
                        putString("account", account)
                        putString("password", password)
                    }
                } else {
                    editor.clear()
                }
                editor.apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "account or password is invalid", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //   @Deprecated
    private fun onAccountDone() {
        //        用户名输入完成
        accountEdit.setOnFocusChangeListener { v, hasFocus ->
            Log.d("TAG", "用户名输入完成 $hasFocus ${passwordEdit.text.toString()}")
            if (!hasFocus && passwordEdit.text.toString().isEmpty()) {
                // 当按下"Done"按钮时，这里的代码会被执行
                // v是当前的EditText，你可以从中获取输入的文本
                val text = (v as EditText).text.toString()
                val cacheData = load().split(":")
                Log.d("TAG", "用户名输入完成 ${cacheData[0]} ${cacheData[1]}")
                if (cacheData.isNotEmpty() && text == cacheData[0]) {
                    passwordEdit.setText(cacheData[1])
                    passwordEdit.setSelection(cacheData[1].length)
                    Toast.makeText(this, "Restoring succeeded", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    //   @Deprecated
    private fun load(): String {
        val content = StringBuilder()
        try {
            val input = openFileInput("data")
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                reader.forEachLine {
                    content.append(it)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content.toString()
    }

    //   @Deprecated
    private fun save(inputText: String) {
        try {
            val output = openFileOutput("data", Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))
            Log.d("TAG", inputText)
            writer.use {
                it.write(inputText)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}