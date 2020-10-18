package com.cbe.bakery.fragments
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.R
import com.cbe.bakery.adapter.AvailableSummaryAdapter
import com.cbe.bakery.adapter.CustomListAdapter
import com.cbe.bakery.adapter.StoreAdapter
import com.cbe.bakery.model.AvailibitySummary
import com.cbe.bakery.model.ItemsModel
import com.cbe.bakery.model.ShopModel
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.RecyclerItemClickListener
import com.cbe.bakery.utils.ViewUtils
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap
import kotlin.math.roundToInt

class AvailabilityFragment : Fragment(){
    lateinit var edtItem: EditText
    lateinit var edtlocation: EditText
    lateinit var shopTxt: TextView
    lateinit var itemTxt: TextView
    lateinit var dateTxt: TextView
    lateinit var quantityTxt: TextView
    var itemList: ArrayList<ItemsModel> = ArrayList()
    var finalShopList: ArrayList<ShopModel> = ArrayList()
    var shopId: Long = 0L
    var itemId: Long = 0L
    lateinit var show: Button
    lateinit var search: EditText
    lateinit var shopDialog: Dialog
    private lateinit var productDialog: Dialog
    lateinit var listView: ListView
    lateinit var myListAdapter: CustomListAdapter
    lateinit var summaryAdapter: AvailableSummaryAdapter
    var newList: ArrayList<ItemsModel> = ArrayList()
    lateinit var storeAdapter: StoreAdapter
    lateinit var recyclerview: RecyclerView
    var shopList: ArrayList<ShopModel> = ArrayList()
    var searchList: ArrayList<String> = ArrayList()
//    lateinit var summaryRecycler: RecyclerView
    var shopMap: HashMap<String, ShopModel> = HashMap<String, ShopModel> ()
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    private lateinit var  viewUtils: ViewUtils
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View= inflater.inflate(R.layout.availability_summary,container,false)
        edtItem=view.findViewById(R.id.edtItem);
        edtlocation=view.findViewById(R.id.edtlocation);
        show=view.findViewById(R.id.show)
//        summaryRecycler=view.findViewById(R.id.summaryRecycler)
//        summaryRecycler

        shopTxt=view.findViewById(R.id.shopTxt)
        itemTxt=view.findViewById(R.id.itemTxt)
        dateTxt=view.findViewById(R.id.dateTxt)
        quantityTxt=view.findViewById(R.id.quantityTxt)
        progressDialog = ProgressDialog(activity)
        viewUtils= ViewUtils()
        sessionManager =
            SessionManager(activity)
        edtItem.isFocusable=false
        edtItem.isCursorVisible=false
        edtlocation.isFocusable=false
        edtlocation.isCursorVisible=false
        show.setOnClickListener {
            showSummary()
        }
        edtItem.setOnClickListener {
            getItems()
        }
        edtlocation.setOnClickListener {
            getShopName()
        }
   return view
    }
    private fun showSummary() {
        var objects= JsonObject()
        objects.addProperty("shopId",shopId)
        objects.addProperty("itemId",itemId)
        Log.e("objects", "$objects  ")
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
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
        requestInterface.getAvailabilitySummary(objects).enqueue(object : Callback<AvailibitySummary> {
            override fun onResponse(call: Call<AvailibitySummary>, response: Response<AvailibitySummary>) {
                progressDialog.dismiss()
                Log.e("response",response.code().toString()+" ")
                if (response.code() == 200) {
//                    var summaryList: ArrayList<AvailibitySummary> = ArrayList()

                    shopTxt.setText("Shop Name "+ response.body().shopName)
                    itemTxt.setText("Item Name "+ response.body().itemName)
                    dateTxt.setText("Quantity      "+ response.body().quantity)
                    quantityTxt.setText("Date      "+viewUtils.convertLongToTime(response.body().modifiedOn))
//                    summaryList=response.body()
//                    Log.e("response code",response.code().toString()+" "+response.body().size.toString()+" ")
//                    summaryRecycler.layoutManager = LinearLayoutManager(recyclerview.context)
//                    summaryRecycler.setHasFixedSize(true)
//                    summaryAdapter =
//                        AvailableSummaryAdapter(summaryList, activity)
//                    summaryRecycler.adapter =summaryAdapter
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        activity,
                        "Please Check Store name and try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
            override fun onFailure(call: Call<AvailibitySummary>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG).show()
            }
        })

    } private fun getShopName() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
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
                    searchList.clear()
                    shopList = response.body()
                    for(items in shopList){
                        searchList.add(items.name!!)
                        shopMap.put(items.name!!, items)
                    }
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
                storeAdapter = activity?.let {
                    StoreAdapter(
                        finalShopList,
                        it
                    )
                }!!
                recyclerview.adapter = storeAdapter
            }
            override fun afterTextChanged(editable: Editable) {}
        })
        finalShopList.addAll(shopList)
        storeAdapter = activity?.let {
            StoreAdapter(
                finalShopList,
                it
            )
        }!!
        recyclerview.adapter = storeAdapter
        recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity!!,
                object :
                    RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (shopDialog.isShowing) {
                            shopDialog.dismiss()
                        }
                        edtlocation.setText(finalShopList[position].name.toString())
                        shopId = finalShopList[position].id!!
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
    private fun getItems() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
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
                progressDialog.dismiss()
                if (response.code() == 200) {
                    itemList = response.body()
                    showItemDialog()
                }
                else {
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
    private fun showItemDialog() {
        productDialog = activity?.let { Dialog(it) }!!
        productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        productDialog.setCancelable(true)
        productDialog.setContentView(R.layout.alert_product)
        listView = productDialog.findViewById(R.id.list_item)
        search = productDialog.findViewById(R.id.search)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                newList.clear()
                for (item in itemList) {
                    if (item.name?.toLowerCase()?.contains(charSequence.toString())!!) {
                        newList.add(item)
                    }
                }
                myListAdapter = activity?.let {
                    CustomListAdapter(
                        it,
                        newList
                    )
                }!!
                listView.adapter = myListAdapter
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        newList.addAll(itemList)
        myListAdapter = activity?.let {
            CustomListAdapter(
                it,
                newList
            )
        }!!
        listView.adapter = myListAdapter

        listView.setOnItemClickListener { adapterView, view, position: Int, id: Long ->
            if (productDialog.isShowing){
                productDialog.dismiss()
            }
            edtItem.setText(newList[position].name)
            itemId= newList[position].id!!


        }
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)
        val width = (metrics.widthPixels * 0.9)
        productDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        productDialog.show()
    }
}