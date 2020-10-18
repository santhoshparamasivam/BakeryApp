package com.cbe.bakery.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.R
import com.cbe.bakery.model.StockModel
import com.cbe.bakery.model.SummaryModel
import com.cbe.bakery.utils.ViewUtils
import kotlinx.android.synthetic.main.itemsview_row.view.name_text
import kotlinx.android.synthetic.main.stock_item_row.view.*
import kotlinx.android.synthetic.main.stock_item_row.view.lay_container
import kotlinx.android.synthetic.main.stock_item_row.view.stockName
import kotlinx.android.synthetic.main.summary_item.view.*


class SummaryAdapter(
    private var stockList: ArrayList<SummaryModel>,
    private val homeActivity: FragmentActivity?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var filterStockList = ArrayList<SummaryModel>()
    lateinit var viewUtils: ViewUtils
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (filterStockList[position].action!=null)
//        {
            holder.itemView.actionTxt.text = "Action :  " + filterStockList[position].action
//           holder.itemView.lay_container.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.byItem));

//        }
        if (filterStockList[position].shopName!=null)
//        {
            holder.itemView.shopTxt.text = "Shop Name :  " + filterStockList[position].shopName
//            holder.itemView.lay_container.setCardBackgroundColor(R.color.colorAccent)
//            holder.itemView.lay_container.setBackgroundColor(R.color.colorAccent);
//            holder.itemView.lay_container.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.byLocation));
//        }
//        holder.itemView.stockId.text = "ID: " + filterStockList[position].id.toString()

        if(null != filterStockList[position].itemName)
//        {
            holder.itemView.itemTxt.text = "Item Name: " + filterStockList[position].itemName

        if(null != filterStockList[position].modifiedOn)
//        {
            holder.itemView.dateTxt.text = "Date : " +viewUtils.convertLongToTime(filterStockList[position].modifiedOn)
//        }else {
//            holder.itemView.stockName.text = "Stock Name: ";
//        }
    }
}