package com.natasaandzic.domaci1.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.natasaandzic.domaci1.R
import com.natasaandzic.domaci1.db.ContactsDbHelper
import kotlinx.android.synthetic.main.activity_add_new_contact.*


class AddNewContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_contact)

        setSupportActionBar(toolbar)

        saveBtn.setOnClickListener {
            val name = nameEt.text.trim().toString()
            val surname = surnameEt.text.trim().toString()
            val phone = phoneEt.text.trim().toString()
            val email = emailEt.text.trim().toString()

            ContactsDbHelper.getInstance(this@AddNewContactActivity)!!.addContact(name, surname, phone, email)

            val i = Intent()
            setResult(Activity.RESULT_OK, i)
            finish()
        }

        captureBtn.setOnClickListener {
            val i = Intent(this@AddNewContactActivity, CaptureActivity::class.java)
            startActivity(i)
        }

    }

}