package com.cbe.bakery.fragments
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
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
import com.cbe.bakery.MovementStockSummary
import com.cbe.bakery.R
import com.cbe.bakery.SummaryViewActivity
import com.cbe.bakery.adapter.CustomListAdapter
import com.cbe.bakery.adapter.StoreAdapter
import com.cbe.bakery.adapter.SummaryAdapter
import com.cbe.bakery.model.ItemsModel
import com.cbe.bakery.model.ShopModel
import com.cbe.bakery.model.SummaryModel
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class StockMovementSummary : Fragment(){
    lateinit var edtfromDate:EditText
    lateinit var edttoDate:EditText
    private lateinit var transactionSpinner:Spinner
    lateinit var edtItem:EditText
    lateinit var edtlocation:EditText
    lateinit var edtReason:Spinner
    lateinit var show:Button
    var itemList: ArrayList<ItemsModel> = ArrayList()
    var finalShopList: ArrayList<ShopModel> = ArrayList()
    var shopId: Long = 0L
    var itemId: Long = 0L
    lateinit var search: EditText
    lateinit var shopDialog: Dialog
    private lateinit var productDialog: Dialog
    lateinit var listView: ListView
    lateinit var myListAdapter: CustomListAdapter
    lateinit var summaryAdapter: SummaryAdapter
    var newList: ArrayList<ItemsModel> = ArrayList()
    lateinit var storeAdapter: StoreAdapter
    lateinit var recyclerview: RecyclerView
    var shopList: ArrayList<ShopModel> = ArrayList()
    var searchList: ArrayList<String> = ArrayList()
    lateinit var summaryRecycler:RecyclerView
    lateinit var progressDialog: ProgressDialog
    private lateinit var  viewUtils: ViewUtils
    lateinit var sessionManager: SessionManager
    var shopMap: HashMap<String, ShopModel> = HashMap<String, ShopModel> ()

    var fmonth: String? = null
    var fDate: String? = null
    var month = 0
    var paddedMonth = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.move_stock_summary, container, false)
        edtfromDate=view.findViewById(R.id.edtfromDate);
        edttoDate=view.findViewById(R.id.edttoDate);
        transactionSpinner=view.findViewById(R.id.spinner);
        edtItem=view.findViewById(R.id.edtItem);
        edtReason=view.findViewById(R.id.edtReason);
        edtlocation=view.findViewById(R.id.edtlocation);
        show=view.findViewById(R.id.show)
        summaryRecycler=view.findViewById(R.id.summaryRecycler)
        progressDialog = ProgressDialog(activity)
        viewUtils= ViewUtils()
        sessionManager =
            SessionManager(activity)

        setFromandTodate()
        edtfromDate.isClickable=false
        edtfromDate.isFocusable=false
        edtfromDate.isCursorVisible=false
        edttoDate.isClickable=false
        edttoDate.isFocusable=false
        edtItem.isFocusable=false
        edtItem.isCursorVisible=false
        edtlocation.isFocusable=false
        edtlocation.isCursorVisible=false
        edttoDate.isCursorVisible=false

        edtfromDate.setOnClickListener {
            pickDateTime(edtfromDate)
        }
        edttoDate.setOnClickListener {
            pickDateTime(edttoDate)
        }
        show.setOnClickListener {
            if (edtfromDate.text.toString()==null && edtfromDate.text.toString()==""){
                edtfromDate.error="Please Select From date"
            }else if(edttoDate.text.toString()==null && edttoDate.text.toString()==""){
                edttoDate.error="Please Select To date"
            }else if(itemId==0L){
                edtItem.error="Please Select Item"
            }else if(shopId==0L){
                edtlocation.error="Please Select Shop"
            }else{
                var intent= Intent(activity, MovementStockSummary::class.java)
                intent.putExtra("shopId",shopId)
                intent.putExtra("itemId",itemId)
                intent.putExtra("fromDate",edtfromDate.text.toString())
                intent.putExtra("toDate",edttoDate.text.toString())
                intent.putExtra("transaction",transactionSpinner.selectedItem.toString())
                startActivity(intent)
            }

//            showSummary()
        }
        edtItem.setOnClickListener {
            getItems()
        }
        edtlocation.setOnClickListener {
            getShopName()
        }
   return view }

    private fun setFromandTodate() {
        val c = Calendar.getInstance()
        val  mYear = c[Calendar.YEAR]
        val  mMonth = c[Calendar.MONTH]
        val   mDay = c[Calendar.DAY_OF_MONTH]

        try {
            if (mMonth < 10 && mDay < 10) {
                fmonth = "0$mMonth"
                month = fmonth!!.toInt() + 1
                fDate = "0$mDay"
                val paddedMonth = String.format("%02d", month)
                edtfromDate.setText("$mYear-$paddedMonth-$fDate")
                edttoDate.setText("$mYear-$paddedMonth-$fDate")
                Log.e("date selected 1",edttoDate.text.toString()+" ");
                Log.e("date edtfromDate 1",edtfromDate.text.toString()+" ");
            }else if(mMonth >= 10 && mDay <= 9){
                fmonth = "$mMonth"
                month = fmonth!!.toInt() + 1
                fDate = "0$mDay"
                val paddedMonth = String.format("%02d", month)
                edtfromDate.setText("$mYear-$paddedMonth-$fDate")
                edttoDate.setText("$mYear-$paddedMonth-$fDate")
                Log.e("date selected 2",edttoDate.text.toString()+" ");
                Log.e("date edtfromDate 2",edtfromDate.text.toString()+" ");
            } else
            {
                fmonth = "0$mMonth"
                month = fmonth!!.toInt() + 1
                val paddedMonth = String.format("%02d", month)
                edtfromDate.setText("$mYear-$paddedMonth-$mDay")
                edttoDate.setText("$mYear-$paddedMonth-$mDay")
                Log.e("date selected 3",edttoDate.text.toString()+" ");
                Log.e("date edtfromDate 3",edtfromDate.text.toString()+" ");
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


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
                    searchList.clear()
                    shopList = response.body()
                    for (items in shopList) {
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
    private fun pickDateTime(dateSelect: EditText) {
        val c = Calendar.getInstance()
        val  mYear = c[Calendar.YEAR]
        val  mMonth = c[Calendar.MONTH]
        val   mDay = c[Calendar.DAY_OF_MONTH]


        val datePickerDialog = DatePickerDialog(
            context!!,
            object : OnDateSetListener {
                var fmonth: String? = null
                var fDate: String? = null
                var month = 0
                override fun onDateSet(
                    view: DatePicker?, year: Int,
                    monthOfYear: Int, dayOfMonth: Int
                ) {
                    try {
                        if (monthOfYear < 10 && dayOfMonth < 10) {
                            fmonth = "0$monthOfYear"
                            month = fmonth!!.toInt() + 1
                            fDate = "0$dayOfMonth"
//                            fDate = "$dayOfMonth"
                            val paddedMonth = String.format("%02d", month)
//                            dateSelect.setText("$fDate/$paddedMonth/$year")
                            dateSelect.setText("$year-$paddedMonth-$fDate")
                            Log.e("date selected 1",dateSelect.text.toString()+" ");
                        } else {
                            fmonth = "0$monthOfYear"
//                            fmonth = "$monthOfYear"
                            month = fmonth!!.toInt() + 1
                            val paddedMonth = String.format("%02d", month)
//                            dateSelect.setText("$dayOfMonth/$paddedMonth/$year")
                            dateSelect.setText("$year-$paddedMonth-$dayOfMonth")
                            Log.e("date selected 2",dateSelect.text.toString()+" ");
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }, mYear, mMonth, mDay
        )
        val oneDay = 90*24 * 60 * 60 * 1000L

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.datePicker.minDate = (System.currentTimeMillis() -  oneDay)
        datePickerDialog.show()

    }
}