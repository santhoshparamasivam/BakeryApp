package com.cbe.bakery

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.fragments.AddStockItemFragment
import com.cbe.bakery.fragments.AddStockLocationFragment
import com.cbe.bakery.model.ShopModel
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.ViewUtils
import com.cbe.bakery.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AddStockActivity : AppCompatActivity() {
    private lateinit var viewUtils: ViewUtils
    lateinit var toolbar: Toolbar
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    private lateinit var productDialog: Dialog
    lateinit var recyclerview: RecyclerView
    var shopList: ArrayList<ShopModel> = ArrayList()
    lateinit var navBottomView: BottomNavigationView
    var itemId: Long = 0
    var searchList: ArrayList<String> = ArrayList()
    var type=""
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stock)
        viewUtils = ViewUtils()
        var intent=intent
        type=intent.getStringExtra("type")!!
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        toolbar = findViewById(R.id.toolbar)
        navBottomView=findViewById(R.id.nav_bottomView)
        setSupportActionBar(toolbar)
        if(type=="removeStock") {
            supportActionBar?.title = "Remove Stock"
        }else if(type=="adStock")
            supportActionBar?.title = "Add Stock"


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        navBottomView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.addStockByItem -> {
                    loadFragment(AddStockItemFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.addStockByLocation -> {
                    loadFragment(AddStockLocationFragment())
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }

            getShopName()

            loadFragment(AddStockItemFragment())
        }

        private fun loadFragment(fragment: Fragment) {
            val bundle = Bundle()
            bundle.putString("type", type)
            fragment.arguments = bundle
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()

        }

    private fun getShopName() {
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
            requestInterface.getShopsList().enqueue(object : Callback<ArrayList<ShopModel>> {
                override fun onResponse(
                    call: Call<ArrayList<ShopModel>>,
                    response: Response<ArrayList<ShopModel>>
                ) {
                    Log.e("response", response.code().toString() + " error")
                    if (response.code() == 200) {
                        shopList = response.body()
                        for (items in shopList) {
                            searchList.add(items.name!!)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please try again later",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<ArrayList<ShopModel>>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(
                        applicationContext,
                        "Connection failed,Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }


