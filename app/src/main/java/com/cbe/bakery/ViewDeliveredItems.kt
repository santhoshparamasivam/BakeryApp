package com.cbe.bakery

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.model.OrdersModel
import com.cbe.bakery.utils.RecyclerItemClickListener
import com.cbe.bakery.adapter.OrdersAdapter
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.R

class ViewDeliveredItems : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    lateinit var recyclerview: RecyclerView
    var ordersList: ArrayList<OrdersModel.Datum> = ArrayList()
    lateinit var ordersAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_delivered_items)
        recyclerview=findViewById(R.id.recyclerview)
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)

        recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                object :
                    RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val intent = Intent(
                            applicationContext,
                            ViewSingleItem::class.java
                        )
                        intent.putExtra("itemId", ordersList[position].itemId)
                        intent.putExtra("shopId", ordersList[position].shopId)
                        startActivity(intent)
                    }
                })
        )
    }
}