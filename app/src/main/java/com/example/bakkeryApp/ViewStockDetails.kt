package com.example.bakkeryApp

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.bakkeryApp.model.MultiStockAdd
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import kotlinx.android.synthetic.main.activity_view_stock_details.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class ViewStockDetails : AppCompatActivity() {
    var itemId:Int = 0
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    var shopId: Int = 0
    lateinit var shopName: String
    lateinit var list: List<String>
    var multiStockList: ArrayList<MultiStockAdd> = ArrayList()
    lateinit var toolbar: Toolbar
    lateinit var txt_item:TextView
    lateinit var edt_category:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stock_details)
        var intent=intent
        itemId= intent?.getIntExtra("ItemId", 0)!!
        progressDialog = ProgressDialog(this)
        sessionManager= SessionManager(this)
        toolbar = findViewById(R.id.toolbar)
        txt_item=findViewById(R.id.txt_item)
        edt_category=findViewById(R.id.edt_category)
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
                        val jsonObject = JSONObject(response.body().string())
                        if (!jsonObject.isNull("shopId")) {
                            txt_item.text="Location"
                            var shopId = jsonObject.getString("shopId")
                            if (!jsonObject.isNull("shopName")) {
                                var shopName = jsonObject.getString("shopName")
                                edt_category.setText(shopName)
                            }
                            if (!jsonObject.isNull("stock")) {
                                var stock=jsonObject.getJSONArray("stock")
                                for (i in 0 until stock.length()) {
                                    val item = stock.getJSONObject(i)
                                    var itemName=item.getString("itemName")
                                    var quantity=item.getString("quantity")
                                    var stockAdd=MultiStockAdd();
                                    stockAdd.location=itemName
                                    stockAdd.quantity=quantity
                                    multiStockList.add(stockAdd)
                                }
                            }
                            multiItemAdded("Item")
                        }else if(!jsonObject.isNull("itemId")){
                            var shopId = jsonObject.getString("itemId")
                            if (!jsonObject.isNull("itemName")) {
                                var shopName = jsonObject.getString("itemName")
                                edt_category.setText(shopName)
                            }
                            txt_item.text="Item"
                            if (!jsonObject.isNull("stock")) {
                                var stock=jsonObject.getJSONArray("stock")
                                for (i in 0 until stock.length()) {
                                    val item = stock.getJSONObject(i)
                                    var itemName=item.getString("shopName")
                                    var quantity=item.getString("quantity")
                                    var stockAdd=MultiStockAdd()
                                    stockAdd.location=itemName
                                    stockAdd.quantity=quantity
                                    multiStockList.add(stockAdd)
                                }
                            }
                            multiItemAdded("Location")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
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

    private fun multiItemAdded(type: String) {
        val inflater = LayoutInflater.from(this)
        tblContact.removeAllViews()
        for (contact in multiStockList) {
            val row =
                inflater.inflate(R.layout.tb_add_item, null) as TableRow
            val btnDelete =
                row.findViewById<View>(R.id.btnDelete) as ImageView
            val edtContact =
                row.findViewById<View>(R.id.edtLocation) as AutoCompleteTextView
            val edtType = row.findViewById<View>(R.id.edtQuantity) as EditText
            val txt_location = row.findViewById<View>(R.id.txt_location) as TextView
            txt_location.text = type
            edtContact.tag = contact
            btnDelete.visibility=View.GONE
            edtContact.setText(contact.location)
//            edtContact.isClickable=false
            edtContact.isEnabled=false
            edtType.isEnabled=false
            edtType.setText(contact.quantity)
            tblContact.addView(row)
        }
    }

}