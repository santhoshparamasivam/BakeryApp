package com.cbe.bakery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cbe.bakery.adapter.ItemCategoryAdapter
import com.cbe.bakery.adapter.PriceHistoryAdapter
import com.cbe.bakery.model.ItemCategoryModel
import com.cbe.bakery.model.ItemHistoryModel
import com.cbe.bakery.model.ItemsModel
import com.cbe.bakery.model.PinVerificationModel
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiManager.Companion.BASE_URL
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.RecyclerItemClickListener
import com.cbe.bakery.utils.ViewUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_view_single_item.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
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
    private lateinit var file: File
    private var saleTaxIncluded : Boolean=false
    private var costTaxIncluded : Boolean=false
    var itemCategoryModelList: ArrayList<ItemCategoryModel> = ArrayList()
    lateinit var edt_units: Spinner

    lateinit var body:MultipartBody.Part
    lateinit var simple:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_item)
        itemImageView=findViewById(R.id.itemImageView)
        itemImageView.isEnabled=false
        itemImageView.isClickable=false
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
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        edt_category.isEnabled = false
        edt_name.isEnabled  = false
        edt_hsnCode.isEnabled = false
        edt_sku.isEnabled  = false
        checkbox_cost_tax.isEnabled  = false
        edt_taxPercentage.isEnabled  = false
        edt_price.isEnabled = false
        edt_sell_Price.isEnabled  = false
        checkbox_sell_tax.isEnabled  = false
        edt_trackInventory.isEnabled=false
        edt_radio_product.isEnabled=false
        edt_radio_service.isEnabled=false

        img_query.setOnClickListener {
            img_query.visibility=View.GONE
            edt_category.isEnabled = true
            edt_category.isCursorVisible=false
            edt_name.isEnabled  = true
            edt_hsnCode.isEnabled = true
            edt_sku.isEnabled  = false
            checkbox_cost_tax.isEnabled  = true
            edt_taxPercentage.isEnabled  = true
            edt_price.isEnabled = true
            edt_sell_Price.isEnabled  = true
            checkbox_sell_tax.isEnabled  = true
            edt_trackInventory.isEnabled=true
            edt_radio_product.isEnabled=false
            edt_radio_service.isEnabled=false
            itemImageView.isEnabled=false
            itemImageView.isClickable=false
            updateItem.visibility= View.VISIBLE
            priceHistory.visibility=View.GONE
            edt_category.requestFocus()
            if(edt_category.requestFocus()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }
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
        priceHistory.setOnClickListener {
            getPriceHistory(id)
        }
        checkbox_sell_tax.setOnClickListener{
            saleTaxIncluded=true
        }

        edt_trackInventory.setOnClickListener{
            taxIncluded=true
        }

        itemImageView.setOnClickListener {
            checkRunTimePermission()
        }

//        val dropdown: Spinner = findViewById(R.id.edt_units)
        edt_units = findViewById(R.id.edt_units)
        val items = arrayOf(
            "KG",
            "Gram",
            "Litre",
            "Ml",
            "Box",
            "Dozen",
            "Each/Piece"
        )
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        edt_units.adapter = adapter

        voidItem.setOnClickListener {
//            viewUtils.bakeryAlert(
//                this,
//                "Are you sure you want to Delete?",
//                "Yes",
//                DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//
//                },"No",
//                DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//                },true
//            )
            verificationMethod()


        }
     loadSingleItem()
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
                    edt_two.isFocusable = true
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {

            }
        })
        edt_two.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_two.length() == 1) {
                    edt_three.isFocusable = true
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {

            }
        })
        edt_three.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_three.length() == 1) {
                    edt_four.isFocusable = true
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {

            }
        })
        edt_four.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_four.length() == 1) {
                    edt_four.isFocusable = false
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
                deleteItemMethod()
            }else{
            Toast.makeText(applicationContext, "Please Enter Valid Pin...", Toast.LENGTH_SHORT).show()
        }
    }
        btn_neg.setOnClickListener {

                alertDialog.dismiss()
        }
    alertDialog.show()
    }

    private fun verificationMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.dismiss()
        var userId = sessionManager.getStringKey(SessionKeys.USER_ID).toString()
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
        requestInterface.getPin(userId).enqueue(object : Callback<PinVerificationModel> {
            override fun onResponse(
                call: Call<PinVerificationModel>,
                response: Response<PinVerificationModel>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    Log.e("pin verify", response.body().pin + "  ");
                    response.body().pin?.let { pinVerificationAlert(it) }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Please try again late",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<PinVerificationModel>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    this@ViewSingleItem,
                    "Connection Failed,Please try again late",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
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
        viewUtils.bakeryAlert(
            this,
            details,
            "Okay",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                checkRunTimePermission()
            }, "Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            }, true
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
                simple="file"
            }
            if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode === RESULT_OK) {
                    outputFileUri = result.uri
                    Log.e("imageview", outputFileUri.path + "   ")
                    itemImageView.setImageURI(outputFileUri)
                    file =  File(outputFileUri.path)
                    simple="file"
                    saveFileIntoLocal(file)
                } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    viewUtils.showToast(this@ViewSingleItem,"Please Select and try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Please Select and try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun saveFileIntoLocal(files: File) {
        var fileName = files.name
        try {
            val apkStorage = File(applicationContext.cacheDir.toString()+"/Image/")
            apkStorage.mkdirs()
            if (!apkStorage.exists()) {
                if (!apkStorage.mkdirs()) {
                }
            }
            val mediaFile = File(apkStorage.path + File.separator)
            file = File(mediaFile, fileName)
            try {
                if (!file.exists()) {
                    file.createNewFile()
                }
            } catch (ex: IOException) {
            }
            val fos = FileOutputStream(file)
            val inputStream: InputStream? = contentResolver.openInputStream(outputFileUri)
            var total: Long = 0
            val data = ByteArray(8192)
            var len1: Int
            while (inputStream?.read(data).also { len1 = it!! } != -1) {
                total += len1.toLong()
                fos.write(data, 0, len1)
            }
            fos.flush()
            fos.close()
            inputStream?.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun deleteItemMethod() {
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
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        val call=requestInterface.voidItemDetails(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewSingleItem,"Item is successfully deleted",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Item is successfully deleted",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewSingleItem,"Please try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
//                viewUtils.showToast(this@ViewSingleItem,"Connection Failed,Please try again later",Toast.LENGTH_SHORT)
                Toast.makeText(
                    this@ViewSingleItem,
                    "Connection Failed,Please try again late",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getItemCategory() {
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
                if (response.code() == 200) {
                    itemCategoryModelList = response.body()
                    showItemNameDialog()
                } else {

                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewSingleItem,"Please try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Please try again late",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<ItemCategoryModel>>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
//                viewUtils.showToast(this@ViewSingleItem,"Connection Failed,Please try again later",Toast.LENGTH_SHORT)
                Toast.makeText(
                    this@ViewSingleItem,
                    "Connection Failed,Please try again late",
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
                itemAdapter = ItemCategoryAdapter(
                    itemCategoryModelList,
                    applicationContext
                )
                recyclerview.adapter = itemAdapter

            }

            override fun afterTextChanged(editable: Editable) {}
        })
        finalShopList.addAll(itemCategoryModelList)
        recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                object :
                    RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (itemCategoryDialog.isShowing) {
                            itemCategoryDialog.dismiss()
                        }
                        edt_category.setText(finalShopList[position].name)
                    }
                })
        )
        itemAdapter = ItemCategoryAdapter(
            itemCategoryModelList,
            applicationContext
        )
        recyclerview.adapter = itemAdapter


        val metrics = DisplayMetrics()

        this.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)

        val width = (metrics.widthPixels * 0.9)

        itemCategoryDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        itemCategoryDialog.show()

    }
    @NonNull
    private fun createPartFromString(descriptionString: String): RequestBody? {
        return RequestBody.create(
            MediaType.parse("multipart/form-data"), descriptionString
        )
    }
    private fun updateItemMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        if(simple!="noFile"){
         var   requestFile  = RequestBody.create(MediaType.parse("multipart/form-data"), file)
             body =MultipartBody.Part.createFormData("files", file.name, requestFile)
        }else{
          var  requestFile  = RequestBody.create(MediaType.parse("multipart/form-data"), "")
            body =MultipartBody.Part.createFormData("files", "", requestFile)
        }
        val itemType: RequestBody? = createPartFromString(type)
        val itemCategory: RequestBody? = createPartFromString(edt_category.text.toString())
        val itemName: RequestBody? = createPartFromString(edt_name.text.toString())
        val sku: RequestBody? = createPartFromString(edt_sku.text.toString())
        val unitMeasures: RequestBody? =
            createPartFromString(edt_units.selectedItem.toString())
        val costPrice: RequestBody? = createPartFromString(edt_price.text.toString())
        val taxIncluded: RequestBody? = createPartFromString(taxIncluded.toString())
        val saleTaxIncluded: RequestBody? = createPartFromString(saleTaxIncluded.toString())
        val costTaxIncluded: RequestBody? = createPartFromString(costTaxIncluded.toString())
        val sellPrice: RequestBody? = createPartFromString(edt_sell_Price.text.toString())
        val taxPercentage: RequestBody? = createPartFromString(edt_taxPercentage.text.toString())
        val hsnCode: RequestBody? = createPartFromString(edt_hsnCode.text.toString())
        var userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN)
        val Id: RequestBody? = createPartFromString(id.toString())
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
        requestInterface.updateItems(
            body, Id,
            itemType,
            itemName,
            itemCategory,
            costPrice,
            sellPrice,
            taxPercentage,
            unitMeasures,
            taxIncluded,
            saleTaxIncluded,
            costTaxIncluded,
            hsnCode,
            sku
        ).enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NewApi")
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewSingleItem,"SuccessFully Saved",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Successfully Saved",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewSingleItem,"Please try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Please try again late",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
//                viewUtils.showToast(this@ViewSingleItem,"Connection failed,Please try again later",Toast.LENGTH_SHORT)
                Toast.makeText(
                    this@ViewSingleItem,
                    "Connection Failed,Please try again late",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun loadSingleItem() {
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
                    checkbox_cost_tax.isChecked = itemsModel.costTaxIncluded!!
                    checkbox_sell_tax.isChecked = itemsModel.saleTaxIncluded!!
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
                    type = itemsModel.type!!
                    costTaxIncluded = itemsModel.costTaxIncluded!!
                    saleTaxIncluded = itemsModel.saleTaxIncluded!!
                    taxIncluded = itemsModel.trackInventory!!
                    simple = "noFile"
                } else {
                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewSingleItem,"Please try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Please try again late",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ItemsModel>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()

//                viewUtils.showToast(this@ViewSingleItem,"Connection Failed,Please try again later",Toast.LENGTH_SHORT)
                Toast.makeText(
                    this@ViewSingleItem,
                    "Connection Failed,Please try again late",
                    Toast.LENGTH_LONG
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
        adapter =
            PriceHistoryAdapter(itemHistoryList, this)
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
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    itemHistoryList = response.body()

                    showItemHistDialog()

                } else {
                    progressDialog.dismiss()
//                    viewUtils.showToast(this@ViewSingleItem,"Please try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@ViewSingleItem,
                        "Please try again late",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ArrayList<ItemHistoryModel>>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
//                viewUtils.showToast(this@ViewSingleItem,"Connection Failed,Please try again later",Toast.LENGTH_SHORT)
                Toast.makeText(
                    this@ViewSingleItem,
                    "Connection Failed,Please try again late",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

}