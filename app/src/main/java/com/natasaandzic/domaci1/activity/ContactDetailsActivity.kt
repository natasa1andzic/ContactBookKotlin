package com.natasaandzic.domaci1.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.natasaandzic.domaci1.R
import com.natasaandzic.domaci1.db.ContactsDbHelper
import kotlinx.android.synthetic.main.activity_contact_details.*

class ContactDetailsActivity : AppCompatActivity() {

    private var id = 0

    private var shouldUpdateUIOnResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)

        setSupportActionBar(toolbar)

        id = intent.getIntExtra(EXTRA_ID, 0)
        val name: String = intent.getStringExtra(EXTRA_NAME)
        nameTv.text = name
        val surname = intent.getStringExtra(EXTRA_SURNAME)
        surnameTv.text = surname
        val phone = intent.getStringExtra(EXTRA_NUMBER)
        phoneTv.text = phone
        val email = intent.getStringExtra(EXTRA_EMAIL)
        emailTv.text = email

        deleteBtn.setOnClickListener {
            //Intent i = new Intent(ContactDetailsActivity.this,HomeActivity.class);
            //i.putExtra(ContactDetailsActivity.EXTRA_ID, id);
            ContactsDbHelper.getInstance(this@ContactDetailsActivity)!!.deleteContact(id.toLong())
            val newIntent = Intent()
            //Na ovoj liniji ne setuje request code, nego rezultat akcije to je Activity.RESULT_OK
            //setResult(REQUEST_CODE_EDIT_CONTACT, newIntent);
            setResult(Activity.RESULT_OK)
            finish()
        }

        editBtn.setOnClickListener {
            val intent = Intent(this@ContactDetailsActivity, EditContactActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_NAME, name)
            intent.putExtra(EXTRA_SURNAME, surname)
            intent.putExtra(EXTRA_NUMBER, phone)
            intent.putExtra(EXTRA_EMAIL, email)
            startActivityForResult(intent, REQUEST_CODE_EDIT_CONTACT)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE_EDIT_CONTACT || data == null) {
            return
        }

        /* int status = data.getIntExtra(ContactDetailsActivity.EXTRA_CONTACT_EDIT_STATUS, -1);

        if (status == ContactDetailsActivity.CONTACT_EDITED){
;*/
        val name = data.getStringExtra(EXTRA_NAME)
        val surname = data.getStringExtra(EXTRA_SURNAME)
        val number = data.getStringExtra(EXTRA_NUMBER)
        val email = data.getStringExtra(EXTRA_EMAIL)
        shouldUpdateUIOnResume = true
    }

    override fun onResume() {
        super.onResume()
        if (shouldUpdateUIOnResume) {
            shouldUpdateUIOnResume = false
            nameTv.setText(name)
            surnameTv.setText(surname)
            numberTv.setText(number)
            emailTv.setText(email)
        }
    }

    override fun onBackPressed() {
        //Ako pozovemo onbackpressed on ce da overriduje nas result ok sa result canceled
        //super.onBackPressed();

        //Intent i = new Intent();
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        const val REQUEST_CODE_EDIT_CONTACT = 1
        val EXTRA_ID: String? = "id"
        val EXTRA_NAME: String? = "name"
        val EXTRA_SURNAME: String? = "surname"
        val EXTRA_NUMBER: String? = "number"
        val EXTRA_EMAIL: String? = "email"
    }
}