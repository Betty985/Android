package com.example.androidtest

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ContactsActivity : AppCompatActivity() {
    private val contactsList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactsList)
        val contactsView = findViewById<ListView>(R.id.contactsView)
        contactsView.adapter = adapter
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 2)
        } else {
            readContacts()
        }
//        取消通知的方式
//        val manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.cancel(1)
    }

    private fun readContacts() {
//        查询联系人数据
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )?.apply {
            while (moveToNext()) {
                val displayNameIndex =
                    getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                获取联系人姓名
                val displayName =
                    if (displayNameIndex != -1) getString(displayNameIndex) else "Unknown"
//                获取联系人手机号
                val number = if (numberIndex != -1) getString(numberIndex) else "Unknown"

                contactsList.add("$displayName\n$number")
            }
            adapter.notifyDataSetChanged()
            close()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            2->{
                if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts()
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}