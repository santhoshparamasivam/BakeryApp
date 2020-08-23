package com.example.bakkeryApp

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.bakkeryApp.model.MultiStockAdd
import com.example.bakkeryApp.model.payload.StockByItemDtlResponse
import com.example.bakkeryApp.model.payload.StockByItemResponse
import com.example.bakkeryApp.model.payload.StockByLocDtlResponse
import com.example.bakkeryApp.model.payload.StockByLocationResponse
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
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
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    var shopId: Int = 0
    lateinit var toolbar: Toolbar
    lateinit var txtItem:TextView
    lateinit var edtCategory:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stock_details)
        var intent=intent
        itemId= intent?.getIntExtra("ItemId", 0)!!
        stockBy= intent?.getStringExtra("stockBy")!!
        progressDialog = ProgressDialog(this)
        sessionManager= SessionManager(this)
        toolbar = findViewById(R.id.toolbar)
        txtItem=findViewById(R.id.txt_item)
        edtCategory=findViewById(R.id.edt_category)
        setSupportActionBar(toolbar)
            viewDetailsMethod()
        toolbar.setNavigationOnClickListener {
            finish()
        }

    }

    private fun viewDetailsMethod() {
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
       var call=requestInterface.getStock(itemId)

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
                            var stockByLocationResponse = gson.fromJson(response.body().string(), StockByLocationResponse::class.java)

                            txtItem.text = "Location"
                            edtCategory.setText(stockByLocationResponse.shopName)
                            stockByLocation(stockByLocationResponse.stock)

                        }else if(stockBy == "ByItem"){

                            val gson = Gson()
                            var stockByItemResponse = gson.fromJson(response.body().string(), StockByItemResponse::class.java)

                            txtItem.text = "Item"
                            edtCategory.setText(stockByItemResponse.itemName)
                            stockByItem(stockByItemResponse.stock)
                        }
                    } catch (e: Exception ) {

                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Connection Failed,Please try again later",
                    Toast.LENGTH_SHORT
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