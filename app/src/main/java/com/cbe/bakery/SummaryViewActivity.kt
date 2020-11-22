package com.cbe.bakery

import android.Manifest
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.adapter.AvailableSummaryAdapter
import com.cbe.bakery.createpdf.PDFUtility
import com.cbe.bakery.createpdf.PDFUtility.OnDocumentClose
import com.cbe.bakery.model.AvailibitySummary
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.ViewUtils
import com.google.gson.Gson
import com.google.gson.JsonElement
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

class SummaryViewActivity : AppCompatActivity(), OnDocumentClose {
    lateinit var progressDialog: ProgressDialog
    lateinit var exportDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    private lateinit var viewUtils: ViewUtils
    lateinit var edtItem: EditText
    lateinit var edtlocation: EditText
//    lateinit var shopTxt: TextView
//    lateinit var itemTxt: TextView
//    lateinit var dateTxt: TextView
//    lateinit var quantityTxt: TextView
    var shopId: Long = 0L
    var itemId: Long = 0L
    lateinit var toolbar: Toolbar
    lateinit var export:ImageView
    lateinit var pdfDoc:PdfDocument
    lateinit var noDataTxt:TextView
    private lateinit var mgr: DownloadManager
    private var lastDownload = -1L
    lateinit var availableRecycler:RecyclerView
//    lateinit var line1:LinearLayout
    var FILEEXTENSION = ".pdf"
    lateinit var summaryAdapter: AvailableSummaryAdapter
    lateinit var summaryList: ArrayList<AvailibitySummary>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_view)
        progressDialog = ProgressDialog(this)
        viewUtils = ViewUtils()
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        sessionManager =
            SessionManager(this)
        toolbar = findViewById(R.id.toolbar)
        availableRecycler = findViewById(R.id.availableRecycler)
        noDataTxt=findViewById(R.id.noDataTxt)
        setSupportActionBar(toolbar)
        supportActionBar?.title ="Availability"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        export=findViewById(R.id.export)
        export.visibility=View.GONE
//        shopTxt = findViewById(R.id.shopTxt)
//        itemTxt = findViewById(R.id.itemTxt)
//        dateTxt = findViewById(R.id.dateTxt)
//        quantityTxt = findViewById(R.id.quantityTxt)
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

        val path = Environment.getExternalStorageDirectory().toString() + "/Availability"
        val df: DateFormat = SimpleDateFormat("yyyyMMddhhmmss")
        val filename: String = path + df.format(Date()).toString() + "." + FILEEXTENSION
        Log.e("filename", filename+" ")
//        val filePath = Environment.getExternalStorageDirectory().toString() + "/Availability.pdf"
        try {
            PDFUtility.createPdf(
                applicationContext,
                this@SummaryViewActivity,
                sampleData,
                filename,
                true
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error Creating Pdf")
            Toast.makeText(applicationContext, "Error Creating Pdf", Toast.LENGTH_SHORT).show()
        }

//
//        exportDialog = ProgressDialog(this)
//        exportDialog.setMessage("Exporting data please wait....")
//        exportDialog.show()
//        val path = Environment.getExternalStorageDirectory().toString() + "/Availability.pdf"
//        try {
//            PDFUtility.createPdf(
//                applicationContext,
//                this@SummaryViewActivity,
//                sampleData,
//                path,
//                true
//            )
//        } catch (e: Exception) {
//            exportDialog.dismiss()
//            e.printStackTrace()
//            Log.e("TAG", "Error Creating Pdf")
//            Toast.makeText(applicationContext, "Error Creating Pdf", Toast.LENGTH_SHORT).show()
//        }

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
            Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    val gson = Gson()
                    if (response.body().isJsonObject) {
                        val availableResponse = gson.fromJson(
                            response.body(),
                            AvailibitySummary::class.java
                        )
                        summaryList = ArrayList<AvailibitySummary>()
                        summaryList.add(availableResponse)
                        if (summaryList.size == 0) {
//                                line1.visibility=View.GONE
                            export.visibility = View.GONE
                            availableRecycler.visibility = View.GONE
                            noDataTxt.visibility = View.VISIBLE
                        } else {
                            noDataTxt.visibility = View.GONE
                            availableRecycler.visibility = View.VISIBLE
//                                line1.visibility=View.VISIBLE
                            export.visibility = View.VISIBLE
//                                shopTxt.setText(availableResponse.shopName)
//                                itemTxt.setText(availableResponse.itemName)
//                                dateTxt.setText(availableResponse.quantity.toString())
//                                quantityTxt.setText(viewUtils.convertLongToTime(availableResponse.modifiedOn))
                            availableRecycler.layoutManager = LinearLayoutManager(
                                availableRecycler.context
                            )
                            availableRecycler.setHasFixedSize(true)
                            summaryAdapter = AvailableSummaryAdapter(
                                summaryList,
                                this@SummaryViewActivity
                            )
                            availableRecycler.adapter = summaryAdapter
                        }
                    } else if (response.body().isJsonArray) {
                        Log.e("isJsonArray", response.body().isJsonArray.toString() + " ")
                        summaryList = ArrayList<AvailibitySummary>()
                        for (responce in response.body().asJsonArray) {
                            Log.e("responce", responce.toString() + " ")
                            val availableResponse = gson.fromJson(
                                responce,
                                AvailibitySummary::class.java
                            )
                            summaryList.add(availableResponse)
                        }
                        Log.e("summaryList", summaryList.size.toString() + " ")
                        if (summaryList.size == 0) {
                            Log.e("summaryList 000000000", summaryList.size.toString() + " ")
//                                line1.visibility=View.GONE
                            export.visibility = View.GONE
                            availableRecycler.visibility = View.GONE
                            noDataTxt.visibility = View.VISIBLE
                        } else {
                            availableRecycler.visibility = View.VISIBLE
                            noDataTxt.visibility = View.GONE
//                                line1.visibility=View.GONE
                            export.visibility = View.VISIBLE
                            availableRecycler.layoutManager = LinearLayoutManager(
                                availableRecycler.context
                            )
                            availableRecycler.setHasFixedSize(true)
                            summaryAdapter = AvailableSummaryAdapter(
                                summaryList,
                                this@SummaryViewActivity
                            )
                            availableRecycler.adapter = summaryAdapter
                        }

                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@SummaryViewActivity,
                        "Please Check Store name and try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
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

//    private fun AddNotification(file: File?) {
//        val notificationId = 100
//        val chanelid = "chanelid"
//        val intent = Intent(this, MainActivity::class.java)
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // you must create a notification channel for API 26 and Above
//            val name = "my channel"
//            val description = "channel description"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(chanelid, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            val notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val mBuilder = NotificationCompat.Builder(this, chanelid)
//            .setSmallIcon(R.drawable.profile_icon)
//            .setContentTitle("Want to Open My App?")
//            .setContentText("Open my app and see good things")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true) // cancel the notification when clicked
//            .addAction(R.drawable.ic_down_arrow, "YES", pendingIntent) //add a btn to the Notification with a corresponding intent
//
//        val notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(notificationId, mBuilder.build());
//
//    }

}