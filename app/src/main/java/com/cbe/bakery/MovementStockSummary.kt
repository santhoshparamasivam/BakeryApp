package com.cbe.bakery

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.adapter.SummaryAdapter
import com.cbe.bakery.createpdf.PDFUtility
import com.cbe.bakery.model.SummaryModel
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.ViewUtils
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MovementStockSummary : AppCompatActivity(), PDFUtility.OnDocumentClose {
    var shopId: Long = 0L
    var itemId: Long = 0L
     lateinit var transaction: String
     lateinit var fromDate: String
     lateinit var toDate: String
    lateinit var summaryAdapter: SummaryAdapter
    lateinit var toolbar: Toolbar
    lateinit var export: ImageView
    lateinit var summaryRecycler: RecyclerView
    lateinit var progressDialog: ProgressDialog
    private lateinit var  viewUtils: ViewUtils
    lateinit var sessionManager: SessionManager
    lateinit var exportDialog: ProgressDialog
    lateinit var noDataTxt: TextView
    var FILEEXTENSION = ".pdf"
    var summaryList: ArrayList<SummaryModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movement_stock_summary)
        summaryRecycler=findViewById(R.id.summaryRecycler)
        noDataTxt=findViewById(R.id.noDataTxt)
        progressDialog = ProgressDialog(this)
        viewUtils= ViewUtils()
        sessionManager =
            SessionManager(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        supportActionBar?.title ="Movement"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        export=findViewById(R.id.export)
        export.visibility=View.GONE
        var intent=intent
        if (intent!=null) {
            shopId = intent.getLongExtra("shopId", 0)
            itemId = intent.getLongExtra("itemId", 0)
            fromDate = intent.getStringExtra("fromDate")
            toDate = intent.getStringExtra("toDate")
            transaction = intent.getStringExtra("transaction")
        }
        showSummary()
        export.setOnClickListener {
//            checkRunTimePermission()
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
            )
        } else {
            createPdf()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {

            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    createPdf()
                } else {
                    var details = "Please Allow Storage Permission For Store and Retrieve Data"

                    permissionDeniedAlertBox(details)
                }
                return
            }
        }
    }

    private fun createPdf() {

        val path = Environment.getExternalStorageDirectory().toString() + "/Availability"
        val df: DateFormat = SimpleDateFormat("yyyyMMddhhmmss")
        val filename: String = path + df.format(Date()).toString() + FILEEXTENSION
        Log.e("filename", filename+" ")
        try {
            PDFUtility.createPdf(
                applicationContext,
                this@MovementStockSummary,
                sampleData,
                filename,
                true
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error Creating Pdf")
            Toast.makeText(applicationContext, "Error Creating Pdf", Toast.LENGTH_SHORT).show()
        }

    }

    private fun permissionDeniedAlertBox(details: String) {
        viewUtils.bakeryAlert(
            this,
            details,
            "Okay",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                checkStoragePermission()
            }, "Cancel",

            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()

            }, true
        )

    }

    private val sampleData: List<Array<String>>
        private get() {
            var count = 20
            if (summaryList.size!=0) {
                count = summaryList.size
            }
            val temp: MutableList<Array<String>> = java.util.ArrayList()
            for (i in 0 until count) {
                temp.add(
                    arrayOf(
                        summaryList.get(i).shopName,
                        summaryList.get(i).itemName,
                        summaryList.get(
                            i
                        ).quantity.toString(),
                        viewUtils.convertLongToTime(summaryList.get(i).modifiedOn)
                    )
                )
            }
            return temp
        }

    override fun onPDFDocumentClose(file: File?) {
        viewUtils.bakeryAlert(
            this,
            "Pdf exported,You want to open?",
            "yes",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                openPdf(file)
            }, "no",

            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()

            }, true
        )

    }

    private fun openPdf(file: File?) {
//        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/example.pdf")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)

    }
    private fun showSummary() {
        var objects= JsonObject()
        objects.addProperty("fromDate", fromDate)
        objects.addProperty("toDate", toDate)
        objects.addProperty("transaction", transaction)
        objects.addProperty("shopId", shopId)
        objects.addProperty("itemId", itemId)
        Log.e("objects", "$objects  ")

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
            .baseUrl(ApiManager.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        requestInterface.getSummary(objects).enqueue(object : Callback<ArrayList<SummaryModel>> {
            override fun onResponse(
                call: Call<ArrayList<SummaryModel>>,
                response: Response<ArrayList<SummaryModel>>
            ) {
                progressDialog.dismiss()
                if (response != null)
                    if (response.code() == 200) {
                        summaryList = response.body()
                        if (summaryList.size <= 0 && summaryList.size == 0) {
                            noDataTxt.visibility = View.VISIBLE
                            export.visibility = View.GONE
                        } else {
                            export.visibility = View.VISIBLE
                            noDataTxt.visibility = View.GONE
                            summaryRecycler.layoutManager =
                                LinearLayoutManager(summaryRecycler.context)
                            summaryRecycler.setHasFixedSize(true)
                            summaryAdapter =
                                SummaryAdapter(summaryList, this@MovementStockSummary)
                            summaryRecycler.adapter = summaryAdapter
                        }
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@MovementStockSummary,
                            "Please Check Store name and try again later",
                            Toast.LENGTH_LONG
                        ).show()
                    }

            }

            override fun onFailure(call: Call<ArrayList<SummaryModel>>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                Toast.makeText(
                    this@MovementStockSummary,
                    "Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

    }
}