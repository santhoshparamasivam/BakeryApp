package com.example.bakkeryApp

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.adapter.MyClassAdapter
import com.example.bakkeryApp.adapter.Product_Adapter
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.model.MultiStockAdd
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.example.bakkeryApp.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_add_stock.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class AddStockActivity : AppCompatActivity() {
    var MultiStockList: ArrayList<MultiStockAdd> = ArrayList()
    lateinit var  viewUtils: ViewUtils
    lateinit var toolbar: Toolbar
    var Itemlist: ArrayList<ItemsModel> = ArrayList()
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    private lateinit var Product_dialog: Dialog
    lateinit var listView: ListView
    lateinit var search: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stock)
        viewUtils=ViewUtils()
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title ="Add Stock"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        lyt_add_item.setOnClickListener {
            MultiStockList.add(MultiStockAdd("", ""))
            Log.e("MultiSTock in add",MultiStockList.size .toString())
            MultiItemAdded()
        }
        edt_category.setOnClickListener {
            ShowItemDialog()
        }
//        getProductName()
        MultiItemAdded()
    }

    private fun MultiItemAdded() {

        if (MultiStockList == null) return
        val inflater = LayoutInflater.from(this@AddStockActivity)
        tblContact.removeAllViews()
        for (contact in MultiStockList) {
            val row =
                inflater.inflate(R.layout.tb_add_item, null) as TableRow
            val btnDelete =
                row.findViewById<View>(R.id.btnDelete) as ImageView
            val edtContact =
                row.findViewById<View>(R.id.edtLocation) as EditText
            val edtType = row.findViewById<View>(R.id.edtQuantity) as EditText
            edtType.hint = "Name"
            edtContact.tag = contact
            edtContact.setText(contact.location)
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
                        //                        Collections.sort(MainApplication.multiEmergencyContacts);
                    }
                }
            })

            tblContact.addView(row)
        }
    }
    private fun getProductName() {
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
        requestInterface.GetItems().enqueue(object : Callback<ArrayList<ItemsModel>> {
            override fun onResponse(
                call: Call<ArrayList<ItemsModel>>,
                response: Response<ArrayList<ItemsModel>>
            ) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + "  rss")
                if (response.code() == 200) {

                    Itemlist = response.body()
                    ShowItemDialog()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this@AddStockActivity, "Please try again later", Toast.LENGTH_LONG)
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
        Log.e("dialog","working");
//        for(int s=0;s<5;s++){
//
//        }
        val model = ItemsModel()
        model.name = "Santhosh"
        Itemlist.add(model)
        Product_dialog = Dialog(this)
        Product_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        Product_dialog.setCancelable(true)
        Product_dialog.setContentView(R.layout.alert_product)
        listView = Product_dialog.findViewById(R.id.list_item)
        search = Product_dialog.findViewById(R.id.search)

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {}
        })
        var myListAdapter = MyClassAdapter(Itemlist,this)
        listView.adapter = myListAdapter


        val metrics = DisplayMetrics()

        this!!.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)

        val width = (metrics.widthPixels * 0.9)

        Product_dialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        Product_dialog.show()

    }


}
