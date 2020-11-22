package com.cbe.bakery.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.R
import com.cbe.bakery.model.AvailibitySummary
import com.cbe.bakery.utils.ViewUtils
import kotlinx.android.synthetic.main.summary_item.view.*

class AvailableSummaryAdapter (
    stockList: ArrayList<AvailibitySummary>,
    private val homeActivity: FragmentActivity?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var filterStockList = ArrayList<AvailibitySummary>()
    var viewUtils: ViewUtils
    var mContext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        filterStockList = stockList
        viewUtils= ViewUtils()
        mContext= this.homeActivity!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.summary_item, parent, false)
        val sch =
            CountryHolder(countryListView)
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return filterStockList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Log.e("onBindViewHolder", filterStockList[position].shopName+" "+filterStockList[position].itemName)
        holder.itemView.actionTxt.visibility= View.GONE

        if (filterStockList[position].shopName!=null)
            holder.itemView.shopTxt.text = "Shop Name :  " + filterStockList[position].shopName

        if(null != filterStockList[position].itemName)
            holder.itemView.itemTxt.text = "Item Name: " + filterStockList[position].itemName

        if(null != filterStockList[position].modifiedOn)
            holder.itemView.dateTxt.text = "Date : " +viewUtils.convertLongToTime(filterStockList[position].modifiedOn)

    }
}