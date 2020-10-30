package com.cbe.bakery

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.cbe.bakery.createpdf.PDFUtility
import com.cbe.bakery.model.AvailibitySummary
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.ViewUtils
import com.google.gson.JsonObject
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import com.cbe.bakery.createpdf.PDFUtility.OnDocumentClose

class SummaryViewActivity : AppCompatActivity(), OnDocumentClose {
    lateinit var progressDialog: ProgressDialog
    lateinit var exportDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    private lateinit var viewUtils: ViewUtils
    lateinit var edtItem: EditText
    lateinit var edtlocation: EditText
    lateinit var shopTxt: TextView
    lateinit var itemTxt: TextView
    lateinit var dateTxt: TextView
    lateinit var quantityTxt: TextView
    var shopId: Long = 0L
    var itemId: Long = 0L
    lateinit var toolbar: Toolbar
    lateinit var export:ImageView
    lateinit var pdfDoc:PdfDocument
    lateinit var summaryList: ArrayList<AvailibitySummary>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_view)
        progressDialog = ProgressDialog(this)
        viewUtils = ViewUtils()
        sessionManager =
            SessionManager(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title ="Availability"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        export=findViewById(R.id.export)
        shopTxt = findViewById(R.id.shopTxt)
        itemTxt = findViewById(R.id.itemTxt)
        dateTxt = findViewById(R.id.dateTxt)
        quantityTxt = findViewById(R.id.quantityTxt)
        var intent=intent
        if (intent!=null) {
            shopId = intent.getLongExtra("shopId", 0)
            itemId = intent.getLongExtra("itemId", 0)
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
        exportDialog = ProgressDialog(this)
        exportDialog.setMessage("Exporting data please wait....")
        exportDialog.show()
        val path = Environment.getExternalStorageDirectory().toString() + "/Availability.pdf"
        try {
            PDFUtility.createPdf(applicationContext, this@SummaryViewActivity, sampleData, path, true)
        } catch (e: Exception) {
            exportDialog.dismiss()
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
            },"Cancel",

            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()

            },true
        )

    }

//        @RequiresApi(Build.VERSION_CODES.KITKAT)
//    fun createPdf() {
//
//        // create a new document
//        val document = PdfDocument()
//
//        // crate a page description
//        var pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
//
//        // start a page
//        var page: PdfDocument.Page = document.startPage(pageInfo)
//        var canvas = page.canvas
//        var paint = Paint()
////        paint.color = Color.RED
////        canvas.drawCircle(50F, 50F, 30F, paint)
//        paint.color = Color.BLACK
//        canvas.drawText(shopTxt.text.toString(), 80F, 50F, paint)
//
//        //canvas.drawt
//        // finish the page
//        document.finishPage(page)
//        // draw text on the graphics object of the page
//
////        // Create Page 2
////        pageInfo = PdfDocument.PageInfo.Builder(300, 600, 2).create()
////        page = document.startPage(pageInfo)
////        canvas = page.canvas
////        paint = Paint()
////        paint.color = Color.BLUE
////        canvas.drawCircle(100F, 100F, 100F, paint)
////        document.finishPage(page)
//
//        // write the document content
//        val directory_path = Environment.getExternalStorageDirectory().path + "/mypdf/"
//        val file = File(directory_path)
//        if (!file.exists()) {
//            file.mkdirs()
//        }
//        val targetPdf = directory_path + "Availability.pdf"
//        val filePath = File(targetPdf)
//        try {
//            document.writeTo(FileOutputStream(filePath))
//            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
//        } catch (e: IOException) {
//            Log.e("main", "error " + e.toString())
//            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show()
//        }
//
//        // close the document
//        document.close()
//        //isPrinting = false
//    }
    private fun showSummary() {
        var objects = JsonObject()
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
        requestInterface.getAvailabilitySummary(objects).enqueue(object :
            Callback<AvailibitySummary> {
            override fun onResponse(
                call: Call<AvailibitySummary>,
                response: Response<AvailibitySummary>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    summaryList= ArrayList<AvailibitySummary>()
                    summaryList.add(response.body())
                    shopTxt.setText(response.body().shopName)
                    itemTxt.setText(response.body().itemName)
                    dateTxt.setText(response.body().quantity.toString())
                    quantityTxt.setText(viewUtils.convertLongToTime(response.body().modifiedOn))

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@SummaryViewActivity,
                        "Please Check Store name and try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

            override fun onFailure(call: Call<AvailibitySummary>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                Toast.makeText(
                    this@SummaryViewActivity,
                    "Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

    }

    private val sampleData: List<Array<String>>
        private get() {
            var count = 20
            if (summaryList.size!=0) {
                count = summaryList.size
            }
            val temp: MutableList<Array<String>> = ArrayList()
            for (i in 0 until count) {
                temp.add(arrayOf(summaryList.get(i).shopName, summaryList.get(i).itemName , summaryList.get(i).quantity.toString() , viewUtils.convertLongToTime(summaryList.get(i).modifiedOn)))
            }
            return temp
        }
    override fun onPDFDocumentClose(file: File?) {
        exportDialog.dismiss()
        Toast.makeText(this, " Pdf Created", Toast.LENGTH_SHORT).show()
    }
}