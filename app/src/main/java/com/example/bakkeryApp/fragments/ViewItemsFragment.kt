package com.example.bakkeryApp.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bakkeryApp.AddItemActivity
import com.example.bakkeryApp.HomeActivity
import com.example.bakkeryApp.R
import com.example.bakkeryApp.adapter.ItemsAdapter
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList


class ViewItemsFragment : Fragment() {
    private lateinit var  createItem:ImageView
    private lateinit var  viewItem:ImageView
    lateinit var  recyclerview:RecyclerView
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    var itemList: ArrayList<ItemsModel> = ArrayList()
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var  swipeRefresh: SwipeRefreshLayout
    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View= inflater.inflate(R.layout.fragment_view_items, container, false)
        createItem=view.findViewById(R.id.create_item)
        viewItem=view.findViewById(R.id.view_item)
        recyclerview=view.findViewById(R.id.recyclerview)
        swipeRefresh=view.findViewById(R.id.swipeRefresh)
        sessionManager = SessionManager(activity)
        progressDialog = ProgressDialog(activity)

        activity?.title  ="Items"
        (activity as HomeActivity?)?.searchView!!.visibility = View.VISIBLE

        (activity as HomeActivity?)?.fab!!.visibility = View.VISIBLE

//        (activity as HomeActivity?)?.searchView!!.setOnClickListener {
//            (activity as HomeActivity?)?.searchView!!.onActionViewExpanded()
//        }

        (activity as HomeActivity?)?.appBar!!.visibility = View.VISIBLE
        (activity as HomeActivity?)?.fab!!.setOnClickListener {
            val intent= Intent(activity, AddItemActivity::class.java)
            activity?.startActivity(intent)

        }


        (activity as HomeActivity?)?.searchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
         override fun onQueryTextChange(newText: String): Boolean {
        val finalItemList = ArrayList<ItemsModel>()
        for (item in itemList) {
//            if (item.name?.toLowerCase()?.contains(newText)!!) {
//                finalItemList.add(item)
//            }
            if (item.name.toString().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT)) ||item.itemCategory.toString().toLowerCase(
                    Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))) {
                finalItemList.add(item)
            }
        }
        setAdapterMethod(finalItemList)
        return false
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        // task HERE
        return false
    }

})
        createItem.setOnClickListener {

        }
        swipeRefresh.setOnRefreshListener {
            ordersMethod()
            swipeRefresh.isRefreshing = false
        }
      return view
    }

    private fun ordersMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
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
        requestInterface.getAllItems().enqueue(object : Callback<ArrayList<ItemsModel>> {
            override fun onResponse(
                call: Call<ArrayList<ItemsModel>>,
                response: Response<ArrayList<ItemsModel>>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {

                    itemList = response.body()
                    setAdapterMethod(itemList)
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ArrayList<ItemsModel>>, t: Throwable) {
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

    private fun setAdapterMethod(itemList: ArrayList<ItemsModel>) {
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        itemsAdapter = ItemsAdapter(itemList, activity)
        recyclerview.adapter =itemsAdapter
    }

    override fun onResume() {
        super.onResume()
        ordersMethod()
    }

    }

