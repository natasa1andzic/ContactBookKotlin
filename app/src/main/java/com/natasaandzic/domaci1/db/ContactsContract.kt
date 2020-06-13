package com.natasaandzic.domaci1.db

import android.provider.BaseColumns

class ContactsContract private constructor() {
    object ContactEntry : BaseColumns {
        val TABLE_NAME: String? = "contact"
        val COLUMN_NAME: String? = "name"
        val COLUMN_SURNAME: String? = "surname"
        val COLUMN_NUMBER: String? = "number"
        val COLUMN_EMAIL: String? = "email"
    }
}