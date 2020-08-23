package com.example.bakkeryApp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
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
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.adapter.ItemCategoryAdapter
import com.example.bakkeryApp.model.ItemCategoryModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.example.bakkeryApp.utils.RecyclerItemClickListener
import com.example.bakkeryApp.utils.ViewUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_item.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AddItemActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    private lateinit var viewUtils: ViewUtils
    private lateinit var itemCategoryDialog: Dialog
    lateinit var recyclerview: RecyclerView
    lateinit var search: EditText
    lateinit var adapter: ItemCategoryAdapter
    lateinit var edtCategory: EditText
    private lateinit var itemImageView: ImageView
    var itemCategoryModelList: ArrayList<ItemCategoryModel> = ArrayList()
    private val SELECT_PICTURE = 10
    private val REQUEST_CAMERA = 11
    lateinit var toolbar: Toolbar
    private var isPermitted:Boolean = false
    private lateinit var outputFileUri : Uri
    private var taxIncluded : Boolean=false
    lateinit var type: String
    private lateinit var file:File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        viewUtils = ViewUtils()
        edtCategory=findViewById(R.id.edt_category)
        itemImageView=findViewById(R.id.itemImageView)

        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
         toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        itemCategoryDialog = Dialog(this)
        supportActionBar?.title ="Add Item"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
          finish()
        }

        val dropdown: Spinner = findViewById(R.id.edt_units)
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
        dropdown.adapter = adapter

        edtCategory.setOnClickListener {
            getItemCategory()
        }

        itemImageView.setOnClickListener {
            checkRunTimePermission()
        }

        checkbox_cost_tax.setOnClickListener{
            taxIncluded=true
        }

        type = ""
        radio_product.setOnClickListener{
            type="PRODUCT"
        }
        radio_service.setOnClickListener {
            type="SERVICE"
        }

        checkbox_sell_tax.setOnClickListener{
            taxIncluded=true
        }

        checkbox_tax.setOnClickListener{
            taxIncluded=true
        }

        create_item.setOnClickListener {
            when {
                type.isEmpty() -> {
                    val lastRadioBtn = findViewById<RadioButton>(R.id.radio_product)
                    lastRadioBtn.error = "Select Type"
                }
                edtCategory.text.toString().isEmpty() -> {
                    edtCategory.error = "Please Select Category"
                }
                edt_item.text.toString().isEmpty() -> {
                    edt_item.error="Please Enter Item Name"
                }
                edt_sku.text.toString().isEmpty() -> {
                    edt_sku.error="Please Enter SKU"
                }
                edt_units.selectedItem == null -> {
                    val errorText = edt_units.selectedView as TextView
                    errorText.error = ""
                    errorText.setTextColor(Color.RED) //just to highlight that this is an error
                    errorText.text = "Select unit"
                }
                else -> {
                    addItemMethod()
                }
            }
        }
    }

    @NonNull
    private fun createPartFromString(descriptionString: String): RequestBody? {
        return RequestBody.create(
            MediaType.parse("multipart/form-data"), descriptionString
        )
    }


    private fun addItemMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()


            Log.e("name", file.name + "  ")
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("files", file.name, requestFile)
            val itemType: RequestBody? = createPartFromString(type)
            val itemCategory: RequestBody? = createPartFromString(edtCategory.text.toString())
            val itemName: RequestBody? = createPartFromString(edt_item.text.toString())
            val sku: RequestBody? = createPartFromString(edt_sku.text.toString())
            val unitMeasures: RequestBody? =
                createPartFromString(edt_units.selectedItem.toString())
            val costPrice: RequestBody? = createPartFromString(edt_price.text.toString())
            val taxIncluded: RequestBody? = createPartFromString(taxIncluded.toString())
            val sellPrice: RequestBody? = createPartFromString(edt_sell_Price.text.toString())
            val taxPercentage: RequestBody? = createPartFromString(edt_tax.text.toString())
            val hsnCode: RequestBody? = createPartFromString(edt_hsn.text.toString())
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
            requestInterface.saveItems(
                body,
                itemType,
                itemName,
                itemCategory,
                costPrice,
                sellPrice,
                taxPercentage,
                unitMeasures,
                taxIncluded,
                hsnCode,
                sku
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    progressDialog.dismiss()
                    if (response.code() == 200) {
                        progressDialog.dismiss()
                        edtCategory.text = null
                        edt_item.text = null
                        edt_sku.text = null
                        edt_price.text = null
                        edt_sell_Price.text = null
                        edt_tax.text = null
                        edt_hsn.text = null
                        Toast.makeText(applicationContext, "SuccessFully Saved", Toast.LENGTH_LONG)
                            .show()

                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Please try again later",
                            Toast.LENGTH_LONG
                        )
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

    private fun checkRunTimePermission() {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this@AddItemActivity,
//                    Manifest.permission.CAMERA)) {
//                ActivityCompat.requestPermissions(this@AddItemActivity,
//                    arrayOf(Manifest.permission.CAMERA), 1)
//            } else {
//                    CheckStoragePermission()
//            }
//        val permission = ActivityCompat.checkSelfPermission(this,
//            Manifest.permission.CAMERA)
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(String[] {
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }else{
//            CheckStoragePermission()
//        }
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
        viewUtils.alertViewDialog(
            this,
            "",
            details,
            "Okay",
            "Cancel",
            true,
            positiveDialogInterface = DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                checkRunTimePermission()
            },
            negative_dialogInterface = DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()

            },
            s = ""
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

    fun getPath(uri: Uri?, activity: Activity): String {
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor = activity
            .managedQuery(uri, projection, null, null, null)
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
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
            .baseUrl(ApiManager.BASE_URL)
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
                    Toast.makeText(
                        this@AddItemActivity,
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
                    this@AddItemActivity,
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
                adapter = ItemCategoryAdapter(itemCategoryModelList, applicationContext)
                recyclerview.adapter = adapter

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
                        edtCategory.setText(finalShopList[position].name)
                    }
                })
        )
        adapter = ItemCategoryAdapter(itemCategoryModelList, applicationContext)
        recyclerview.adapter = adapter


        val metrics = DisplayMetrics()

        this.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)

        val width = (metrics.widthPixels * 0.9)

        itemCategoryDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        itemCategoryDialog.show()

    }

    fun itemCategorySetUp(itemCategory: ItemCategoryModel) {
        if (itemCategoryDialog.isShowing){
            itemCategoryDialog.dismiss()
            edtCategory.setText(itemCategory.name)
        }
    }
}
