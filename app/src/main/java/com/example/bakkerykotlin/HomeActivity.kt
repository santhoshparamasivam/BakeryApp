package com.example.bakkerykotlin

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkerykotlin.Model.ItemsModel
import com.example.bakkerykotlin.Model.ShopModel
import com.example.bakkerykotlin.RetrofitService.ApiManager
import com.example.bakkerykotlin.RetrofitService.ApiService
import com.example.bakkerykotlin.Utils.ViewUtils
import com.example.bakkerykotlin.adapter.Product_Adapter
import com.example.bakkerykotlin.adapter.Store_Adapter
import com.example.bakkerykotlin.sessionManager.SessionKeys
import com.example.bakkerykotlin.sessionManager.SessionManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    var Shoplist: ArrayList<ShopModel> = ArrayList()

    var Itemlist: ArrayList<ItemsModel> = ArrayList()

    lateinit var shopid: String

    lateinit var Itemid: String

    lateinit var Userid: String

    lateinit var sessionManager: SessionManager
    lateinit var adapter: Product_Adapter
    lateinit var Store_adapter: Store_Adapter
    lateinit var recyclerview: RecyclerView
    lateinit var search: EditText
    lateinit var progressDialog: ProgressDialog

    lateinit var Product_dialog: Dialog
    lateinit var viewUtils:ViewUtils
    lateinit var Shop_dialog: Dialog
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewUtils=ViewUtils()
        sessionManager = SessionManager(this)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        progressDialog = ProgressDialog(this@HomeActivity)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        val headerView: View = navView.getHeaderView(0)
        headerView.userName.text = sessionManager.getStringKey(SessionKeys.FIRST_NAME)
        Userid = sessionManager.getStringKey(SessionKeys.USER_ID).toString()
        edt_storename.setOnClickListener {

            GetShopName()
        }

        edt_product.setOnClickListener {
            getProductName()

        }
        edt_date.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this@HomeActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var months = monthOfYear + 1
//                edt_date.setText("$dayOfMonth/$months/$year")

                    if(months==10){
                        months=10
                        edt_date.setText("$year-$months-$dayOfMonth")
                    }else if(months==11){
                        months=11
                        edt_date.setText("$year-$months-$dayOfMonth")
                    }else if(months==12){
                        months=12
                        edt_date.setText("$year-$months-$dayOfMonth")
                    }else{
                        edt_date.setText("$year-0$months-$dayOfMonth")
                    }


                },
                year,
                month,
                day
            )
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.show()
        }
        Submit.setOnClickListener {
            if (edt_product.text.toString().isEmpty()) {
                edt_product.error = "Please Select Item"
            } else if (edt_storename.text.toString().isEmpty()) {
                edt_storename.error = "Please Select Store"
            } else if (edt_quantity.text.toString().isEmpty()) {
                edt_quantity.error = "Please Enter Quantity"
            } else if (edt_date.text.toString().isEmpty()) {
                edt_date.error = "Please select date"
            } else
                SubmitDatatoServer()
        }

    }

    private fun SubmitDatatoServer() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor{ chain ->
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
                val sk=Integer.parseInt(edt_quantity.text.toString())


        requestInterface.SaveOrders(shopid,Itemid,sk,edt_date.text.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "SuccessFully Saved", Toast.LENGTH_LONG)
                        .show()
                    edt_storename.text = null
                    edt_product.text = null
                    edt_date.text = null
                    edt_quantity.text = null
                } else if(response.code()==400) {
                    progressDialog.dismiss()
//                    Log.e("response", response.body().toString()+ " error")
                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })


    }


    private fun GetShopName() {
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
                    progressDialog.dismiss()
                    Shoplist = response.body()
                    Log.e("Shoplist", Shoplist.size.toString() + " error")
                    ShowShopName()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ArrayList<ShopModel>>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun ShowShopName() {
//        val shopName: ArrayList<String> = ArrayList()
//        for (item in Shoplist) {
//            shopName.add(item.name+" - "+item.location)
//        }
        Shop_dialog = Dialog(this)
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
                Store_adapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        Store_adapter = Store_Adapter(Shoplist, this)
        recyclerview.adapter = Store_adapter

//        recyclerview.addOnItemTouchListener(
//            RecyclerItemClickListener(this, object : RecyclerItemClickListener.OnItemClickListener {
//                override fun onItemClick(view: View, position: Int) {
//                    dialog.dismiss()
//                    Log.e( "recyler", view.select_country_text.text.toString())
//                    edt_storename.setText(view.select_country_text.text.toString())
////                    Log.e( "position",   Store_adapter.getItemId(position).toString());
//
//                }
//            }))

        val metrics = DisplayMetrics()

        this.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)  //set height to 90% of total

        val width = (metrics.widthPixels * 0.9) //set width to 90% of total

        Shop_dialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        Shop_dialog.show()

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
                    Log.e("Itemlist", Itemlist.size.toString() + " error")
                    showItemNameDialog()
                } else {

                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ArrayList<ItemsModel>>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    fun showItemNameDialog() {
        Product_dialog = Dialog(this)
        Product_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        Product_dialog.setCancelable(true)
        Product_dialog.setContentView(R.layout.custom_layout)
        recyclerview = Product_dialog.findViewById(R.id.recyclerview)
        search = Product_dialog.findViewById(R.id.search)
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter.filter.filter(charSequence.toString())

            }

            override fun afterTextChanged(editable: Editable) {}
        })
        adapter = Product_Adapter(Itemlist, this)
        recyclerview.adapter = adapter


        val metrics = DisplayMetrics()

        this.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)  //set height to 90% of total

        val width = (metrics.widthPixels * 0.9) //set width to 90% of total

        Product_dialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        Product_dialog.show()

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_viewOrders-> {
                intent = Intent(applicationContext, ViewDeliveredItems::class.java)
                startActivity(intent)
            }
            R.id.nav_update -> {
            }
            R.id.nav_logout -> {

                viewUtils.alert_view_dialog(this,"","Are you Sure You want to Logout?","Okay","Cancel",true,
                    postive_dialogInterface = DialogInterface.OnClickListener {
                            dialog, which ->
                        dialog.dismiss()
                        sessionManager.clearSession()
                        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                        intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    },negative_dialogInterface = DialogInterface.OnClickListener { dialog, which ->

                            dialog.dismiss()

                    },s = "")

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun ProductSetUp(itemsModel: ItemsModel) {
        if (Product_dialog.isShowing) {
            Product_dialog.dismiss()
        }
        Itemid=itemsModel.id
        edt_product.setText(itemsModel.name+"-"+itemsModel.itemCategory)
    }

    fun StoreSetUp(shopModel: ShopModel) {
        if (Shop_dialog.isShowing) {
            Shop_dialog.dismiss()
        }
        shopid=shopModel.id
        edt_storename.setText(shopModel.name+"-"+shopModel.location)

    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


}