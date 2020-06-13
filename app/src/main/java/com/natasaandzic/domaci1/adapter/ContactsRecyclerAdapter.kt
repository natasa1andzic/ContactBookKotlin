package com.natasaandzic.domaci1.adapter

import android.database.Cursor
import android.provider.BaseColumns

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.natasaandzic.domaci1.R
import com.natasaandzic.domaci1.adapter.ContactsRecyclerAdapter.ContactHolder
import com.natasaandzic.domaci1.callback.OnUserClickCallback
import com.natasaandzic.domaci1.db.ContactsContract.ContactEntry


class ContactsRecyclerAdapter(private var mCursor: Cursor?,
                              private val mOnUserClickCallback: OnUserClickCallback?) : RecyclerView.Adapter<ContactHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)
        return ContactHolder(view)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        if (!mCursor!!.moveToPosition(position)) {
            return
        }
        holder.nameTv.setText(mCursor.getString(mCursor.getColumnIndex(ContactEntry.COLUMN_NAME)))
        holder.surnameTv.setText(mCursor.getString(mCursor.getColumnIndex(ContactEntry.COLUMN_SURNAME)))
    }

    fun setCursor(cursor: Cursor?) {
        if (mCursor != null) {
            mCursor.close()
        }
        mCursor = cursor
        if (mCursor != null) {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mCursor!!.count
    }

    inner class ContactHolder(itemView: View?) : ViewHolder(itemView), View.OnClickListener {

        override fun onClick(v: View?) {

            mCursor!!.moveToPosition(adapterPosition)

            val id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID))
            val name = mCursor.getString(mCursor.getColumnIndex(ContactEntry.COLUMN_NAME))
            val surname = mCursor.getString(mCursor.getColumnIndex(ContactEntry.COLUMN_SURNAME))
            val number = mCursor.getString(mCursor.getColumnIndex(ContactEntry.COLUMN_NUMBER))
            val email = mCursor.getString(mCursor.getColumnIndex(ContactEntry.COLUMN_EMAIL))
            Log.v(TAG, v.javaClass.toString() + "")
            mOnUserClickCallback.onUserClick(
                    id,
                    name,
                    surname,
                    number,
                    email,
                    adapterPosition)
        }

        init {
            itemView!!.setOnClickListener(this)
        }
    }

    companion object {
        private val TAG: String? = "ContactsRecyclerAdapter"
    }




}