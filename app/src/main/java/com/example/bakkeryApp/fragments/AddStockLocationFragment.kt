package com.example.bakkeryApp.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.R
import com.example.bakkeryApp.adapter.CustomListAdapter
import com.example.bakkeryApp.adapter.StoreAdapter
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.model.MultiStockAdd
import com.example.bakkeryApp.model.ShopModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.example.bakkeryApp.utils.RecyclerItemClickListener
import com.example.bakkeryApp.utils.ViewUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_add_stock.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AddStockLocationFragment : Fragment(){
    var multiStockList: ArrayList<MultiStockAdd> = ArrayList()
    private lateinit var  viewUtils: ViewUtils
    lateinit var toolbar: Toolbar
    var itemList: ArrayList<ItemsModel> = ArrayList()
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
    var shopId: Int = 0
    lateinit var edtCategory: EditText
    private lateinit var createStock: Button
    private lateinit var removeStock: Button
    private lateinit var lytAddItem: LinearLayout
    lateinit var tblContact: TableLayout
    var searchList: ArrayList<String> = ArrayList()
    private lateinit var  type: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View= inflater.inflate(R.layout.fragment_addstock_location,container,false)
        type = arguments!!.getString("type")!!
        viewUtils=ViewUtils()
        sessionManager = SessionManager(activity)
        progressDialog = ProgressDialog(activity)
        edtCategory=view.findViewById(R.id.edt_category)
        createStock=view.findViewById(R.id.create_stock)
        removeStock=view.findViewById(R.id.removeStock)
        lytAddItem=view.findViewById(R.id.lyt_add_item)
        tblContact=view.findViewById(R.id. tblContact)


        if(type=="removeStock") {
            createStock.visibility=View.GONE
            removeStock.visibility=View.VISIBLE
        }else if(type=="adStock") {
            createStock.visibility = View.VISIBLE
            removeStock.visibility = View.GONE
        }

        lytAddItem.setOnClickListener {
            multiStockList.add(MultiStockAdd("1", "1"))

            multiItemAdded()
        }
        edtCategory.setOnClickListener {

            getShopName()

        }
        removeStock.setOnClickListener {
            if (edtCategory.text.toString().isEmpty()) {
                edtCategory.error = "Please Enter Category.."
            } else {
                removeStockMethod()

            }
        }
        createStock.setOnClickListener {
            if (edtCategory.text.toString().isEmpty()) {
                edtCategory.error = "Please Enter Category.."
            } else {
                addStockToServer()

            }
        }
        getProductName()
        multiItemAdded()

        return view }

    private fun removeStockMethod() {
        var objects = JsonObject()
        var jsonArray = JsonArray()

        for (row in itemList) {
            for (item in multiStockList) {
                if (row.name.equals(item.location)) {
                    var jsonObject = JsonObject()
                    jsonObject.addProperty("itemId", row.id)
                    jsonObject.addProperty("quantity", Integer.parseInt(item.quantity!!))
                    jsonArray.add(jsonObject)
                }
            }
        }
        objects.addProperty("shopId", shopId)
        objects.add("stock", jsonArray)
        val progressDialog = ProgressDialog(activity)
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

        requestInterface.voidStockByLocation(objects).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + " ")
                if (response.code() == 200) {
                    Toast.makeText(
                        activity,
                        "Stock Added SuccessFully",
                        Toast.LENGTH_LONG
                    ).show()
                    tblContact.removeAllViews()
                    edtCategory.text=null
                    edt_item.text=null
                    edt_sell_Price.text=null
                } else {
                    progressDialog.dismiss()
                    Log.e("response", response.message() + "")
                    Toast.makeText(
                        activity,
                        "Please Check Store name and try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                Toast.makeText(activity, "Please try again later",Toast.LENGTH_LONG).show()
            }
        })


    }

    private fun addStockToServer() {

        var objects = JsonObject()
        var jsonArray = JsonArray()

        for (row in itemList) {
            for (item in multiStockList) {
                if (row.name.equals(item.location)) {
                    var jsonObject = JsonObject()
                    jsonObject.addProperty("itemId", row.id)
                    jsonObject.addProperty("quantity", Integer.parseInt(item.quantity!!))
                    jsonArray.add(jsonObject)
                }
            }
        }
        objects.addProperty("shopId", shopId)
        objects.add("stock", jsonArray)
        val progressDialog = ProgressDialog(activity)
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

        requestInterface.stockByLocation(objects).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + " ")
                if (response.code() == 200) {
                    Toast.makeText(
                        activity,
                        "Stock Added SuccessFully",
                        Toast.LENGTH_LONG
                    ).show()
                    tblContact.removeAllViews()
                    edtCategory.text=null
                    edt_item.text=null
                    edt_sell_Price.text=null
                } else {
                    progressDialog.dismiss()
                    Log.e("response", response.message() + "")
                    Toast.makeText(
                        activity,
                        "Please Check Store name and try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                Toast.makeText(activity, "Please try again later",Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun multiItemAdded() {
        if (multiStockList == null) return
        val inflater = LayoutInflater.from(activity)
        tblContact.removeAllViews()
        for (contact in multiStockList) {
            val row =
                inflater.inflate(R.layout.tb_add_item, null) as TableRow
            val btnDelete =
                row.findViewById<View>(R.id.btnDelete) as ImageView
            val edtContact =
                row.findViewById<View>(R.id.edtFieldValue) as AutoCompleteTextView
            val edtType = row.findViewById<View>(R.id.edtQuantity) as EditText
            val txtLocation = row.findViewById<View>(R.id.txt_field_name) as TextView
            txtLocation.text = "Item"
            val adapter: ArrayAdapter<String>? =
                activity?.let { ArrayAdapter(it, android.R.layout.select_dialog_item, searchList) }
            edtContact.threshold = 1
            edtContact.setAdapter(adapter)

            edtContact.tag = contact
            edtContact.setText(contact.location)
            edtContact.isClickable=false
            edtType.setText(contact.quantity)
            btnDelete.setOnClickListener {
                val multiContact: MultiStockAdd =
                    edtContact.tag as MultiStockAdd
                multiStockList.remove(multiContact)
                multiItemAdded()
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
                        val multiContact: MultiStockAdd =
                            edtContact.tag as MultiStockAdd
                        multiStockList.remove(multiContact)
                        multiContact.location=edtContact.text.toString()
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
                    if (s.isNotEmpty()) {
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
        requestInterface.getShopsList().enqueue(object : Callback<ArrayList<ShopModel>> {
            override fun onResponse(
                call: Call<ArrayList<ShopModel>>,
                response: Response<ArrayList<ShopModel>>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    shopList = response.body()
                    showShopName()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }
            override fun onFailure(call: Call<ArrayList<ShopModel>>, t: Throwable) {
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
    private fun showShopName() {
        finalShopList.clear()
        shopDialog = this.activity?.let { Dialog(it) }!!
        shopDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        shopDialog.setCancelable(true)
        shopDialog.setContentView(R.layout.custom_layout)
        recyclerview = shopDialog.findViewById(R.id.recyclerview)
        search = shopDialog.findViewById(R.id.search)
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                finalShopList.clear()
                for (item in shopList) {
                    if (item.name?.toLowerCase()?.contains(charSequence.toString())!!) {
                        finalShopList.add(item)
                    }
                }
                storeAdapter = activity?.let { StoreAdapter(finalShopList, it) }!!
                recyclerview.adapter = storeAdapter
            }
            override fun afterTextChanged(editable: Editable) {}
        })
        finalShopList.addAll(shopList)
        storeAdapter = activity?.let { StoreAdapter(finalShopList, it) }!!
        recyclerview.adapter = storeAdapter
        recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(activity!!, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    if (shopDialog.isShowing){
                        shopDialog.dismiss()
                    }
                    edtCategory.setText(finalShopList[position].name.toString())
                    shopId=Integer.parseInt(finalShopList[position].id.toString())
                }
            })
        )
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)  //set height to 90% of total
        val width = (metrics.widthPixels * 0.9) //set width to 90% of total
        shopDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        shopDialog.show()
    }
    private fun getProductName() {
        itemList.clear()
        newList.clear()
        var userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN)
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
                if (response.code() == 200) {
                    itemList = response.body()
                    for(items in itemList){
                        searchList.add(items.name!!)
                    }
                } else {
                    Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }
            override fun onFailure(call: Call<ArrayList<ItemsModel>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(
                    activity,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
