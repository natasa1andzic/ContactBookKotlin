package com.natasaandzic.domaci1.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.natasaandzic.domaci1.R
import com.natasaandzic.domaci1.db.ContactsDbHelper
import kotlinx.android.synthetic.main.activity_add_new_contact.*
import kotlinx.android.synthetic.main.activity_edit_contact.nameEt
import kotlinx.android.synthetic.main.activity_edit_contact.toolbar

class EditContactActivity : AppCompatActivity() {

    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)
        setSupportActionBar(toolbar)

        id = intent.getIntExtra(ContactDetailsActivity.EXTRA_ID, 0)
        val name = intent.getStringExtra(ContactDetailsActivity.EXTRA_NAME)
        val surname = intent.getStringExtra(ContactDetailsActivity.EXTRA_SURNAME)
        val number = intent.getStringExtra(ContactDetailsActivity.EXTRA_NUMBER)
        val email = intent.getStringExtra(ContactDetailsActivity.EXTRA_EMAIL)

        nameEt.setText(name)
        surnameEt.setText(surname)
        phoneEt.setText(number)
        emailEt.setText(email)

        saveBtn.setOnClickListener {

            val newName = nameEt.text.trim().toString()
            val newSurname = surnameEt.text.trim().toString()
            val newPhone = phoneEt.text.trim().toString()
            val newEmail = emailEt.text.trim().toString()

            ContactsDbHelper.getInstance(this@EditContactActivity)!!.updateContact(id.toLong(), newName, newSurname, newPhone, newEmail)

            val i = Intent(this@EditContactActivity, ContactDetailsActivity::class.java)

            i.putExtra(ContactDetailsActivity.EXTRA_ID, id)
            i.putExtra(ContactDetailsActivity.EXTRA_NAME, name)
            i.putExtra(ContactDetailsActivity.EXTRA_SURNAME, surname)
            i.putExtra(ContactDetailsActivity.EXTRA_NUMBER, number)
            i.putExtra(ContactDetailsActivity.EXTRA_EMAIL, email)

            setResult(Activity.RESULT_OK, i)
            finish()
        }
    }
}