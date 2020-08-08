package com.example.bakkeryApp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.bakkeryApp.AddItemActivity
import com.example.bakkeryApp.R
import com.example.bakkeryApp.ViewSingleItem

class ViewItemsFragment : Fragment() {
    lateinit var  create_item:ImageView
    lateinit var  view_item:ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View= inflater.inflate(R.layout.fragment_view_items,container,false)
        create_item=view.findViewById(R.id.create_item)
        view_item=view.findViewById(R.id.view_item)
        activity?.title  ="View Item"
        create_item.setOnClickListener {
            val intent= Intent(activity,AddItemActivity::class.java)
            activity?.startActivity(intent)
        }

        view_item.setOnClickListener {
            val intent= Intent(activity,ViewSingleItem::class.java)
            activity?.startActivity(intent)
        }
      return view
    }
}