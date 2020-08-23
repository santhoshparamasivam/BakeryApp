package com.example.bakkeryApp.utils

import android.R
import android.app.Activity
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import androidx.appcompat.app.AlertDialog


class ViewUtils {

    private var alert: AlertDialog? = null
    fun  alertViewDialog(
        mContext: Activity?,
        title: String?,
        message: String?,
        success_txt: String?,
        failure_txt: String?,
        cancelable_val: Boolean?,
        positiveDialogInterface: DialogInterface.OnClickListener?,
        negative_dialogInterface: DialogInterface.OnClickListener?,
        s: String?
    ): AlertDialog? {
        if (mContext != null) {
            val dialog = AlertDialog.Builder(mContext)
            dialog.setCancelable(cancelable_val!!)
            dialog.setMessage(message)
            dialog.setPositiveButton(success_txt, positiveDialogInterface)
            if (negative_dialogInterface != null) {
                dialog.setNegativeButton(failure_txt, negative_dialogInterface)
            }
            alert = dialog.create()
            alert!!.setOnShowListener(OnShowListener {
                if (alert != null) {
                    alert!!.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(mContext.resources.getColor(R.color.black))
                    alert!!.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(mContext.resources.getColor(R.color.black))
                }
            })
            alert!!.show()
        }
        return alert
    }
//    var multiStockList: List<MultiStockAdd> = ArrayList<MultiStockAdd>(
//        listOf(
//            MultiStockAdd(
//                "" + 0,
//                ""
//            )
//        )
//    )


}
