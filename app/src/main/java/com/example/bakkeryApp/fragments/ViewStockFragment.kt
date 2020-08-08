package com.example.bakkeryApp.fragments
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.bakkeryApp.AddItemActivity
import com.example.bakkeryApp.R

class ViewStockFragment : Fragment(){
    lateinit var  create_item: ImageView
    lateinit var  view_item: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view :View= inflater.inflate(R.layout.fragment_view_stock,container,false)
        create_item=view.findViewById(R.id.create_item)
        view_item=view.findViewById(R.id.view_item)
        activity?.title  ="View Stock"
        create_item.setOnClickListener {
            val intent= Intent(activity, AddItemActivity::class.java)
            activity?.startActivity(intent)
        }
    return view}
}