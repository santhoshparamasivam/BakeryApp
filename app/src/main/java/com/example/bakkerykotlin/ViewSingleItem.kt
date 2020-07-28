package com.example.bakkerykotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_view_single_item.*

class ViewSingleItem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_item)
        val intent: Intent = intent
        var itemId = intent.getStringExtra("itemId")
        val shopId = intent.getStringExtra("shopId")

        txt_itemId.text = "Item Id $itemId  Shop Id$shopId"
    }
}