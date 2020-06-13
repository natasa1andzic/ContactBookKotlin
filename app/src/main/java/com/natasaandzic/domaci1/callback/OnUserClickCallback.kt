package com.natasaandzic.domaci1.callback


interface OnUserClickCallback {
     fun onUserClick(userId: Int, name: String?, surname: String?, number: String?, email: String?, position: Int)
}