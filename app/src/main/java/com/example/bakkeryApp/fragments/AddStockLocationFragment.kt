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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.R
import com.example.bakkeryApp.adapter.CustomListAdapter
import com.example.bakkeryApp.adapter.Store_Adapter
import com.example.bakkeryApp.fragments.AddStockItemFragment
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
import kotlinx.android.synthetic.main.tb_add_item.*
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
    var MultiStockList: ArrayList<MultiStockAdd> = ArrayList()
    lateinit var  viewUtils: ViewUtils
    lateinit var toolbar: Toolbar
    var Itemlist: ArrayList<ItemsModel> = ArrayList()
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    private lateinit var Product_dialog: Dialog
    lateinit var listView: ListView
    lateinit var search: EditText
    lateinit var myListAdapter: CustomListAdapter
    var newList: ArrayList<ItemsModel> = ArrayList()
    lateinit var Shop_dialog: Dialog
    lateinit var Store_adapter: Store_Adapter
    lateinit var recyclerview: RecyclerView
    var Shoplist: ArrayList<ShopModel> = ArrayList()
    var finalShopList: ArrayList<ShopModel> = ArrayList()
    //    lateinit var stritemId: String

    var shopId: Int = 0
    lateinit var edt_category: EditText
    lateinit var create_item: Button
    lateinit var lyt_add_item: LinearLayout
    lateinit var tblContact: TableLayout
    var searchList: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View= inflater.inflate(R.layout.fragment_addstock_location,container,false)
        viewUtils=ViewUtils()
        sessionManager = SessionManager(activity)
        progressDialog = ProgressDialog(activity)
        edt_category=view.findViewById(R.id.edt_category)
        create_item=view.findViewById(R.id.create_item)
        lyt_add_item=view.findViewById(R.id.lyt_add_item)
        tblContact=view.findViewById(R.id. tblContact)

        lyt_add_item.setOnClickListener {
            MultiStockList.add(MultiStockAdd("1", "1"))

            MultiItemAdded()
        }
        edt_category.setOnClickListener {

//            ShowItemDialog()
            getShopName()

        }
        create_item.setOnClickListener {
            if (edt_category.text.toString().isEmpty()) {
                edt_category.error = "Please Enter Category.."
            } else {
                AddStockToServer()

            }
        }
        getProductName()
        MultiItemAdded()

        return view }
    private fun AddStockToServer() {

        var objects = JsonObject()
        var jsonArray = JsonArray()

        for (row in Itemlist) {
            Log.e("row.name", "${row.name} ")
            Log.e("row.location", "${row.name} ")
            for (item in MultiStockList) {

                Log.e("item.location", "${item.location}  ")
                if (row.name.equals(item.location)) {
                    var jsonObject = JsonObject()
                    jsonObject.addProperty("itemId", row.id)
                    jsonObject.addProperty("quantity", Integer.parseInt(item.quantity!!))
                    jsonArray.add(jsonObject)
                    Log.e("jsonArray", "$jsonArray  ")
                }
            }
        }
        objects.addProperty("shopId", shopId)
        objects.add("stock", jsonArray)

        Log.e("objects", "$objects  ")
        val progressDialog = ProgressDialog(activity)
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

        requestInterface.StockByLocation(objects).enqueue(object : Callback<ResponseBody> {
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
                    edt_category.text=null
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

    private fun MultiItemAdded() {
        if (MultiStockList == null) return
        val inflater = LayoutInflater.from(activity)
        tblContact.removeAllViews()
        for (contact in MultiStockList) {
            val row =
                inflater.inflate(R.layout.tb_add_item, null) as TableRow
            val btnDelete =
                row.findViewById<View>(R.id.btnDelete) as ImageView
            val edtContact =
                row.findViewById<View>(R.id.edtLocation) as AutoCompleteTextView
            val edtType = row.findViewById<View>(R.id.edtQuantity) as EditText
            val txt_location = row.findViewById<View>(R.id.txt_location) as TextView
            txt_location.setText("Item")
            val adapter: ArrayAdapter<String>? =
                activity?.let { ArrayAdapter<String>(it, android.R.layout.select_dialog_item, searchList) }
            edtContact.threshold = 1;
            edtContact.setAdapter(adapter);

            edtContact.tag = contact
            edtContact.setText(contact.location)
            edtContact.isClickable=false
            edtType.setText(contact.quantity)
            btnDelete.setOnClickListener {
                val multiContact: MultiStockAdd =
                    edtContact.tag as MultiStockAdd
                MultiStockList.remove(multiContact)
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
                        val multiContact: MultiStockAdd =
                            edtContact.tag as MultiStockAdd
                        MultiStockList.remove(multiContact)
                        multiContact.location=edtContact.text.toString()
                        MultiStockList.add(multiContact)
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
                        MultiStockList.remove(multiContact)
                        multiContact.quantity=edtType.text.toString()
                        MultiStockList.add(multiContact)
                    }
                }
            })
            tblContact.addView(row)
        }
    }
    private fun getShopName() {
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
        requestInterface.GetShops().enqueue(object : Callback<ArrayList<ShopModel>> {
            override fun onResponse(
                call: Call<ArrayList<ShopModel>>,
                response: Response<ArrayList<ShopModel>>
            ) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + " error")
                if (response.code() == 200) {
//                    progressDialog.dismiss()
                    Shoplist = response.body()
//                    for(items in Shoplist){
//                        searchList.add(items.name!!)
//                    }
                    ShowShopName()
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
    private fun ShowShopName() {
        finalShopList.clear()
        Shop_dialog = this.activity?.let { Dialog(it) }!!
        Shop_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        Shop_dialog.setCancelable(true)
        Shop_dialog.setContentView(R.layout.custom_layout)
        recyclerview = Shop_dialog.findViewById(R.id.recyclerview)
        search = Shop_dialog.findViewById(R.id.search)
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                finalShopList.clear()
                for (item in Shoplist) {
                    if (item.name?.toLowerCase()?.contains(charSequence.toString())!!) {
                        finalShopList.add(item)
                    }
                }
                Store_adapter = activity?.let { Store_Adapter(finalShopList, it) }!!
                recyclerview.adapter = Store_adapter
            }
            override fun afterTextChanged(editable: Editable) {}
        })
        finalShopList.addAll(Shoplist)
        Store_adapter = activity?.let { Store_Adapter(finalShopList, it) }!!
        recyclerview.adapter = Store_adapter
        recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(activity!!, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    if (Shop_dialog.isShowing){
                        Shop_dialog.dismiss()
                    }
                    edt_category.setText(finalShopList[position].name.toString())
                    shopId=Integer.parseInt(finalShopList[position].id.toString())
                }
            })
        )
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)  //set height to 90% of total
        val width = (metrics.widthPixels * 0.9) //set width to 90% of total
        Shop_dialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        Shop_dialog.show()
    }
    private fun getProductName() {
//        progressDialog.setMessage("Loading...")
//        progressDialog.show()
        Itemlist.clear()
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
        requestInterface.GetItems().enqueue(object : Callback<ArrayList<ItemsModel>> {
            override fun onResponse(
                call: Call<ArrayList<ItemsModel>>,
                response: Response<ArrayList<ItemsModel>>
            ) {
//                progressDialog.dismiss()
                Log.e("response", response.code().toString() + "  rss")
                if (response.code() == 200) {
                    Itemlist = response.body()
                    for(items in Itemlist){
                        searchList.add(items.name!!)
                    }
                } else {
//                    progressDialog.dismiss()
                    Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }
            override fun onFailure(call: Call<ArrayList<ItemsModel>>, t: Throwable) {
                t.printStackTrace()
//                progressDialog.dismiss()
                Toast.makeText(
                    activity,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun ShowItemDialog() {
        Product_dialog = activity?.let { Dialog(it) }!!
        Product_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        Product_dialog.setCancelable(true)
        Product_dialog.setContentView(R.layout.alert_product)
        listView = Product_dialog.findViewById(R.id.list_item)
        search = Product_dialog.findViewById(R.id.search)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                myListAdapter.filter?.filter(charSequence.toString())
                newList.clear()
                for (item in Itemlist) {
                    if (item.name?.toLowerCase()?.contains(charSequence.toString())!!) {
                        newList.add(item)
                    }
                }
                myListAdapter = activity?.let { CustomListAdapter(it,newList) }!!
                listView.adapter = myListAdapter
            }

            override fun afterTextChanged(editable: Editable) {}
        })

//        if(newList==null && newList.size==0) {
        newList.addAll(Itemlist)
//               myListAdapter = CustomListAdapter(this, Itemlist)
//               listView.adapter = myListAdapter
//        }  else {
        myListAdapter = activity?.let { CustomListAdapter(it, newList) }!!
        listView.adapter = myListAdapter
//        }

        listView.setOnItemClickListener { adapterView, view, position: Int, id: Long ->
            if (Product_dialog.isShowing){
                Product_dialog.dismiss()
                edt_category.setText(newList[position].name)
//                itemId= newList[position].id!!
            }
        }
        val metrics = DisplayMetrics()

        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)

        val width = (metrics.widthPixels * 0.9)

        Product_dialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        Product_dialog.show()

    }


}
