package com.example.bakkeryApp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakkeryApp.adapter.ItemCategoryAdapter
import com.example.bakkeryApp.adapter.PriceHistoryAdapter
import com.example.bakkeryApp.model.ItemCategoryModel
import com.example.bakkeryApp.model.ItemHistoryModel
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.retrofitService.ApiManager.Companion.BASE_URL
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.example.bakkeryApp.utils.RecyclerItemClickListener
import com.example.bakkeryApp.utils.ViewUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_view_single_item.*
import kotlinx.android.synthetic.main.activity_view_single_item.checkbox_sell_tax
import kotlinx.android.synthetic.main.activity_view_single_item.edt_category
import kotlinx.android.synthetic.main.activity_view_single_item.edt_sku
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URLEncoder
import kotlin.math.roundToInt


class ViewSingleItem : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var sessionManager: SessionManager
    lateinit var mContext: Context
    private lateinit var priceHistDialog: Dialog
    lateinit var recyclerview: RecyclerView
    lateinit var search: EditText
    lateinit var adapter: PriceHistoryAdapter
    var itemHistoryList: ArrayList<ItemHistoryModel> = ArrayList()
    var id:Long = 0
    lateinit var progressDialog: ProgressDialog
    private lateinit var itemCategoryDialog: Dialog
    lateinit var itemAdapter: ItemCategoryAdapter
    private lateinit var viewUtils: ViewUtils
    lateinit var itemImageView: ImageView
    private var isPermitted:Boolean = false
    private lateinit var outputFileUri : Uri
    private var taxIncluded =false
    private val SELECT_PICTURE = 10
    private val REQUEST_CAMERA = 11
    lateinit var type: String
    private lateinit var file:File
    var itemCategoryModelList: ArrayList<ItemCategoryModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_item)
        itemImageView=findViewById(R.id.itemImageView)
        id = intent.getLongExtra("id", 0)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewUtils = ViewUtils()
        progressDialog = ProgressDialog(this)
        mContext= this
        supportActionBar?.title ="View Items"
        sessionManager= SessionManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        edt_category.isEnabled = false
        edt_name.isEnabled  = false
        edt_hsnCode.isEnabled = false
        edt_sku.isEnabled  = false
        edt_unitOfMeasure.isEnabled = false
        checkbox_cost_tax.isEnabled  = false
        edt_taxPercentage.isEnabled  = false
        edt_price.isEnabled = false
        edt_sell_Price.isEnabled  = false
        checkbox_sell_tax.isEnabled  = false
        edt_trackInventory.isEnabled=false
        edt_radio_product.isEnabled=false
        edt_radio_service.isEnabled=false

        img_query.setOnClickListener {
            edt_category.isEnabled = true
            edt_category.isCursorVisible=false
            edt_name.isEnabled  = true
            edt_hsnCode.isEnabled = true
            edt_sku.isEnabled  = false
            edt_unitOfMeasure.isEnabled = true
            checkbox_cost_tax.isEnabled  = true
            edt_taxPercentage.isEnabled  = true
            edt_price.isEnabled = true
            edt_sell_Price.isEnabled  = true
            checkbox_sell_tax.isEnabled  = true
            edt_trackInventory.isEnabled=true
            edt_radio_product.isEnabled=false
            edt_radio_service.isEnabled=false

            updateItem.visibility= View.VISIBLE
            priceHistory.visibility=View.GONE
            edt_category.requestFocus()
            if(edt_category.requestFocus()) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
        updateItem.setOnClickListener {
            updateItemMethod()
        }

        edt_category.setOnClickListener {
            getItemCategory()
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
        checkbox_sell_tax.setOnClickListener{
            taxIncluded=true
        }
        priceHistory.setOnClickListener {
            getPriceHistory(id)
            //showItemHistDialog();
        }

        itemImageView.setOnClickListener {
            checkRunTimePermission()
        }

        checkbox_cost_tax.setOnClickListener{
            taxIncluded=true
        }

        voidItem.setOnClickListener {
//            viewUtils.alertViewDialog(this,
//                "Are you sure you want to Delete?",
//                "Yes",
//                "No",
//                true,
//                positiveDialogInterface = DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//                    deleteItemMethod()
//                },
//                negative_dialogInterface = DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//
//                }
//            )
            viewUtils.bakeryAlert(
                this,
                "Are you sure you want to Delete?",
                "Yes",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    deleteItemMethod()
                },"No",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                },true
            )

        }
     loadSingleItem()
    }
    private fun checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA
                ),
                1
            )
        }else{
            checkStoragePermission()
        }

    }
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                2
            )        }
        else{
            takePhoto()
        }


    }

    private fun takePhoto() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    takePhotoMethod()
                }
                items[item] == "Choose from Library" -> {
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    intent.type = "image/*"
                    startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        SELECT_PICTURE
                    )
                }
                items[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun takePhotoMethod() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {
                    checkStoragePermission()
                } else {
                    var details = "Please Allow Camera Permission For Capture the Image"
                    permissionDeniedAlertBox(details)
                }
                return
            }
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    takePhoto()
                } else {
                    var details = "Please Allow Storage Permission For Store and Retrieve Data"

                    permissionDeniedAlertBox(details)
                }
                return
            }
        }
    }

    private fun permissionDeniedAlertBox(details: String) {
//        viewUtils.alertViewDialog(
//            this,
//            details,
//            "Okay",
//            "Cancel",
//            true,
//            positiveDialogInterface = DialogInterface.OnClickListener { dialog, which ->
//                dialog.dismiss()
//                checkRunTimePermission()
//            },
//            negative_dialogInterface = DialogInterface.OnClickListener { dialog, which ->
//                dialog.dismiss()
//
//            }
//
//        )
        viewUtils.bakeryAlert(
            this,
            details,
            "Okay",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                checkRunTimePermission()
            },"Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            },true
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {

            } else if (requestCode == SELECT_PICTURE) {
                val selectedImageUri = data?.data
                outputFileUri= selectedImageUri!!
                CropImage.activity(outputFileUri)
                    .start(this)
            }
            if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode === RESULT_OK) {
                    outputFileUri = result.uri
                    itemImageView.setImageURI(outputFileUri)
                    file =  File(outputFileUri.path)
                    Log.e("file",file.name+"   ")
                } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }


    private fun deleteItemMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        val userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $userToken")
                .build()
            chain.proceed(newRequest)
        }.build()
        val requestInterface = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        val call=requestInterface.voidItemDetails(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Item is successfully deleted", Toast.LENGTH_SHORT)
                        .show()
                    finish()
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

    private fun getItemCategory() {
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
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        requestInterface.getItemCategories().enqueue(object :
            Callback<ArrayList<ItemCategoryModel>> {
            override fun onResponse(
                call: Call<ArrayList<ItemCategoryModel>>,
                response: Response<ArrayList<ItemCategoryModel>>
            ) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + "  rss")
                if (response.code() == 200) {

                    itemCategoryModelList = response.body()
                    showItemNameDialog()
                } else {

                    progressDialog.dismiss()
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Please try again later",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<ArrayList<ItemCategoryModel>>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    this@ViewSingleItem,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    fun showItemNameDialog() {
        var finalShopList = ArrayList<ItemCategoryModel>()
        itemCategoryDialog = Dialog(this)
        itemCategoryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        itemCategoryDialog.setCancelable(true)
        itemCategoryDialog.setContentView(R.layout.custom_layout)
        recyclerview = itemCategoryDialog.findViewById(R.id.recyclerview)
        search = itemCategoryDialog.findViewById(R.id.search)
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                finalShopList.clear()
                for (item in itemCategoryModelList) {
                    if (item.name?.toLowerCase()?.contains(charSequence.toString())!!) {
                        finalShopList.add(item)
                    }
                }
                itemAdapter = ItemCategoryAdapter(itemCategoryModelList, applicationContext)
                recyclerview.adapter = itemAdapter

            }

            override fun afterTextChanged(editable: Editable) {}
        })
        finalShopList.addAll(itemCategoryModelList)
        recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (itemCategoryDialog.isShowing) {
                            itemCategoryDialog.dismiss()
                        }
                        edt_category.setText(finalShopList[position].name)
                    }
                })
        )
        itemAdapter = ItemCategoryAdapter(itemCategoryModelList, applicationContext)
        recyclerview.adapter = itemAdapter


        val metrics = DisplayMetrics()

        this.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)

        val width = (metrics.widthPixels * 0.9)

        itemCategoryDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        itemCategoryDialog.show()

    }

    private fun updateItemMethod() {
        var itemSku=edt_sku.text.toString()
        var itemHsn=edt_hsnCode.text.toString()
        var costPrice=edt_price.text.toString().toFloat()

        val sellingPrice = edt_sell_Price.text.toString().toFloat()

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
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        requestInterface.updateItems(id, costPrice, sellingPrice).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Product updated successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
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

    private fun loadSingleItem() {
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
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)

        requestInterface.getItemDetails(id).enqueue(object : Callback<ItemsModel> {
            override fun onResponse(
                call: Call<ItemsModel>,
                response: Response<ItemsModel>
            ) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + "  rss")
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    var itemsModel = ItemsModel()
                    itemsModel = response.body()

                    val encodedURL: String =
                        URLEncoder.encode(itemsModel.imageFileName, "UTF-8").replace(
                            "+",
                            "%20"
                        )
                    var uri = BASE_URL + "downloadfile/item/" + encodedURL

                    Glide.with(mContext as ViewSingleItem).load(uri).into(itemImageView)

                    edt_category.setText(itemsModel.itemCategory)
                    edt_name.setText(itemsModel.name)
                    edt_hsnCode.setText(itemsModel.hsnCode)
                    edt_sku.setText(itemsModel.sku)
                    edt_unitOfMeasure.setText(itemsModel.unitOfMeasure)
                    checkbox_cost_tax.isChecked = itemsModel.taxIncluded!!
                    edt_taxPercentage.setText(itemsModel.taxPercentage.toString())
                    edt_sell_Price.setText(itemsModel.sellingPrice.toString())
                    edt_price.setText(itemsModel.costPrice.toString())
                    edt_trackInventory.isChecked = itemsModel.trackInventory!!
                    if (itemsModel.type == "PRODUCT") {
                        edt_radio_product.isChecked = true
                        edt_radio_service.isChecked = false
                    } else {
                        edt_radio_service.isChecked = true
                        edt_radio_product.isChecked = false
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ItemsModel>, t: Throwable) {
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

    fun showItemHistDialog() {
        priceHistDialog = Dialog(this)
        priceHistDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        priceHistDialog.setCancelable(true)
        priceHistDialog.setContentView(R.layout.price_history)
        recyclerview = priceHistDialog.findViewById(R.id.price_history_recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        adapter = PriceHistoryAdapter(itemHistoryList, this)
        recyclerview.adapter = adapter

        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)
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

    private fun getPriceHistory(id: Long) {
        var userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $userToken")
                .build()
            chain.proceed(newRequest)
        }.build()
        val requestInterface = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)

        requestInterface.getItemPriceHistory(id).enqueue(object :
            Callback<ArrayList<ItemHistoryModel>> {
            override fun onResponse(
                call: Call<ArrayList<ItemHistoryModel>>,
                response: Response<ArrayList<ItemHistoryModel>>
            ) {
                Log.e("response", response.code().toString() + "  rss")
                if (response.code() == 200) {

                    itemHistoryList = response.body()

                    showItemHistDialog()

                } else {
                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ArrayList<ItemHistoryModel>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(
                    applicationContext,
                    "Server Error,Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}