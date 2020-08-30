package com.cbe.bakery.utils

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.cbe.bakery.model.AvailableQuantity
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@SuppressLint("StaticFieldLeak")
class AsyncTaskExample(
    private var activity: FragmentActivity?,
    private var shopId: Long,
    private var itemId: Long,
    var edtAvailableQuantity: EditText
) : AsyncTask<Any, Int, Any?>() {

    var returnVal = 0
//    lateinit var progressDialog: ProgressDialog

    override fun onPreExecute() {
        super.onPreExecute()
//        progressDialog = ProgressDialog(activity)
//        progressDialog.setMessage("Loading...")
//        progressDialog.show()
    }

    override fun doInBackground(vararg req: Any?): Any? {

        lateinit var sessionManager: SessionManager
        sessionManager = SessionManager(activity)


        var userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $userToken")
                .build()
            chain.proceed(newRequest)
        }.build()

        val requestInterface = Retrofit.Builder()
            .baseUrl(ApiManager.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)

        requestInterface.getAvailableQuantity(shopId, itemId).enqueue(object : Callback<AvailableQuantity> {
            override fun onResponse(call: Call<AvailableQuantity>, response: Response<AvailableQuantity>) {
                if (response.code() == 200 && response.body() != null) {
                    returnVal = response.body().availableQuantity!!
                    edtAvailableQuantity.setText(returnVal.toString())

//                    progressDialog.dismiss()
                } else {
                    edtAvailableQuantity.setText("0")
//                    progressDialog.dismiss()
                }
            }
            override fun onFailure(call: Call<AvailableQuantity>, t: Throwable) {
                t.printStackTrace()
//                progressDialog.dismiss()
            }
        })

        return returnVal
    }

    // it will update your progressbar
    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        return
    }
}