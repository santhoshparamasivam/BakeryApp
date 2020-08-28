package com.example.bakkeryApp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bakkeryApp.R


class ViewUtils {

    private var alert: AlertDialog? = null
    fun alertViewDialog(
        mContext: Activity?,
        message: String?,
        success_txt: String?,
        failure_txt: String?,
        cancelable_val: Boolean?,
        positiveDialogInterface: DialogInterface.OnClickListener?,
        negative_dialogInterface: DialogInterface.OnClickListener?
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
            alert!!.setOnShowListener {
                if (alert != null) {
                    alert!!.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(mContext.resources.getColor(R.color.black))
                    alert!!.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(mContext.resources.getColor(R.color.black))
                }
            }
            alert!!.show()
        }
        return alert
    }

    //common toast for full Project
    fun showToast(activity: Activity, message: String, toastDuration: Int) {
        if (activity.isFinishing) {
            return
        }
        val inflater = activity.layoutInflater
        val layout: View = inflater.inflate(R.layout.toast, null)
        val text = layout.findViewById<TextView>(R.id.text)
        text.text = message
        val toast = Toast(activity.applicationContext)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = toastDuration
        toast.show()
    }
    //common Alert for Full Project
    fun bakeryAlert(
        context: Context?, message: String?, posTxt: String?,
        posClickCallListener: DialogInterface.OnClickListener?, negTxt: String?,
        negClickCallListener: DialogInterface.OnClickListener?, isCancelable: Boolean
    ): Dialog? {
        var alertDialog = context?.let { Dialog(it) }
        var window: Window? = alertDialog?.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog?.setContentView(R.layout.alert_bakkery)
        alertDialog?.setCancelable(isCancelable)
        alertDialog?.setCanceledOnTouchOutside(isCancelable)

        (alertDialog?.findViewById(R.id.messageTxt) as TextView).text = message
        var posTv  = alertDialog.findViewById<TextView>(R.id.btn_pos)
        if (!TextUtils.isEmpty(posTxt)) {
            posTv.text = posTxt
            posTv.setOnClickListener {
                if (posClickCallListener != null) {
                    posClickCallListener.onClick(alertDialog, 0)
                } else {
                    alertDialog.dismiss()
                }
            }
        } else {
            posTv.visibility = View.GONE
        }
        var negTv: TextView = alertDialog.findViewById(R.id.btn_neg)
        if (!TextUtils.isEmpty(negTxt)) {
            negTv.text = negTxt
            negTv.setOnClickListener {
                if (negClickCallListener != null) {
                    negClickCallListener.onClick(alertDialog, 0)
                } else {
                    alertDialog.dismiss()
                }
            }
        } else {
            negTv.visibility = View.GONE
        }
        alertDialog.show()
        return alertDialog
    }


}
