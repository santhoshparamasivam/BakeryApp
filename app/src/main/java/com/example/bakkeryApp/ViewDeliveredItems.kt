package com.example.bakkeryApp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.model.OrdersModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.utils.RecyclerItemClickListener
import com.example.bakkeryApp.adapter.Orders_Adapter
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import kotlinx.android.synthetic.main.activity_view_delivered_items.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewDeliveredItems : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    lateinit var recyclerview: RecyclerView
    var orderslist: ArrayList<OrdersModel.Datum> = ArrayList()
    lateinit var Ordersadapte:Orders_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_delivered_items)
        recyclerview=findViewById(R.id.recyclerview)
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
//        ordersmethod()


        recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(this, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    Log.e( "orderd list", orderslist[position].itemId+" "+orderslist[position].shopId)
                    val intent=Intent(applicationContext,ViewSingleItem::class.java)
                    intent.putExtra("itemId",orderslist[position].itemId)
                    intent.putExtra("shopId",orderslist[position].shopId)
                    startActivity(intent)

                }
            })
        )
    }

//    private fun ordersmethod() {
//        progressDialog.setMessage("Loading...")
//        progressDialog.show()
//        val user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
//        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
//            val newRequest: Request = chain.request().newBuilder()
//                .addHeader("Authorization", "Bearer $user_token")
//                .build()
//            chain.proceed(newRequest)
//        }.build()
//        val requestInterface = Retrofit.Builder()
//            .baseUrl(ApiManager.BASE_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build().create(ApiService::class.java)
//        requestInterface.GetOrders().enqueue(object : Callback<OrdersModel> {
//            override fun onResponse(
//                call: Call<OrdersModel>,
//                response: Response<OrdersModel>
//            ) {
//                progressDialog.dismiss()
//                Log.e("response", response.code().toString() + " error")
//                if (response.code() == 200) {
//                    progressDialog.dismiss()
//                    for (item in response.body().data!!) {
//
////                        val list = arrayListOf(response.body().data!!.toTypedArray())
//                        orderslist.addAll(listOf(item))
//                        }
////                    orderslist = response.body().data!!
//                    Log.e("orderslist", orderslist.size.toString() + " error")
//                    setadaptermethod()
//                } else {
//                    progressDialog.dismiss()
//                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//
//            override fun onFailure(call: Call<OrdersModel>, t: Throwable) {
//                t.printStackTrace()
//                progressDialog.dismiss()
//                Toast.makeText(
//                    applicationContext,
//                    "Connection failed,Please try again later",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        })
//    }

    private fun setadaptermethod() {

        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)

        recyclerview.setHasFixedSize(true)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Ordersadapte.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        Ordersadapte = Orders_Adapter(orderslist, this)
        recyclerview.adapter =Ordersadapte
    }
}