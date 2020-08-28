package com.example.bakkeryApp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.R
import com.example.bakkeryApp.ViewStockDetails
import com.example.bakkeryApp.model.StockModel
import kotlinx.android.synthetic.main.itemsview_row.view.name_text
import kotlinx.android.synthetic.main.itemsview_row.view.units_text
import kotlinx.android.synthetic.main.stock_item_row.view.*

class StockAdapter(
    private var stockList: ArrayList<StockModel>,
    private val homeActivity: FragmentActivity?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var filterStockList = ArrayList<StockModel>()

    var mContext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        filterStockList = stockList
        mContext= this.homeActivity!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.stock_item_row, parent, false)
        val sch = CountryHolder(countryListView)
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return filterStockList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (filterStockList[position].item==null && filterStockList[position].shop==null){
            holder.itemView.name_text.text = "Item Name :  No values"
        }
        if (filterStockList[position].item!=null)
            holder.itemView.name_text.text = "Item Name :  "+filterStockList[position].item

        if (filterStockList[position].shop!=null)
            holder.itemView.name_text.text = "Shop Name :  "+filterStockList[position].shop

        holder.itemView.stockId.text = "ID: " + filterStockList[position].id.toString()

        if(null != filterStockList[position].name)
            holder.itemView.stockName.text = "Stock Name: " + filterStockList[position].name
        else
            holder.itemView.stockName.text = "Stock Name: ";


//        holder.itemView.setOnClickListener{
//            val intent= Intent(mContext, ViewStockDetails::class.java)
//            intent.putExtra("ItemId",filterStockList[position].id);
//
//            if (filterStockList[position].item!=null)
//                intent.putExtra("stockBy", "ByItem");
//            if (filterStockList[position].shop!=null)
//                intent.putExtra("stockBy", "ByLocation");
//
//            mContext.startActivity(intent)
//        }
    }
}