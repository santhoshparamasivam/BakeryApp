package com.example.bakkerykotlin.sessionManager

import android.content.Context
import android.content.SharedPreferences


class SessionManager(context: Context) {
    private val mPreferences: SharedPreferences = context.getSharedPreferences("KEY", Context.MODE_PRIVATE)

private val editor = mPreferences.edit()
    var PRIVATE_MODE = 0

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

//    companion object {
//        private const val Messagebody = "messagebody"
//        private const val PREF_NAME = "FirebaseMessagae"
//
//    }
    /**
     * Initiate Session manager
     *
     * @param context current context
     */
//    init {
//        pref = context.getSharedPreferences(
//            PREF_NAME,
//            PRIVATE_MODE
//        )
//        editor = pref.edit()
//    }
}