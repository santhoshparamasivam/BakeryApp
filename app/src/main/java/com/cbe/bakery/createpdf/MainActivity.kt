//package com.cbe.bakery.createpdf
//
//import android.os.Bundle
//import android.os.Environment
//import android.text.TextUtils
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatButton
//import androidx.appcompat.widget.AppCompatEditText
//import com.cbe.bakery.R
//import com.cbe.bakery.createpdf.PDFUtility.OnDocumentClose
//import java.io.File
//import java.util.*
//
//class MainActivity : AppCompatActivity(), OnDocumentClose {
//    private var rowCount: AppCompatEditText? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        rowCount = findViewById(R.id.rowCount)
//        val button1 = findViewById<AppCompatButton>(R.id.button1)
//        button1.setOnClickListener { v ->
//            val path = Environment.getExternalStorageDirectory().toString() + "/Availability.pdf"
//            try {
//                PDFUtility.createPdf(v.context, this@MainActivity, sampleData, path, true)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e(TAG, "Error Creating Pdf")
//                Toast.makeText(v.context, "Error Creating Pdf", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private val sampleData: List<Array<String>>
//        private get() {
//            var count = 20
//            if (!TextUtils.isEmpty(rowCount!!.text)) {
//                count = rowCount!!.text.toString().toInt()
//            }
//            val temp: MutableList<Array<String>> = ArrayList()
//            for (i in 0 until count) {
//                temp.add(arrayOf("C1-R" + (i + 1), "C2-R" + (i + 1)))
//            }
//            return temp
//        }
//
//    companion object {
//        private val TAG = MainActivity::class.java.simpleName
//    }
//
//    override fun onPDFDocumentClose(file: File?) {
//        Toast.makeText(this, " Pdf Created", Toast.LENGTH_SHORT).show()
//    }
//}