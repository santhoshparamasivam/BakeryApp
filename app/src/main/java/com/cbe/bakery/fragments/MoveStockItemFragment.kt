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
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.R
import com.cbe.bakery.adapter.CustomListAdapter
import com.cbe.bakery.adapter.StoreAdapter
import com.cbe.bakery.model.ItemsModel
import com.cbe.bakery.model.MultiStockAdd
import com.cbe.bakery.model.ShopModel
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.AsyncTaskExample
import com.cbe.bakery.utils.AsyncTaskPrice
import com.cbe.bakery.utils.RecyclerItemClickListener
import com.cbe.bakery.utils.ViewUtils
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
import kotlin.math.roundToInt

class MoveStockItemFragment : Fragment(){

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
    var shopMap: HashMap<String, ShopModel> = HashMap<String, ShopModel> ()
    var finalShopList: ArrayList<ShopModel> = ArrayList()
    var itemId: Long = 0L
    lateinit var edtCategory: EditText
    lateinit var edtLocation: EditText
    lateinit var txt_qty: EditText
    lateinit var edt_reason: EditText
    private lateinit var createStock: Button
//    private lateinit var removeStock: Button
    var shopId: Long = 0L
    private lateinit var lytAddItem: LinearLayout
    lateinit var tblContact: TableLayout
    lateinit var  type: String
    var searchList: ArrayList<String> = ArrayList()
    private lateinit var availQtyLayout: LinearLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View= inflater.inflate(R.layout.fragment_move_stock_item,container,false)
        viewUtils= ViewUtils()
        sessionManager =
            SessionManager(activity)
        progressDialog = ProgressDialog(activity)
        edtCategory=view.findViewById(R.id.edt_category)
        createStock=view.findViewById(R.id.create_stock)
        lytAddItem=view.findViewById(R.id.lyt_add_item)
        tblContact=view.findViewById(R.id. tblContact)
        edtLocation=view.findViewById(R.id.edt_location)
        txt_qty=view.findViewById(R.id.txt_qty)
        edt_reason=view.findViewById(R.id.edt_reason)


        lytAddItem.setOnClickListener {
            if (edtLocation.text.isNotEmpty()) {
                multiStockList.add(MultiStockAdd("1", "1"))
                multiItemAdded()
            }else{
            Toast.makeText(
                activity,
                "Please select Shop Name first",
                Toast.LENGTH_LONG
            ).show()
        }
        }
        edtCategory.setOnClickListener {
            if (edtLocation.text.isNotEmpty()) {
                getProductName()
            }else
                Toast.makeText(
                    activity,
                    "Please select Shop Name first",
                    Toast.LENGTH_LONG
                ).show()

        }

        edtLocation.setOnClickListener {
            getShopName()
        }

        createStock.setOnClickListener {
            if(edtCategory.text.toString().isEmpty()){
                edtCategory.error="Please Enter Category.."
            }else {
//                addStockToServer()
                addStockToServer()
            }
        }

        multiItemAdded()

        return view
    }
    private fun getShopName() {
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
                        edtLocation.setText(finalShopList[position].name.toString())
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
    private fun removeStockMethod() {
        var objects= JsonObject()
        var jsonArray= JsonArray()

        for(item in multiStockList) {
            var sm = shopMap.get(item.location)

            var jsonObject= JsonObject()
            jsonObject.addProperty("shopId", sm?.id)
            jsonObject.addProperty("quantity",Integer.parseInt(item.quantity!!))
            jsonArray.add(jsonObject)
        }

        /*for (row in shopList) {
            for(item in multiStockList) {
                if (row.name.equals(item.location)) {
                    var jsonObject=JsonObject()
                    jsonObject.addProperty("shopId",Integer.parseInt(row.id!!))
                    jsonObject.addProperty("quantity",Integer.parseInt(item.quantity!!))
                    jsonArray.add(jsonObject)
                }
            }
        }*/
        objects.addProperty("itemId",itemId)
        objects.add("stock",jsonArray)

        Log.e("objects", "$objects  ")
        val progressDialog = ProgressDialog(activity)
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

        requestInterface.voidStockByItem(objects).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
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
                Toast.makeText(
                    activity,
                    "Please try again later",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }

    private fun addStockToServer() {

        var objects= JsonObject()
        var jsonArray= JsonArray()
        if (multiStockList!=null && multiStockList.size>0)
        for(item in multiStockList) {
            if (item.quantity!="1") {
            var sm = shopMap.get(item.location)
                var jsonObject = JsonObject()

                jsonObject.addProperty("toShopId", sm?.id)
                jsonObject.addProperty("quantity", Integer.parseInt(item.quantity!!))
                jsonArray.add(jsonObject)
            }
        }
        objects.addProperty("fromShopId",shopId)
        objects.addProperty("itemId",itemId)
        objects.add("stock",jsonArray)

        Log.e("objects", "$objects  ")
        val progressDialog = ProgressDialog(activity)
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
        Log.e("before fragment ",requestInterface.moveStockByItem(objects).request().url().toString()+" ")
        requestInterface.moveStockByItem(objects).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + " ")
                if (response.code() == 200) {
                    Toast.makeText(
                        activity,
                        "Move Stock SuccessFully",
                        Toast.LENGTH_LONG
                    ).show()
                    tblContact.removeAllViews()
                    edtCategory.text=null
                    edtLocation.text=null
                    edt_reason.text=null
//                    edt_item.text=null
//                    edt_sell_Price.text=null
                } else {
                    progressDialog.dismiss()
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
                inflater.inflate(R.layout.tbl_move_stock, null) as TableRow
            val btnDelete =
                row.findViewById<View>(R.id.btnDelete) as ImageView
            val edtContact =
                row.findViewById<View>(R.id.edtFieldValue) as AutoCompleteTextView
            val edtType = row.findViewById<View>(R.id.edtQuantity) as EditText
            val edtmrp = row.findViewById<View>(R.id.mrp) as EditText
            val edtAvailableQuantity = row.findViewById<View>(R.id.edtAvailableQuantity) as EditText

            val adapter: ArrayAdapter<String>? =
                activity?.let { ArrayAdapter(it, android.R.layout.select_dialog_item, searchList) }
            edtContact.threshold = 2
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
                        if (edtLocation.text.toString() == edtContact.text.toString()) {
                            edtContact.error = "Please select Different location..."
                        } else {
                            val multiContact: MultiStockAdd =
                                edtContact.tag as MultiStockAdd
                            multiStockList.remove(multiContact)
                            multiContact.location = edtContact.text.toString()
                            multiStockList.add(multiContact)
                            if (s.length > 5) {
                                Log.e("shopid", shopMap.size.toString() + "  ")
                                var shopId = shopMap.get(edtContact.text.toString())?.id
                                Log.e("shopid", shopId?.toString() + "  ")
                                if (shopId != null && shopId > 0) {
                                    var task = AsyncTaskPrice(
                                        activity,
                                        shopId,
                                        itemId,
                                        edtAvailableQuantity,
                                        edtmrp
                                    ).execute().get()
//                               viewUtils.priceloadMethod(activity,shopId,itemId,edtAvailableQuantity,edtmrp)
                                }
                            }
                        }
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
                        val first=Integer.parseInt(txt_qty.text.toString())
                        val second=Integer.parseInt(edtType.text.toString())
                            Log.e("first value",first.toString()+" "+second.toString())
                        if(second<= first){
                            val multiContact: MultiStockAdd =
                                edtContact.tag as MultiStockAdd
                            multiStockList.remove(multiContact)
                            multiContact.quantity=edtType.text.toString()
                            multiStockList.add(multiContact)
                            createStock.visibility==View.VISIBLE;
                        }else {
                            edtType.error = "Please enter valid count.."
                            createStock.visibility == View.GONE;
                        }
                    }

                }
            })
            tblContact.addView(row)
        }
    }
    private fun getSearchShopName() {
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
                if (response.code() == 200) {
                    shopList = response.body()
                    for(items in shopList){
                        searchList.add(items.name!!)
                        shopMap[items.name!!] = items
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<ShopModel>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(
                    activity,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getProductName() {
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
            edtCategory.setText(newList[position].name)
            itemId= newList[position].id!!
            if (edtCategory.text.isNotEmpty()){
                if (edtLocation.text.isNotEmpty()){
                    if (shopId==0L){
                        Toast.makeText(activity,"Please Select Shop name ",Toast.LENGTH_SHORT).show()
                    }else if(itemId==0L){
                        Toast.makeText(activity,"Please Select Item ",Toast.LENGTH_SHORT).show()
                    }else
                        AsyncTaskExample(activity, shopId, itemId, txt_qty).execute().get()
                }

            }


        }
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)
        val width = (metrics.widthPixels * 0.9)
        productDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        productDialog.show()
    }
}
