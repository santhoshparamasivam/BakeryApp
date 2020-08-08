package com.example.bakkeryApp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.HomeActivity
import com.example.bakkeryApp.model.ShopModel
import com.example.bakkeryApp.R
import kotlinx.android.synthetic.main.recyclerview_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class Store_Adapter(
    var ItemList: ArrayList<ShopModel>,
    val homeActivity: HomeActivity
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var FilterList = ArrayList<ShopModel>()

    var mcontext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        FilterList = ItemList
        mcontext=homeActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)
        val sch = CountryHolder(countryListView)
        mcontext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return FilterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.select_country_container.setBackgroundColor(Color.TRANSPARENT)

        holder.itemView.select_country_text.setTextColor(Color.WHITE)
        holder.itemView.select_country_text.text = FilterList[position].name+" - "+FilterList[position].location

        holder.itemView.select_country_text.setOnClickListener {

//            homeActivity.StoreSetUp(FilterList[position])

        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    FilterList = ItemList
                } else {
                    val resultList =  ArrayList<ShopModel>()
                    for (row in ItemList) {
                        if (row.name?.toLowerCase(Locale.ROOT)!!.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    FilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = FilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                FilterList = results?.values as  ArrayList<ShopModel>
                notifyDataSetChanged()
            }

        }
    }
}