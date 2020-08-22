package com.example.bakkeryApp

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.adapter.CustomListAdapter
import com.example.bakkeryApp.adapter.StoreAdapter
import com.example.bakkeryApp.fragments.AddStockItemFragment
import com.example.bakkeryApp.fragments.AddStockLocationFragment
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.model.MultiStockAdd
import com.example.bakkeryApp.model.ShopModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.example.bakkeryApp.utils.RecyclerItemClickListener
import com.example.bakkeryApp.utils.ViewUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_add_stock.*
import kotlinx.android.synthetic.main.tb_add_item.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt


class AddStockActivity : AppCompatActivity() {
    var multiStockList: ArrayList<MultiStockAdd> = ArrayList()
    lateinit var viewUtils: ViewUtils
    lateinit var toolbar: Toolbar
    var itemlist: ArrayList<ItemsModel> = ArrayList()
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    private lateinit var productDialog: Dialog
    lateinit var listView: ListView
    lateinit var search: EditText
    lateinit var myListAdapter: CustomListAdapter
    var newList: ArrayList<ItemsModel> = ArrayList()
    lateinit var shopDialog: Dialog
    lateinit var storeAdapter: StoreAdapter
    lateinit var recyclerview: RecyclerView
    var shopList: ArrayList<ShopModel> = ArrayList()
    var finalShopList: ArrayList<ShopModel> = ArrayList()
    lateinit var add_stock_location: TextView
    lateinit var add_stock_item: TextView
    lateinit var navBottomview: BottomNavigationView
    var itemId: Long = 0
    var searchList: ArrayList<String> = ArrayList()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stock)
        viewUtils = ViewUtils()
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        toolbar = findViewById(R.id.toolbar)
        navBottomview=findViewById(R.id.nav_bottomView)
//        add_stock_item=findViewById(R.id.add_stock_item)
//        add_stock_location=findViewById(R.id.add_stock_location)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add Stock"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
//        add_stock_item.setBackgroundColor(Color.WHITE)
//        add_stock_item.setTextColor(Color.BLACK)
//        add_stock_location.setBackgroundColor(Color.TRANSPARENT)
//        add_stock_location.setTextColor(null)
        navBottomview.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.addStockByItem -> {
                    LoadFragment(AddStockItemFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.addStockByLocation -> {
                    LoadFragment(AddStockLocationFragment())
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }

//        add_stock_item.setOnClickListener {
//            add_stock_item.setBackgroundColor(Color.WHITE)
//            add_stock_item.setTextColor(Color.BLACK)
//            add_stock_location.setBackgroundColor(Color.TRANSPARENT)
//            add_stock_location.setTextColor(Color.WHITE)
//            LoadFragment(AddStockItemFragment())
//        }
//        add_stock_location.setOnClickListener {
//            add_stock_location.setBackgroundColor(Color.WHITE)
//            add_stock_location.setTextColor(Color.BLACK)
//            add_stock_location.setBackgroundColor(Color.TRANSPARENT)
//            add_stock_location.setTextColor(Color.WHITE)
//            LoadFragment(AddStockLocationFragment())
//        }


//        lyt_add_item.setOnClickListener {
//            MultiStockList.add(MultiStockAdd("1", "1"))
//
//            MultiItemAdded()
//        }
//        edt_category.setOnClickListener {
////            ShowItemDialog()
//            getProductName()
//        }
//        create_item.setOnClickListener {
//            if(edt_category.text.toString().isEmpty()){
//                edt_category.error="Please Enter Category.."
//            }else
//                AddStockToServer()
            getShopName()
//        MultiItemAdded()

            LoadFragment(AddStockItemFragment())

        }

        private fun LoadFragment(fragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()

        }

        private fun AddStockToServer() {

            var objects = JsonObject()
            var jsonArray = JsonArray()

            for (row in shopList) {
                Log.e("row.name", "${row.name} ")
                Log.e("row.location", "${row.location} ")
                for (item in multiStockList) {

                    Log.e("item.location", "${item.location}  ")
                    if (row.name.equals(item.location)) {
                        var jsonObject = JsonObject()
                        jsonObject.addProperty("shopId", Integer.parseInt(row.id!!))
                        jsonObject.addProperty("quantity", Integer.parseInt(item.quantity!!))
                        jsonArray.add(jsonObject)
                        Log.e("jsonArray", "$jsonArray  ")
                    }
                }
            }
            objects.addProperty("itemId", itemId)
            objects.add("stock", jsonArray)

            Log.e("objects", "$objects  ")
            val progressDialog = ProgressDialog(this@AddStockActivity)
            progressDialog.setMessage("Loading...")
            progressDialog.show()
            var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
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

            requestInterface.stockByItem(objects).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    progressDialog.dismiss()
                    Log.e("response", response.code().toString() + " ")
                    if (response.code() == 200) {
                        Toast.makeText(
                            applicationContext,
                            "Stock Added SuccessFully",
                            Toast.LENGTH_LONG
                        ).show()
                        tblContact.removeAllViews()
                        edt_category.text = null
                        edt_item.text = null
                        edt_sell_Price.text = null
                    } else {
                        progressDialog.dismiss()
                        Log.e("response", response.message() + "")
                        Toast.makeText(
                            applicationContext,
                            "Please Check Store name and try again later",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dismiss()
                    t.printStackTrace()
                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            })

        }

    private fun MultiItemAdded() {
        if (multiStockList == null) return
        val inflater = LayoutInflater.from(this@AddStockActivity)
        tblContact.removeAllViews()
        for (contact in multiStockList) {
            val row =
                inflater.inflate(R.layout.tb_add_item, null) as TableRow
            val btnDelete =
                row.findViewById<View>(R.id.btnDelete) as ImageView
            val edtContact =
                row.findViewById<View>(R.id.edtLocation) as AutoCompleteTextView
            val edtType = row.findViewById<View>(R.id.edtQuantity) as EditText
            val adapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, android.R.layout.select_dialog_item, searchList)
            edtContact.threshold = 1;
            edtContact.setAdapter(adapter);

            edtContact.tag = contact
            edtContact.setText(contact.location)
            edtContact.isClickable=false
            edtType.setText(contact.quantity)
            btnDelete.setOnClickListener {
                val multiContact: MultiStockAdd =
                edtContact.tag as MultiStockAdd
               multiStockList.remove(multiContact)
               MultiItemAdded()
            }
            edtContact.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.isNotEmpty()) {
                        val multiContact: MultiStockAdd = edtContact.tag as MultiStockAdd
                        multiStockList.remove(multiContact)
                        multiContact.location = edtContact.text.toString()
                        multiStockList.add(multiContact)
                    }
                }
            })
            edtType.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.length != 0) {
                        val multiContact: MultiStockAdd =
                            edtContact.tag as MultiStockAdd
                        multiStockList.remove(multiContact)
                        multiContact.quantity=edtType.text.toString()
                       multiStockList.add(multiContact)
                    }
                }
            })
            tblContact.addView(row)
        }
    }
    private fun getShopName() {
//        progressDialog.setMessage("Loading...")
//        progressDialog.show()
            var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
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
            requestInterface.getShopsList().enqueue(object : Callback<ArrayList<ShopModel>> {
                override fun onResponse(
                    call: Call<ArrayList<ShopModel>>,
                    response: Response<ArrayList<ShopModel>>
                ) {
//                progressDialog.dismiss()
                    Log.e("response", response.code().toString() + " error")
                    if (response.code() == 200) {
//                    progressDialog.dismiss()
                        shopList = response.body()
                        for (items in shopList) {
                            searchList.add(items.name!!)
                        }
//                    ShowShopName()
                    } else {
//                    progressDialog.dismiss()
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
//                progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Connection failed,Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        private fun ShowShopName() {
            finalShopList.clear()
            shopDialog = Dialog(this)
            shopDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            shopDialog.setCancelable(true)
            shopDialog.setContentView(R.layout.custom_layout)
            recyclerview = shopDialog.findViewById(R.id.recyclerview)
            search = shopDialog.findViewById(R.id.search)
            recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
            recyclerview.setHasFixedSize(true)
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    finalShopList.clear()
                    for (item in shopList) {
                        if (item.name?.toLowerCase()?.contains(charSequence.toString())!!) {
                            finalShopList.add(item)
                        }
                    }
                    storeAdapter = StoreAdapter(finalShopList, applicationContext)
                    recyclerview.adapter = storeAdapter
                }

                override fun afterTextChanged(editable: Editable) {}
            })
            finalShopList.addAll(shopList)
            storeAdapter = StoreAdapter(finalShopList, this)
            recyclerview.adapter = storeAdapter
            recyclerview.addOnItemTouchListener(
                RecyclerItemClickListener(
                    this,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            if (shopDialog.isShowing) {
                                shopDialog.dismiss()
                            }
                            edtLocation.setText(finalShopList[position].name.toString())
                            Log.e("position", finalShopList[position].name.toString() + "  ");
                            val multiContact: MultiStockAdd =
                                edtLocation.tag as MultiStockAdd
                            multiStockList.remove(multiContact)
                            multiContact.location = edtLocation.text.toString()
                            multiStockList.add(multiContact)
                            edtLocation.invalidate()
                        }
                    })
            )
            val metrics = DisplayMetrics()
            this.windowManager.defaultDisplay.getMetrics(metrics)
            val height = (metrics.heightPixels * 0.5)  //set height to 90% of total
            val width = (metrics.widthPixels * 0.9) //set width to 90% of total
            shopDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
            shopDialog.show()
        }

        private fun getProductName() {
            progressDialog.setMessage("Loading...")
            progressDialog.show()
            itemlist.clear()
            newList.clear()
            var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN)
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
            requestInterface.getAllItems().enqueue(object : Callback<ArrayList<ItemsModel>> {
                override fun onResponse(
                    call: Call<ArrayList<ItemsModel>>,
                    response: Response<ArrayList<ItemsModel>>
                ) {
                    progressDialog.dismiss()
                    Log.e("response", response.code().toString() + "  rss")
                    if (response.code() == 200) {
                        itemlist = response.body()
                        ShowItemDialog()
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@AddStockActivity,
                            "Please try again later",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<ArrayList<ItemsModel>>, t: Throwable) {
                    t.printStackTrace()
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@AddStockActivity,
                        "Connection failed,Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        private fun ShowItemDialog() {
            productDialog = Dialog(this)
            productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            productDialog.setCancelable(true)
            productDialog.setContentView(R.layout.alert_product)
            listView = productDialog.findViewById(R.id.list_item)
            search = productDialog.findViewById(R.id.search)
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                myListAdapter.filter?.filter(charSequence.toString())
                    newList.clear()
                    for (item in itemlist) {
                        if (item.name?.toLowerCase()?.contains(charSequence.toString())!!) {
                            newList.add(item)
                        }
                    }
                    myListAdapter = CustomListAdapter(applicationContext, newList)
                    listView.adapter = myListAdapter
                }

                override fun afterTextChanged(editable: Editable) {}
            })

//        if(newList==null && newList.size==0) {
            newList.addAll(itemlist)
//               myListAdapter = CustomListAdapter(this, Itemlist)
//               listView.adapter = myListAdapter
//        }  else {
            myListAdapter = CustomListAdapter(this, newList)
            listView.adapter = myListAdapter
//        }

            listView.setOnItemClickListener { adapterView, view, position: Int, id: Long ->
                if (productDialog.isShowing) {
                    productDialog.dismiss()
                    edt_category.setText(newList[position].name)
                    itemId = newList[position].id!!
                }
            }
            val metrics = DisplayMetrics()

            this!!.windowManager.defaultDisplay.getMetrics(metrics)
            val height = (metrics.heightPixels * 0.5)

            val width = (metrics.widthPixels * 0.9)

            productDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
            productDialog.show()

        }


    }


