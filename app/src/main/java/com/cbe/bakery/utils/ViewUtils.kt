package com.cbe.bakery.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.cbe.bakery.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


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
//    fun showToast(activity: Activity, message: String, toastDuration: Int) {
//        if (activity.isFinishing) {
//            return
//        }
//        val inflater = activity.layoutInflater
//        val layout: View = inflater.inflate(R.layout.toast, null)
//        val text = layout.findViewById<TextView>(R.id.text)
//        text.text = message
//        val toast = Toast(activity.applicationContext)
//        toast.setGravity(Gravity.CENTER, 0, 0)
//        toast.duration = toastDuration
//        toast.show()
//    }
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
        var posTv = alertDialog.findViewById<TextView>(R.id.btn_pos)
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

    @SuppressLint("SimpleDateFormat")
    fun dateConversion(strdate: String): String {
        val myFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
        val displayFormat = "yyyy-MM-dd HH:mm"
        val destFormat = SimpleDateFormat(displayFormat)
        val myDate: Date = myFormat.parse(strdate)
        return (destFormat.format(myDate))
    }

    fun pinVerificationAlert(
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
        var posTv = alertDialog.findViewById<TextView>(R.id.btn_pos)
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
    fun convertLongToTime(time: Long): String {
        Log.e("Time",time.toString()+" ")

        val date = Date(time)
        Log.e("date",date.toString()+" ")
        val format = SimpleDateFormat("yyyy/MM/dd")
        Log.e("format",format.format(date)+" ")
        return format.format(date)
    }

//    fun priceloadMethod(
//        fromactivity: FragmentActivity?,
//        shopsId: Long,
//        itemsId: Long,
//        edtQuantity: EditText,
//        edtPrice: EditText
//    ) {
//        @SuppressLint("StaticFieldLeak")
//        class AsyncTaskPrice(
//            private var activity: FragmentActivity?,
//            private var shopId: Long,
//            private var itemId: Long,
//            var edtAvailableQuantity: EditText,
//            var edtAvailablePrice: EditText
//        ) : AsyncTask<Any, Int, Any?>() {
//
//            var returnVal = 0
//
//
//            override fun onPreExecute() {
//                super.onPreExecute()
////        progressDialog = ProgressDialog(activity)
////        progressDialog.setMessage("Loading...")
////        progressDialog.show()
//                activity=fromactivity
//                shopId=shopsId
//                itemId=itemsId
//               edtAvailableQuantity=edtQuantity
//                edtAvailablePrice=edtPrice
//
//            }
//
//            override fun doInBackground(vararg req: Any?): Any? {
//
//                lateinit var sessionManager: SessionManager
//                sessionManager = SessionManager(this.activity)
//
//
//                var userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
//                val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
//                    val newRequest: Request = chain.request().newBuilder()
//                        .addHeader("Authorization", "Bearer $userToken")
//                        .build()
//                    chain.proceed(newRequest)
//                }.build()
//
//                val requestInterface = Retrofit.Builder()
//                    .baseUrl(ApiManager.BASE_URL)
//                    .client(client)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build().create(ApiService::class.java)
//
//                requestInterface.getAvailableQuantity(shopId,itemId).enqueue(object :
//                    Callback<AvailableQuantity> {
//                    override fun onResponse(
//                        call: Call<AvailableQuantity>,
//                        response: Response<AvailableQuantity>
//                    ) {
//                        if (response.code() == 200 && response.body() != null) {
//                            returnVal = response.body().availableQuantity!!
//                         edtAvailableQuantity.setText(returnVal.toString())
//                            edtAvailablePrice.setText(response.body().mrp?.toString())
////                    progressDialog.dismiss()
//                        } else {
//                            edtAvailablePrice.setText(response.body().mrp?.toString())
//                           edtAvailableQuantity.setText("0")
////                    progressDialog.dismiss()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<AvailableQuantity>, t: Throwable) {
//                        t.printStackTrace()
////                progressDialog.dismiss()
//                    }
//                })
//
//                return returnVal
//            }
//
//            override fun onPostExecute(result: Any?) {
//                super.onPostExecute(result)
//                return
//            }
//        }
//    }
}
