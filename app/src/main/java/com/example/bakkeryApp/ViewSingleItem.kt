package com.example.bakkeryApp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakkeryApp.adapter.PriceHistoryAdapter
import com.example.bakkeryApp.model.ItemHistoryModel
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiManager.Companion.BASE_URL
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import kotlinx.android.synthetic.main.activity_view_single_item.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import kotlin.math.roundToInt


class ViewSingleItem : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var sessionManager: SessionManager
    lateinit var mcontext: Context
    private lateinit var priceHistDialog: Dialog
    lateinit var recyclerview: RecyclerView
    lateinit var search: EditText
    lateinit var adapter: PriceHistoryAdapter
    var itemHistoryList: ArrayList<ItemHistoryModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var id = intent.getIntExtra("id", 0)
        setContentView(R.layout.activity_view_single_item)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mcontext= this!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.title ="View Items"

        sessionManager= SessionManager(this)

        edit_query.setOnClickListener {
        }

        priceHistory.setOnClickListener {
            getPriceHistory(id);
            //showItemHistDialog();
        }

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

        requestInterface.getItem(id).enqueue(object : Callback<ItemsModel> {
            override fun onResponse(
                call: Call<ItemsModel>,
                response: Response<ItemsModel>
            ) {
                Log.e("response", response.code().toString() + "  rss")
                if (response.code() == 200) {

                    var itemsModel: ItemsModel = ItemsModel();
                    itemsModel = response.body();

                    val encodedURL: String = URLEncoder.encode(itemsModel.imageFileName,"UTF-8").replace("+", "%20")
                    var uri= BASE_URL+"downloadfile/item/"+encodedURL

                    Glide.with(mcontext as ViewSingleItem).load( uri).into(product_ImageView);

                    edt_category.setText(itemsModel.itemCategory);
                    edt_name.setText(itemsModel.name)
                    edt_hsnCode.setText(itemsModel.hsnCode)
                    edt_sku.setText(itemsModel.sku)
                    edt_unitOfMeasure.setText(itemsModel.unitOfMeasure)
                    edt_taxIncluded.isChecked = itemsModel.taxIncluded!!
                } else {
                }
            }
            override fun onFailure(call: Call<ItemsModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun showItemHistDialog() {
        priceHistDialog = Dialog(this)
        priceHistDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        priceHistDialog.setCancelable(true)
        priceHistDialog.setContentView(R.layout.price_history)
        recyclerview = priceHistDialog.findViewById(R.id.price_history_recyclerview)
        search = priceHistDialog.findViewById(R.id.search)
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        adapter = PriceHistoryAdapter(itemHistoryList, this)
        recyclerview.adapter = adapter


        val metrics = DisplayMetrics()

        this!!.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)

        val width = (metrics.widthPixels * 0.9)

        priceHistDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        priceHistDialog.show()

    }

    fun priceHistorySetUp(itemHistoryModel: ItemHistoryModel) {
        if (priceHistDialog.isShowing){
            priceHistDialog.dismiss()
            edt_category.setText(itemHistoryModel.name)
        }
    }

    fun getPriceHistory(id: Int) {
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

        requestInterface.getItemPriceHistory(id).enqueue(object : Callback<ArrayList<ItemHistoryModel>> {
            override fun onResponse(
                call: Call<ArrayList<ItemHistoryModel>>,
                response: Response<ArrayList<ItemHistoryModel>>
            ) {
                Log.e("response", response.code().toString() + "  rss")
                if (response.code() == 200) {

                   itemHistoryList = response.body();

                    showItemHistDialog();

                } else {
                }
            }
            override fun onFailure(call: Call<ArrayList<ItemHistoryModel>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}