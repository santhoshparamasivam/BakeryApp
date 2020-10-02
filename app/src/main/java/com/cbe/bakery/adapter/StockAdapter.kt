package com.cbe.bakery.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.R
import com.cbe.bakery.model.StockModel
import kotlinx.android.synthetic.main.itemsview_row.view.name_text
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
        val sch =
            CountryHolder(countryListView)
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return filterStockList.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (filterStockList[position].item==null && filterStockList[position].shop==null){
            holder.itemView.name_text.text = "Item Name :  No values"
        }
        if (filterStockList[position].item!=null) {
            holder.itemView.name_text.text = "Item Name :  " + filterStockList[position].item
//            holder.itemView.lay_container.setCardBackgroundColor(R.color.background_color)
            holder.itemView.lay_container.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.byItem));

        }
        if (filterStockList[position].shop!=null) {
            holder.itemView.name_text.text = "Shop Name :  " + filterStockList[position].shop
//            holder.itemView.lay_container.setCardBackgroundColor(R.color.colorAccent)
//            holder.itemView.lay_container.setBackgroundColor(R.color.colorAccent);
            holder.itemView.lay_container.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.byLocation));

        }
        holder.itemView.stockId.text = "ID: " + filterStockList[position].id.toString()

        if(null != filterStockList[position].name) {
            holder.itemView.stockName.text = "Stock Name: " + filterStockList[position].name
        }else {
            holder.itemView.stockName.text = "Stock Name: ";
        }

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