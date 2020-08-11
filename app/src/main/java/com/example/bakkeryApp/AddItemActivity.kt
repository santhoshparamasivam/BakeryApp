package com.example.bakkeryApp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.adapter.Product_Adapter
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.example.bakkeryApp.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_add_item.edt_item
import kotlinx.android.synthetic.main.activity_add_item.edt_sku
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AddItemActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    lateinit var viewUtils: ViewUtils
    private lateinit var productDialog: Dialog
    lateinit var recyclerview: RecyclerView
    lateinit var search: EditText
    lateinit var adapter: Product_Adapter
    lateinit var edtCategory: EditText
    lateinit var productImageView: ImageView
    var itemslist: ArrayList<ItemsModel> = ArrayList()
    private val SELECT_PICTURE = 10
    private val REQUEST_CAMERA = 11
    lateinit var toolbar: Toolbar
    private var isPermitted:Boolean = false
    lateinit var outputFileUri : Uri
    var taxIncluded : Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        viewUtils = ViewUtils()
        edtCategory=findViewById(R.id.edt_category)
        productImageView=findViewById(R.id.product_ImageView)

        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
         toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        productDialog = Dialog(this)
        supportActionBar?.title ="Add Items"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
            toolbar.setNavigationOnClickListener {
              finish()
            }
        edtCategory.setOnClickListener {
            getProductName()
        }
        productImageView.setOnClickListener {
            checkRunTimePermission();
            }
        checkbox_cost_tax.setOnClickListener{
            taxIncluded=true
        }
        checkbox_sell_tax.setOnClickListener{
            taxIncluded=true
        }
        checkbox_tax.setOnClickListener{
            taxIncluded=true
        }



        create_item.setOnClickListener {
            if(edtCategory.text.toString().isEmpty()){
                edtCategory.error = "Please Select Category"
            }else if(edt_item.text.toString().isEmpty()){
                edt_item.error="Please Enter Item Name"
            }else if(edt_sku.text.toString().isEmpty()){
                edt_sku.error="Please Enter SKU"
            }else if(edt_units.text.toString().isEmpty()){
                edt_units.error="Please Enter Units"
            }
            SubmitMethod()
        }
        }

    @NonNull
    private fun createPartFromString(descriptionString: String): RequestBody? {
        return RequestBody.create(
            MediaType.parse("multipart/form-data"), descriptionString
        )
    }


    private fun SubmitMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        val file = File(getPath(outputFileUri,this))
        Log.e("name",file.name+"  ")
    ;
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("files", file.name, requestFile)

        val item_Name: RequestBody? = createPartFromString(edt_item.text.toString())
        val item_category: RequestBody? = createPartFromString(edtCategory.text.toString())
        val cost_Price: RequestBody? = createPartFromString(edt_price.text.toString())
        val sell_Price: RequestBody? = createPartFromString(edt_sell_Price.text.toString())
        val tax_percentage: RequestBody? = createPartFromString(edt_tax.text.toString())
        val tax_included: RequestBody? = createPartFromString(taxIncluded.toString())
        val unit_measures: RequestBody? = createPartFromString(edt_units.text.toString())
        val hsn_Code: RequestBody? = createPartFromString(edt_hsn.text.toString())
        val sku: RequestBody? = createPartFromString(edt_sku.text.toString())
        var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN)
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
        requestInterface.SaveOrders(body,item_Name,item_category,cost_Price,sell_Price,tax_percentage,unit_measures,tax_included,hsn_Code,sku).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "SuccessFully Saved", Toast.LENGTH_LONG)
                        .show()

                }else if(response.code()==400) {
                    progressDialog.dismiss()
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

    private fun checkRunTimePermission() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@AddItemActivity,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this@AddItemActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                    CheckStoragePermission()
            }
    }
    private fun CheckStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@AddItemActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this@AddItemActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }else{
            TakePhoto()
        }
    }

    private fun TakePhoto() {
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
                    TakePhotoMethod()
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

    private fun TakePhotoMethod() {
        var timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
       var imageFileName = "$timeStamp.jpg";
       var mainDir = File(  "/sdcard/Image/");
        if (!mainDir.exists() && !mainDir.isDirectory()) {
            mainDir.mkdirs();
        }else {
            mainDir = File( "/sdcard/Image/");
            if (!mainDir.exists() && !mainDir.isDirectory()) {
                mainDir.mkdirs();
            }
        }

        var image_output_File = File (mainDir, imageFileName);
        var pictureImagePath = mainDir.absolutePath + "/" + imageFileName;
        if (!image_output_File.exists()) {

                image_output_File.createNewFile();

        }
        var file =  File(pictureImagePath);

        outputFileUri = Uri.fromFile(file);

        var cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent,REQUEST_CAMERA);


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                          Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                        CheckStoragePermission()

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }   2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted for Storage", Toast.LENGTH_SHORT).show()
                   TakePhoto()
                } else {
                    Toast.makeText(this, "Permission Denied for Storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                productImageView.setImageURI(outputFileUri)


            } else if (requestCode == SELECT_PICTURE) {
                val selectedImageUri = data?.data
                outputFileUri= selectedImageUri!!
                productImageView.setImageURI(selectedImageUri)
            }
        }
    }

    fun getPath(uri: Uri?, activity: Activity): String {
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor = activity
            .managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
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

                    itemslist = response.body()
                    Log.e("Itemlist", itemslist.size.toString() + " error")
                    showItemNameDialog()
                } else {

                    progressDialog.dismiss()
                    Toast.makeText(this@AddItemActivity, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ArrayList<ItemsModel>>, t: Throwable) {
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
        productDialog = Dialog(this)
        productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        productDialog.setCancelable(true)
        productDialog.setContentView(R.layout.custom_layout)
        recyclerview = productDialog.findViewById(R.id.recyclerview)
        search = productDialog.findViewById(R.id.search)
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter.filter.filter(charSequence.toString())

            }

            override fun afterTextChanged(editable: Editable) {}
        })
        adapter = Product_Adapter(itemslist, this)
        recyclerview.adapter = adapter


        val metrics = DisplayMetrics()

        this!!.windowManager.defaultDisplay.getMetrics(metrics)
        val height = (metrics.heightPixels * 0.5)

        val width = (metrics.widthPixels * 0.9)

        productDialog.window!!.setLayout(width.roundToInt(), height.roundToInt())
        productDialog.show()

    }

    fun productSetUp(itemsModel: ItemsModel) {
        if (productDialog.isShowing){
            productDialog.dismiss()
            edtCategory.setText(itemsModel.name)
        }
    }
}
