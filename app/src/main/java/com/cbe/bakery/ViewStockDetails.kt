package com.cbe.bakery

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cbe.bakery.model.payload.StockByItemDtlResponse
import com.cbe.bakery.model.payload.StockByItemResponse
import com.cbe.bakery.model.payload.StockByLocDtlResponse
import com.cbe.bakery.model.payload.StockByLocationResponse
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.ViewUtils
import com.cbe.bakery.model.PinVerificationModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_view_stock_details.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewStockDetails : AppCompatActivity() {
    var itemId:Int = 0
    var stockBy:String = ""
    var transactionId:String = ""
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    var shopId: Int = 0
    lateinit var toolbar: Toolbar
    lateinit var txtItem:TextView
    lateinit var edtCategory:EditText
    lateinit var edtTransaction:EditText
    private lateinit var viewUtils: ViewUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stock_details)
        val intent=intent
        itemId= intent?.getIntExtra("ItemId", 0)!!
        transactionId= intent.getStringExtra("TransactionId")!!
        stockBy= intent.getStringExtra("stockBy")!!
        progressDialog = ProgressDialog(this)
        sessionManager= SessionManager(this)
        toolbar = findViewById(R.id.toolbar)
        viewUtils = ViewUtils()
        txtItem=findViewById(R.id.txt_item)
        edtCategory=findViewById(R.id.edt_category)
        edtTransaction=findViewById(R.id.edtTransaction)
        edtTransaction.setText(transactionId)
        setSupportActionBar(toolbar)
            viewDetailsMethod()
        toolbar.setNavigationOnClickListener {
            finish()
        }
        btn_void.setOnClickListener {
            verificationMethod()

        }
    }
    private fun verificationMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        var userId = sessionManager.getStringKey(SessionKeys.USER_ID).toString()
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
        requestInterface.getPin(userId).enqueue(object : Callback<PinVerificationModel> {
            override fun onResponse(
                call: Call<PinVerificationModel>,
                response: Response<PinVerificationModel>
            ) {
                progressDialog.dismiss()
                Log.e("pin verify",response.code().toString()+"  ");
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    Log.e("pin verify",response.body().pin+"  ");
                    response.body().pin?.let { pinVerificationAlert(it) }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@ViewStockDetails,
                        "Please try again late",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<PinVerificationModel>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    this@ViewStockDetails,
                    "Connection Failed,Please try again late",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun pinVerificationAlert(pin: String) {

        var alertDialog = Dialog(this)
        var window: Window? = alertDialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_pinconfirm)
        alertDialog.setCancelable(true)
        alertDialog.setCanceledOnTouchOutside(true)
        var edt_one = alertDialog.findViewById<EditText>(R.id.edt_one)
        var edt_two = alertDialog.findViewById<EditText>(R.id.edt_two)
        var edt_three = alertDialog.findViewById<EditText>(R.id.edt_three)
        var edt_four = alertDialog.findViewById<EditText>(R.id.edt_four)
        var btn_neg = alertDialog.findViewById<TextView>(R.id.btn_neg)
        var btn_pos = alertDialog.findViewById<TextView>(R.id.btn_pos)
        edt_one.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_one.length() == 1) {
                    edt_two.requestFocus()
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {

            }
        })
        edt_two.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_two.length() == 1) {
                    edt_three.requestFocus()
                }else if (edt_two.length()==0){
                    edt_one.requestFocus()
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {

            }
        })
        edt_three.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_three.length() == 1) {
                    edt_four.requestFocus()
                }else if (edt_three.length()==0){
                    edt_two.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {

            }
        })
        edt_four.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_four.length() == 1) {
                    edt_four.requestFocus()
                }
                else if (edt_four.length()==0){
                    edt_three.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {

            }
        })
        btn_pos.setOnClickListener {
            var enterPin =
                edt_one.text.toString() + edt_two.text.toString() + edt_three.text.toString() + edt_four.text.toString()
            Log.e("enter pin", enterPin + "  ")

//            verificationMethod()
            if (pin.equals(enterPin)) {
                alertDialog.dismiss()
                voidStockMethod()
            }else{
                Toast.makeText(applicationContext, "Please Enter Valid Pin...",Toast.LENGTH_SHORT).show()
            }
        }
        btn_neg.setOnClickListener {

            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    private fun voidStockMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
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
        val call=requestInterface.voidStock(itemId)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewStockDetails,"Stock is successfully updated",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewStockDetails,
                        "Stock is successfully updated",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewStockDetails,"Please try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewStockDetails,
                        "Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    this@ViewStockDetails,
                    "Connection failed,please try again later",
                    Toast.LENGTH_LONG
                ).show()
//                viewUtils.showToast(this@ViewStockDetails,"Connection failed,please try again later",Toast.LENGTH_SHORT)

            }
        })
    }
    private fun viewDetailsMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        Log.e("token",userToken+" ")
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
       val call=requestInterface.getStock(itemId)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    try {
                        if (stockBy == "ByLocation") {
                            val gson = Gson()
                            val stockByLocationResponse = gson.fromJson(response.body().string(), StockByLocationResponse::class.java)
                            txtItem.text = "Location"
                            edtCategory.setText(stockByLocationResponse.shopName)
                            stockByLocation(stockByLocationResponse.stock)
                        } else if(stockBy == "ByItem"){
                            val gson = Gson()
                            val stockByItemResponse = gson.fromJson(response.body().string(), StockByItemResponse::class.java)
                            txtItem.text = "Item"
                            edtCategory.setText(stockByItemResponse.itemName)
                            stockByItem(stockByItemResponse.stock)
                        }
                    } catch (e: Exception ) {
                        e.stackTrace
                    }
                } else {
                    progressDialog.dismiss()

//                    viewUtils.showToast(this@ViewStockDetails,"Please try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewStockDetails,
                        "please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
//                viewUtils.showToast(this@ViewStockDetails,"Connection failed,please try again later",Toast.LENGTH_SHORT)
                Toast.makeText(
                    this@ViewStockDetails,
                    "Connection failed,please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun stockByItem(stockByItemDtlResponseList: List<StockByItemDtlResponse>?) {
        val inflater = LayoutInflater.from(this)
        tblContact.removeAllViews()
        stockByItemDtlResponseList?.forEachIndexed { index, element ->
            setValues(index,"Location", element.shopName, element.quantity)
        }
    }
    private fun stockByLocation(stockByLocDtlResponseList: List<StockByLocDtlResponse>?) {
        val inflater = LayoutInflater.from(this)
        tblContact.removeAllViews()
        stockByLocDtlResponseList?.forEachIndexed { index, element ->
            setValues(index,"Item", element.itemName, element.quantity)
        }
    }
    private fun setValues(tag: Int, label: String, value: String?, quantity: Int?) {
        val inflater = LayoutInflater.from(this)
        val row = inflater.inflate(R.layout.tb_add_item, null) as TableRow
        val btnDelete = row.findViewById<View>(R.id.btnDelete) as ImageView
        val edtFieldValue = row.findViewById<View>(R.id.edtFieldValue) as AutoCompleteTextView
        val edtQuantity = row.findViewById<View>(R.id.edtQuantity) as EditText
        val txtLocation = row.findViewById<View>(R.id.txt_field_name) as TextView
        txtLocation.text = label
        btnDelete.visibility = View.GONE
        edtFieldValue.tag = tag
        edtFieldValue.setText(value)
        edtQuantity.setText(quantity.toString())
        edtFieldValue.isEnabled=false
        edtQuantity.isEnabled=false
        tblContact.addView(row)
    }
}