package com.cbe.bakery.sessionManager

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity


class SessionManager(context: FragmentActivity?) {
    private val mPreferences: SharedPreferences = context!!.getSharedPreferences("KEY", Context.MODE_PRIVATE)

private val editor = mPreferences.edit()

    /**
     * Set the value
     *
     * @param key   name to store and retrieve
     * @param value value
     */
    fun setSessionValue(key: String?, value: Any?) {

        if (value is Boolean) {
            editor.putBoolean(key, (value as Boolean?)!!)
        } else if (value is Int) {
            editor.putInt(key, (value as Int?)!!)
        } else {
            editor.putString(key, value as String?)
        }
        editor.apply()
    }
    fun getStringKey(key: String?): String? {
        return mPreferences.getString(key, "")
    }
    fun clearSession(){

        editor.clear()
        editor.apply()
    }
}