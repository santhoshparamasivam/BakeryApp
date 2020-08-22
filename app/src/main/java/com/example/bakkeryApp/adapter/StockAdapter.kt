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
import com.example.bakkeryApp.ViewSingleItem
import com.example.bakkeryApp.ViewStockDetails
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.model.StockModel
import com.example.bakkeryApp.retrofitService.ApiManager.Companion.BASE_URL
import kotlinx.android.synthetic.main.itemsview_row.view.*
import kotlinx.android.synthetic.main.itemsview_row.view.layout_container
import kotlinx.android.synthetic.main.itemsview_row.view.name_text
import kotlinx.android.synthetic.main.itemsview_row.view.units_text
import kotlinx.android.synthetic.main.stock_item_row.view.*
import java.net.URL
import java.net.URLEncoder

class StockAdapter(
    var stockList: ArrayList<StockModel>,
    val homeActivity: FragmentActivity?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var filterstockList = ArrayList<StockModel>()

    var mcontext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        filterstockList = stockList
        mcontext= this.homeActivity!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.stock_item_row, parent, false)
        val sch = CountryHolder(countryListView)
        mcontext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return filterstockList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

//        val encodedURL: String = URLEncoder.encode(filterstockList.get(position).imageFileName, "UTF-8")
//        var uri= BASE_URL+"downloadfile/item/"+encodedURL
//        val url = URL( uri)

//        Log.e("Uri", "$uri  ")
//        if (FilterList.get(position).imageFileName!=null) {
//            Glide.with(mcontext)
//                .load( uri)
//                .into(holder.itemView.imageView);
//        }
        if (filterstockList[position].item==null && filterstockList[position].shop==null){
//            holder.itemView.lay_container.visibility=View.GONE
            holder.itemView.name_text.text = "Item Name :  No values"
        }
        if (filterstockList[position].item!=null)
            holder.itemView.name_text.text = "Item Name :  "+filterstockList.get(position).item
        else
            holder.itemView.name_text.visibility = View.GONE

        if (filterstockList[position].shop!=null)
            holder.itemView.units_text.text = "Shop Name :  "+filterstockList.get(position).shop
        else
            holder.itemView.units_text.visibility = View.GONE

    Log.e("ItemId",filterstockList.get(position).itemId.toString()+" ")


        holder.itemView.setOnClickListener{
            val intent= Intent(mcontext, ViewStockDetails::class.java)
            intent.putExtra("ItemId",filterstockList.get(position).id);
            mcontext.startActivity(intent)

        }



    }


//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val charSearch = constraint.toString()
//                if (charSearch.isEmpty()) {
//                    FilterList = ItemList
//                } else {
//                    val resultList = ArrayList<ItemsModel>()
//                    for (row in ItemList) {
//                        if (row.shopId.toString().toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)) ||row.itemId.toString().toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
//                            resultList.add(row)
//                        }
//                    }
//                    FilterList = resultList
//                }
//                val filterResults = FilterResults()
//                filterResults.values = FilterList
//                return filterResults
//            }
//
//            @Suppress("UNCHECKED_CAST")
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                FilterList = results?.values as  ArrayList<OrdersModel.Datum>
//                notifyDataSetChanged()
//            }
//
//        }
//    }

}