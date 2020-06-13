package com.natasaandzic.domaci1.activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.natasaandzic.domaci1.R
import com.natasaandzic.domaci1.adapter.ContactsRecyclerAdapter
import com.natasaandzic.domaci1.callback.OnUserClickCallback
import com.natasaandzic.domaci1.db.ContactsContract.ContactEntry
import com.natasaandzic.domaci1.db.ContactsDbHelper
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerAdapter: ContactsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)

        contactListRv.setHasFixedSize(true)
        val lm = LinearLayoutManager(this)
        contactListRv.layoutManager = lm
        val mDividerItemDecoration = DividerItemDecoration(contactListRv.context,
                lm.orientation)
        contactListRv.addItemDecoration(mDividerItemDecoration)
        val mOnUserClickCallback = UserClickCallback()
        recyclerAdapter = ContactsRecyclerAdapter(getCursor(), mOnUserClickCallback)
        contactListRv.adapter = recyclerAdapter

        addBtn.setOnClickListener {
            val i = Intent(this@HomeActivity, AddNewContactActivity::class.java)
            startActivityForResult(i, REQUEST_CODE_MANAGE_CONTACT)
        }
    }

    private fun getCursor(): Cursor? {
        val db: SQLiteDatabase = ContactsDbHelper.getInstance(this)!!.writableDatabase
        return db.query(
                ContactEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ContactEntry.COLUMN_NAME + " ASC"
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(TAG, "ACTIVITY RESULT")
        if (requestCode == REQUEST_CODE_MANAGE_CONTACT) {
            Log.v(TAG, "RQ MANAGE CONTACT")
        }
        Log.v(TAG, "" + resultCode)
        if (resultCode == Activity.RESULT_OK) {
            Log.v(TAG, "OK RESULT")
        }
        if (requestCode == REQUEST_CODE_MANAGE_CONTACT && resultCode == Activity.RESULT_OK) {
            recyclerAdapter.setCursor(getCursor())
        }
    }

    private inner class UserClickCallback : OnUserClickCallback {
        override fun onUserClick(userId: Int, name: String?, surname: String?, number: String?, email: String?, position: Int) {
            Log.v(TAG, "name " + "surname")
            val intent = Intent(this@HomeActivity, ContactDetailsActivity::class.java)
            intent.putExtra(EXTRA_ID, userId)
            intent.putExtra(EXTRA_NAME, name)
            intent.putExtra(EXTRA_SURNAME, surname)
            intent.putExtra(EXTRA_NUMBER, number)
            intent.putExtra(EXTRA_EMAIL, email)
            startActivityForResult(intent, REQUEST_CODE_MANAGE_CONTACT)
        }
    }

    companion object {
        private val TAG: String? = "HomeActivity"
        val EXTRA_ID: String? = "id"
        val EXTRA_NAME: String? = "name"
        val EXTRA_SURNAME: String? = "surname"
        val EXTRA_NUMBER: String? = "number"
        val EXTRA_EMAIL: String? = "email"
        const val REQUEST_CODE_MANAGE_CONTACT = 1
    }
}