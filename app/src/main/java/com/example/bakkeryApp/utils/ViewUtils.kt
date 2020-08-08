package com.example.bakkeryApp.utils

import android.R
import android.app.Activity
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import androidx.appcompat.app.AlertDialog


class ViewUtils {

    var alert: AlertDialog? = null
    fun  alert_view_dialog(
        mContext: Activity?,
        title: String?,
        message: String?,
        success_txt: String?,
        failure_txt: String?,
        cancelable_val: Boolean?,
        postive_dialogInterface: DialogInterface.OnClickListener?,
        negative_dialogInterface: DialogInterface.OnClickListener?,
        s: String?
    ): AlertDialog? {
        if (mContext != null) {
            val dialog = AlertDialog.Builder(mContext)
            dialog.setCancelable(cancelable_val!!)
            dialog.setMessage(message)
            dialog.setPositiveButton(success_txt, postive_dialogInterface)
            if (negative_dialogInterface != null) {
                dialog.setNegativeButton(failure_txt, negative_dialogInterface)
            }
            alert = dialog.create()
            alert!!.setOnShowListener(OnShowListener {
                if (alert != null) {
                    alert!!.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(mContext.resources.getColor(R.color.darker_gray))
                    alert!!.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(mContext.resources.getColor(R.color.darker_gray))
                }
            })
            alert!!.show()
        }
        return alert
    }

}
