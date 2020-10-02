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
import com.cbe.bakery.model.MoveMultiStockAdd
import com.cbe.bakery.model.MultiStockAdd
import com.cbe.bakery.model.ShopModel
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.AsyncTaskPrice
import com.cbe.bakery.utils.RecyclerItemClickListener
import com.cbe.bakery.utils.ViewUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_move_stock_location.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class MoveStockLocationFragment : Fragment() {
    var multiStockList: ArrayList<MoveMultiStockAdd> = ArrayList()
    private lateinit var viewUtils: ViewUtils
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
    var shopitem: HashMap<String, ItemsModel> = HashMap<String, ItemsModel>()
    var finalShopList: ArrayList<ShopModel> = ArrayList()
//    var itemId: Long = 0L
    lateinit var edt_tolocation: EditText
    lateinit var edtLocation: EditText

    lateinit var edtFromid: EditText
    lateinit var edtToid: EditText
    lateinit var edtReason: EditText
    private lateinit var moveStock: Button
    var shopId: Long = 0L
    private lateinit var lytAddItem: LinearLayout
    lateinit var tblContact: TableLayout
    lateinit var type: String
    var searchList: ArrayList<String> = ArrayList()
    private lateinit var availQtyLayout: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_move_stock_location, container, false)
        activity?.title = "Move Stock"
        viewUtils = ViewUtils()
        sessionManager =
            SessionManager(activity)
        progressDialog = ProgressDialog(activity)
        edt_tolocation = view.findViewById(R.id.edt_tolocation)
        moveStock = view.findViewById(R.id.move_stock)
        lytAddItem = view.findViewById(R.id.lyt_add_item)
        tblContact = view.findViewById(R.id.tblContact)
        edtLocation = view.findViewById(R.id.edt_location)
        edtReason = view.findViewById(R.id.edt_reason)
        edtFromid = view.findViewById(R.id.edt_from_id)
        edtToid = view.findViewById(R.id.edt_to_id)
        lytAddItem.setOnClickListener {
            if (edtLocation.text.isNotEmpty()) {
                multiStockList.add(MoveMultiStockAdd("1", "1","1","1"))
                multiItemAdded()
            }else{
                Toast.makeText(
                    activity,
                    "Please select Shop Name first",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        edtLocation.setOnClickListener {
            getShopName(edtLocation, edtFromid)
        }

        edt_tolocation.setOnClickListener {
            if (edtLocation.text.isNotEmpty()) {
                getShopName(edt_tolocation, edtToid)
            } else
                Toast.makeText(
                    activity,
                    "Please select Shop Name first",
                    Toast.LENGTH_LONG
                ).show()
        }

        moveStock.setOnClickListener {
            if (edtLocation.text.toString().isEmpty()) {
                edtLocation.error = "Please Select From Location.."
            } else if (edt_tolocation.text.toString().isEmpty()) {
                edtLocation.error = "Please Select To Location.."
            }else if(edtLocation.text.toString() == edt_tolocation.text.toString()){
                edtLocation.error = "From and To Location are same..."
            }
            else{
                moveStockLocation()
            }
        }
        getProductName()
        multiItemAdded()

        return view
    }
    private fun getProductName() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        itemList.clear()
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
                    progressDialog.dismiss()
                    itemList = response.body()
                    for (items in itemList) {
                        searchList.add(items.name!!)
                        shopitem[items.name!!] = items
                    }
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
    private fun getShopName(locationTxt: EditText, locationId: EditText) {
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
                    showShopName(locationTxt, locationId)

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

    private fun showShopName(locationTxt: EditText, locationId: EditText) {
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
                        locationTxt.setText(finalShopList[position].name.toString())
                        locationId.setText(finalShopList[position].id.toString())
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

    private fun moveStockLocation() {

        var objects = JsonObject()
        var jsonArray = JsonArray()

        for (item in multiStockList) {
            if (item.quantity != "1") {
                var sm = shopitem.get(item.location)

                var jsonObject = JsonObject()
                jsonObject.addProperty("itemId", sm?.id)
                jsonObject.addProperty("quantity", Integer.parseInt(item.quantity!!))
                jsonArray.add(jsonObject)
            }
        }
        val fromlng = edt_from_id.text.toString ().toLong()
        val tolng = edt_to_id.text.toString().toLong()
        objects.addProperty("fromShopId", fromlng )
        objects.addProperty("toShopId", tolng)
        objects.add("stock", jsonArray)
        Log.e("objects 2", "$objects  ")
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
        Log.e("before fragment ",requestInterface.moveStockByLocation(objects).request().url().toString()+" ")
        requestInterface.moveStockByLocation(objects).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                Log.e("response",response.code().toString()+"  ");
                if (response.code() == 200) {
                    Toast.makeText(
                        activity,
                        "Stock Moved SuccessFully",
                        Toast.LENGTH_LONG
                    ).show()
                    tblContact.removeAllViews()
                    edtLocation.text = null
                    edt_tolocation.text = null
                    edtFromid.text = null
                    edtToid.text = null
                    edtReason.text = null
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
                Log.e("failure",t.message+"  ");
                Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun multiItemAdded() {
        if (multiStockList == null) return
        val inflater = LayoutInflater.from(activity)
        tblContact.removeAllViews()
        for (contact in multiStockList) {
            val row =
                inflater.inflate(R.layout.tbl_move_location, null) as TableRow
            val btnDelete =
                row.findViewById<View>(R.id.btnDelete) as ImageView
            val edtContact =
                row.findViewById<View>(R.id.edtFieldValue) as AutoCompleteTextView
            val edtType = row.findViewById<View>(R.id.edtQuantity) as EditText
            val available_qty = row.findViewById<View>(R.id.available_qty) as EditText
            val mrp = row.findViewById<View>(R.id.mrp) as EditText

            val adapter: ArrayAdapter<String>? =
                activity?.let { ArrayAdapter(it, android.R.layout.select_dialog_item, searchList) }
            edtContact.threshold = 2
            edtContact.setAdapter(adapter)

            edtContact.tag = contact
            edtContact.setText(contact.location)
            edtContact.isClickable = false
            edtType.setText(contact.quantity)
            available_qty.setText(contact.availability)
            mrp.setText(contact.price)

            btnDelete.setOnClickListener {
                val multiContact: MoveMultiStockAdd =
                    edtContact.tag as MoveMultiStockAdd
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
                        val multiContact: MoveMultiStockAdd =
                            edtContact.tag as MoveMultiStockAdd
                        multiStockList.remove(multiContact)
                        multiContact.location = edtContact.text.toString()
                        multiStockList.add(multiContact)

                        if (s.length > 5) {

                            var itemId = shopitem.get(edtContact.text.toString())?.id

                            if(multiStockList.size>1) {

                                for(moveMultiStockAdd in multiStockList) {
                                    if(moveMultiStockAdd.location.equals(edtContact.text.toString())) {
                                        Toast.makeText(
                                            context,
                                            "Item already selected. Please select another item.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        edtContact.setText("")
                                        return
                                    }
                                }
                            }

                            if (itemId != null && itemId > 0) {
                                var task = AsyncTaskPrice(
                                    activity,
                                    shopId,
                                    itemId,
                                    available_qty,
                                    mrp
                                ).execute().get()
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
                        val first=Integer.parseInt(available_qty.text.toString())
                        val second=Integer.parseInt(edtType.text.toString())
                        if(second<= first){
                            val multiContact: MoveMultiStockAdd =
                                edtContact.tag as MoveMultiStockAdd
                            multiStockList.remove(multiContact)
                            multiContact.quantity = edtType.text.toString()
                            multiContact.availability=available_qty.text.toString()
                            multiContact.price=mrp.text.toString()
                            multiStockList.add(multiContact)
                            moveStock.visibility==View.VISIBLE;
                        }else {
                            edtType.error = "Please enter valid count.."
                            moveStock.visibility == View.GONE;
                        }

                    }
                }
            })
            tblContact.addView(row)
        }
    }
}