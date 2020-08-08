package com.example.bakkeryApp.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.AddItemActivity
import com.example.bakkeryApp.R
import com.example.bakkeryApp.ViewSingleItem
import com.example.bakkeryApp.adapter.Orders_Adapter
import com.example.bakkeryApp.model.OrdersModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
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

class ViewItemsFragment : Fragment() {
    lateinit var  create_item:ImageView
    lateinit var  view_item:ImageView
    lateinit var  recyclerview:RecyclerView
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    var orderslist: ArrayList<OrdersModel.Datum> = ArrayList()
    lateinit var Ordersadapte: Orders_Adapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View= inflater.inflate(R.layout.fragment_view_items,container,false)
        create_item=view.findViewById(R.id.create_item)
        view_item=view.findViewById(R.id.view_item)
        recyclerview=view.findViewById(R.id.recyclerview)
        sessionManager = SessionManager(activity)
        progressDialog = ProgressDialog(activity)

        activity?.title  ="View Item"
        create_item.setOnClickListener {
            val intent= Intent(activity,AddItemActivity::class.java)
            activity?.startActivity(intent)
        }

        view_item.setOnClickListener {
            val intent= Intent(activity,ViewSingleItem::class.java)
            activity?.startActivity(intent)
        }
        ordersmethod()
      return view
    }

    private fun ordersmethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        val user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $user_token")
                .build()
            chain.proceed(newRequest)
        }.build()
        val requestInterface = Retrofit.Builder()
            .baseUrl(ApiManager.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        requestInterface.GetOrders().enqueue(object : Callback<OrdersModel> {
            override fun onResponse(
                call: Call<OrdersModel>,
                response: Response<OrdersModel>
            ) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + " error")
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    for (item in response.body().data!!) {

                        orderslist.addAll(listOf(item))
                    }
                    setadaptermethod()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<OrdersModel>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    activity,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setadaptermethod() {
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        Ordersadapte = Orders_Adapter(orderslist, activity)
        recyclerview.adapter =Ordersadapte
    }
    }
