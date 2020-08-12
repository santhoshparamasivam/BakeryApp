package com.example.bakkeryApp

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.adapter.Store_Adapter
import com.example.bakkeryApp.fragments.HomeFragment
import com.example.bakkeryApp.fragments.ViewItemsFragment
import com.example.bakkeryApp.fragments.ViewStockFragment
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.example.bakkeryApp.utils.ViewUtils
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class HomeActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var shopid: String
    lateinit var userId: String
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    lateinit var viewUtils: ViewUtils
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewUtils = ViewUtils()
        sessionManager = SessionManager(this)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        progressDialog = ProgressDialog(this@HomeActivity)
//        val date = Calendar.getInstance().time
//        val formatter = SimpleDateFormat("yyyy/MM/dd")
//        val formatedDate = formatter.format(date)
//        today_date.text =formatedDate
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )

        add_stock_menu.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            loadFragment(ViewStockFragment())
        }
        Dashboard.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            loadFragment(HomeFragment())
        }

        item_menu.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            loadFragment(ViewItemsFragment())
        }

        remove_stock_menu.setOnClickListener {

        }

        logout_menu.setOnClickListener {
            viewUtils.alert_view_dialog(this,
                    "",
                    "Are you sure you want to Logout?",
                    "Yes",
                    "No",
                    true,
                    postive_dialogInterface = DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                        sessionManager.clearSession()
                        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                        intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    negative_dialogInterface = DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()

                    },
                    s = "")
        }


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        stock_move_menu.setOnClickListener {
            if (nav_submenu.isVisible) {
                nav_submenu.visibility = View.GONE
            }else{
                nav_submenu.visibility = View.VISIBLE
            }
        }
        nav_bottomView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_nav_Home-> {
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_stock-> {
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }
        val headerView: View = navView.getHeaderView(0)
        headerView.userName.text = sessionManager.getStringKey(SessionKeys.FIRST_NAME)
        userId = sessionManager.getStringKey(SessionKeys.USER_ID).toString()
        Log.e("user token", sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString())
        hideNaviItems()
        loadFragment(HomeFragment())

    }

    private fun hideNaviItems() {
        val nav_Menu: Menu = navView.menu
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
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

















//        edt_storename.setOnClickListener {
//
//            GetShopName()
//        }
//
//        edt_product.setOnClickListener {
//            getProductName()
//
//        }
//        edt_date.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//
//            val dpd = DatePickerDialog(
//                this@HomeActivity,
//                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    var months = monthOfYear + 1
//
//
//                    if(months==10){
//                        months=10
//                        edt_date.setText("$year-$months-$dayOfMonth")
//                    }else if(months==11){
//                        months=11
//                        edt_date.setText("$year-$months-$dayOfMonth")
//                    }else if(months==12){
//                        months=12
//                        edt_date.setText("$year-$months-$dayOfMonth")
//                    }else{
//                        edt_date.setText("$year-0$months-$dayOfMonth")
//                    }
//                },
//                year,
//                month,
//                day
//            )
//            dpd.datePicker.minDate = System.currentTimeMillis()
//            dpd.show()
//        }
//        Submit.setOnClickListener {
//            if (edt_product.text.toString().isEmpty()) {
//                edt_product.error = "Please Select Item"
//            } else if (edt_storename.text.toString().isEmpty()) {
//                edt_storename.error = "Please Select Store"
//            } else if (edt_quantity.text.toString().isEmpty()) {
//                edt_quantity.error = "Please Enter Quantity"
//            } else if (edt_date.text.toString().isEmpty()) {
//                edt_date.error = "Please select date"
//            } else
//                SubmitDatatoServer()
//        }
//
//    }
//
//    private fun SubmitDatatoServer() {
//        progressDialog.setMessage("Loading...")
//        progressDialog.show()
//        var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
//        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor{ chain ->
//            val newRequest: Request = chain.request().newBuilder()
//                .addHeader("Authorization", "Bearer $user_token")
//                .build()
//            chain.proceed(newRequest)
//        }.build()
//        val requestInterface = Retrofit.Builder()
//            .baseUrl(ApiManager.BASE_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build().create(ApiService::class.java)
//                val sk=Integer.parseInt(edt_quantity.text.toString())
//
//
//        requestInterface.SaveOrders(shopid,Itemid,sk,edt_date.text.toString()).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                progressDialog.dismiss()
//                if (response.code() == 200) {
//                    progressDialog.dismiss()
//                    Toast.makeText(applicationContext, "SuccessFully Saved", Toast.LENGTH_LONG)
//                        .show()
//                    edt_storename.text = null
//                    edt_product.text = null
//                    edt_date.text = null
//                    edt_quantity.text = null
//                } else if(response.code()==400) {
//                    progressDialog.dismiss()
////                    Log.e("response", response.body().toString()+ " error")
//                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                t.printStackTrace()
//                progressDialog.dismiss()
//                Toast.makeText(
//                    applicationContext,
//                    "Connection failed,Please try again later",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        })
//    }
//
//
//    private fun GetShopName() {
//        progressDialog.setMessage("Loading...")
//        progressDialog.show()
//        var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
//        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
//            val newRequest: Request = chain.request().newBuilder()
//                .addHeader("Authorization", "Bearer $user_token")
//                .build()
//            chain.proceed(newRequest)
//        }.build()
//        val requestInterface = Retrofit.Builder()
//            .baseUrl(ApiManager.BASE_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build().create(ApiService::class.java)
//        requestInterface.GetShops().enqueue(object : Callback<ArrayList<ShopModel>> {
//            override fun onResponse(
//                call: Call<ArrayList<ShopModel>>,
//                response: Response<ArrayList<ShopModel>>
//            ) {
//                progressDialog.dismiss()
//                Log.e("response", response.code().toString() + " error")
//                if (response.code() == 200) {
//                    progressDialog.dismiss()
//                    Shoplist = response.body()
//                    Log.e("Shoplist", Shoplist.size.toString() + " error")
//                    ShowShopName()
//                } else {
//                    progressDialog.dismiss()
//                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//
//            override fun onFailure(call: Call<ArrayList<ShopModel>>, t: Throwable) {
//                t.printStackTrace()
//                progressDialog.dismiss()
//                Toast.makeText(
//                    applicationContext,
//                    "Connection failed,Please try again later",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        })
//    }
//
//    private fun ShowShopName() {
////        val shopName: ArrayList<String> = ArrayList()
////        for (item in Shoplist) {
////            shopName.add(item.name+" - "+item.location)
////        }
//        Shop_dialog = Dialog(this)
//        Shop_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        Shop_dialog.setCancelable(true)
//        Shop_dialog.setContentView(R.layout.custom_layout)
//        recyclerview = Shop_dialog.findViewById(R.id.recyclerview)
//        search = Shop_dialog.findViewById(R.id.search)
//        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
//
//        recyclerview.setHasFixedSize(true)
//        search.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                Store_adapter.filter.filter(charSequence.toString())
//            }
//
//            override fun afterTextChanged(editable: Editable) {}
//        })
//        Store_adapter = Store_Adapter(Shoplist, this)
//        recyclerview.adapter = Store_adapter
//
////        recyclerview.addOnItemTouchListener(
////            RecyclerItemClickListener(this, object : RecyclerItemClickListener.OnItemClickListener {
////                override fun onItemClick(view: View, position: Int) {
////                    dialog.dismiss()
////                    Log.e( "recyler", view.select_country_text.text.toString())
////                    edt_storename.setText(view.select_country_text.text.toString())
//////                    Log.e( "position",   Store_adapter.getItemId(position).toString());
////
////                }
////            }))
//
//        val metrics = DisplayMetrics()
//
//        this.windowManager.defaultDisplay.getMetrics(metrics)
//        val height = (metrics.heightPixels * 0.5)  //set height to 90% of total
//
//        val width = (metrics.widthPixels * 0.9) //set width to 90% of total
//
//        Shop_dialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
//        Shop_dialog.show()
//
//    }
//
//
//    private fun getProductName() {
//        progressDialog.setMessage("Loading...")
//        progressDialog.show()
//        var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
//        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
//            val newRequest: Request = chain.request().newBuilder()
//                .addHeader("Authorization", "Bearer $user_token")
//                .build()
//            chain.proceed(newRequest)
//        }.build()
//        val requestInterface = Retrofit.Builder()
//            .baseUrl(ApiManager.BASE_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build().create(ApiService::class.java)
//        requestInterface.GetItems().enqueue(object : Callback<ArrayList<ItemsModel>> {
//            override fun onResponse(
//                call: Call<ArrayList<ItemsModel>>,
//                response: Response<ArrayList<ItemsModel>>
//            ) {
//                progressDialog.dismiss()
//                Log.e("response", response.code().toString() + "  rss")
//                if (response.code() == 200) {
//
//                    Itemlist = response.body()
//                    Log.e("Itemlist", Itemlist.size.toString() + " error")
//                    showItemNameDialog()
//                } else {
//
//                    progressDialog.dismiss()
//                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//
//            override fun onFailure(call: Call<ArrayList<ItemsModel>>, t: Throwable) {
//                t.printStackTrace()
//                progressDialog.dismiss()
//                Toast.makeText(
//                    applicationContext,
//                    "Connection failed,Please try again later",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        })
//    }
//
//    fun showItemNameDialog() {
//        Product_dialog = Dialog(this)
//        Product_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        Product_dialog.setCancelable(true)
//        Product_dialog.setContentView(R.layout.custom_layout)
//        recyclerview = Product_dialog.findViewById(R.id.recyclerview)
//        search = Product_dialog.findViewById(R.id.search)
//        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
//        recyclerview.setHasFixedSize(true)
//        search.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                adapter.filter.filter(charSequence.toString())
//
//            }
//
//            override fun afterTextChanged(editable: Editable) {}
//        })
//        adapter = Product_Adapter(Itemlist, this)
//        recyclerview.adapter = adapter
//
//
//        val metrics = DisplayMetrics()
//
//        this.windowManager.defaultDisplay.getMetrics(metrics)
//        val height = (metrics.heightPixels * 0.5)  //set height to 90% of total
//
//        val width = (metrics.widthPixels * 0.9) //set width to 90% of total
//
//        Product_dialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
//        Product_dialog.show()
//
//    }




//    fun ProductSetUp(itemsModel: ItemsModel) {
//        if (Product_dialog.isShowing) {
//            Product_dialog.dismiss()
//        }
//        Itemid= itemsModel.id!!
//        edt_product.setText(itemsModel.name+"-"+itemsModel.itemCategory)
//    }
//
//    fun StoreSetUp(shopModel: ShopModel) {
//        if (Shop_dialog.isShowing) {
//            Shop_dialog.dismiss()
//        }
//        shopid= shopModel.id!!
//        edt_storename.setText(shopModel.name+"-"+shopModel.location)
//
//    }




//}