package com.cbe.bakery.utils

import android.os.AsyncTask
import android.widget.TextView
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

class AsyncTaskPrice(
    private var activity: FragmentActivity?,
    private var shopId: Long,
    private var itemId: Long,
    var edtAvailableQuantity: TextView,
    var edtAvailablePrice: TextView
) : AsyncTask<Any, Int, Any?>() {

    var returnVal = 0

    override fun onPreExecute() {
        super.onPreExecute()


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

        requestInterface.getAvailableQuantity(shopId,itemId).enqueue(object :
            Callback<AvailableQuantity> {
            override fun onResponse(
                call: Call<AvailableQuantity>,
                response: Response<AvailableQuantity>
            ) {
                if (response.code() == 200 && response.body() != null) {
                    returnVal = response.body().availableQuantity!!
                    edtAvailableQuantity.setText(returnVal.toString())
                    edtAvailablePrice.setText(response.body().mrp?.toString())
                } else {
//                    edtAvailablePrice.setText(response.body().mrp?.toString())
                    edtAvailableQuantity.setText("0")
                    edtAvailableQuantity.setText("0")
                }
            }

            override fun onFailure(call: Call<AvailableQuantity>, t: Throwable) {
                t.printStackTrace()
            }
        })

        return returnVal
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        return
    }
}
