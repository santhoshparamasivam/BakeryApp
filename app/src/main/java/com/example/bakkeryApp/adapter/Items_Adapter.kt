package com.example.bakkeryApp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakkeryApp.R
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.retrofitService.ApiManager.Companion.BASE_URL
import kotlinx.android.synthetic.main.itemsview_row.view.*
import java.net.URL
import java.net.URLEncoder

class Items_Adapter(
    var ItemList: ArrayList<ItemsModel>,
    val homeActivity: FragmentActivity?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var FilterList = ArrayList<ItemsModel>()

    var mcontext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        FilterList = ItemList
        mcontext= this!!.homeActivity!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.itemsview_row, parent, false)
        val sch = CountryHolder(countryListView)
        mcontext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return FilterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val encodedURL: String = URLEncoder.encode(FilterList.get(position).imageFileName, "UTF-8")
        var uri= BASE_URL+"downloadfile/item/"+encodedURL
        val url = URL( uri)

        Log.e("Uri", "$uri  ")
//        if (FilterList.get(position).imageFileName!=null) {
//            Glide.with(mcontext)
//                .load( uri)
//                .into(holder.itemView.imageView);
//        }
        holder.itemView.name_text.setText("ITEM NAME :  "+FilterList.get(position).name)
        holder.itemView.units_text.setText("UNITS :  "+FilterList.get(position).unitOfMeasure)
        holder.itemView.sale_text.setText("Sale Text :  "+FilterList.get(position).sellingPrice)

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