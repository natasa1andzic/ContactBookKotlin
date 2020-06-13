package com.natasaandzic.domaci1.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.natasaandzic.domaci1.db.ContactsContract.ContactEntry


class ContactsDbHelper private constructor(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " +
                ContactEntry.TABLE_NAME + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContactEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ContactEntry.COLUMN_SURNAME + " TEXT, " +
                ContactEntry.COLUMN_NUMBER + " TEXT, " +
                ContactEntry.COLUMN_EMAIL + " TEXT" +
                ");"
        db.execSQL(SQL_CREATE_CONTACT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME)
        onCreate(db)
    }

    fun addContact(name: String?, surname: String?, number: String?, email: String?): Long {
        val cv = ContentValues()
        cv.put(ContactEntry.COLUMN_NAME, name)
        cv.put(ContactEntry.COLUMN_SURNAME, surname)
        cv.put(ContactEntry.COLUMN_NUMBER, number)
        cv.put(ContactEntry.COLUMN_EMAIL, email)
        return writableDatabase.insert(ContactEntry.TABLE_NAME, null, cv)
    }

    fun deleteContact(id: Long) {
        this.writableDatabase.execSQL("DELETE FROM " + ContactEntry.TABLE_NAME + " WHERE _id='" + id + "'")
    }

    /* First make a ContentValues object :

        ContentValues cv = new ContentValues();
        cv.put("Field1","Bob"); //These Fields should be your String values of actual column names
        cv.put("Field2","19");
        cv.put("Field2","Male");
        Then use the update method, it should work now:

        myDB.update(TableName, cv, "_id="+id, null);

        i

        https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/

        i

        https://www.youtube.com/watch?v=dvDTmJtGE_I */
    fun updateContact(id: Long, name: String?, surname: String?, number: String?, email: String?) {
        val s = ("UPDATE " + ContactEntry.TABLE_NAME + " SET "
                + ContactEntry.COLUMN_NAME + " ='" + name + "', "
                + ContactEntry.COLUMN_SURNAME + " ='" + surname + "', "
                + ContactEntry.COLUMN_NUMBER + " ='" + number + "', "
                + ContactEntry.COLUMN_EMAIL + " ='" + email + "' "
                + "WHERE " + BaseColumns._ID + " ='" + id + "'")
        Log.v(TAG, s)
        this.writableDatabase
                .execSQL(s)
    }

    fun finalize() {}

    companion object {
        private val TAG: String? = "ContactsDbHelper"
        val DATABASE_NAME: String? = "contacts.com.natasaandzic.domaci1.db"
        const val DATABASE_VERSION = 2
        private var mInstance: ContactsDbHelper? = null

        @Synchronized
        fun getInstance(context: Context?): ContactsDbHelper? {
            if (mInstance == null) {
                mInstance = ContactsDbHelper(context.getApplicationContext())
            }
            return mInstance
        }
    }
}