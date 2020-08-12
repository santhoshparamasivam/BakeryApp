package com.example.bakkeryApp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.R
import com.example.bakkeryApp.ViewSingleItem
import com.example.bakkeryApp.model.ItemHistoryModel
import kotlinx.android.synthetic.main.price_history_recyclerview_row.view.*
import kotlinx.android.synthetic.main.recyclerview_row.view.*
import kotlinx.android.synthetic.main.recyclerview_row.view.select_country_container
import java.util.*
import kotlin.collections.ArrayList


class PriceHistoryAdapter(
    var itemHistList: ArrayList<ItemHistoryModel>,
    private val homeActivity: ViewSingleItem

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var filterList = ArrayList<ItemHistoryModel>()

    var mcontext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        filterList = itemHistList
        mcontext= homeActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.price_history_recyclerview_row, parent, false)
        val sch = CountryHolder(countryListView)
        mcontext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.costPrice.text = "Cost Price: " + filterList[position].costPrice.toString()
        holder.itemView.sellingPrice.text = "Selling Price: " + filterList[position].sellingPrice.toString()
        holder.itemView.modifiedOn.text = "Modified On: " + filterList[position].modifiedOn.toString()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    this@PriceHistoryAdapter.filterList = itemHistList
                } else {
                    val resultList =  ArrayList<ItemHistoryModel>()
                    for (row in itemHistList) {
                        if (row.name?.toLowerCase(Locale.ROOT)!!.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    filterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as  ArrayList<ItemHistoryModel>
                notifyDataSetChanged()
            }

        }
    }

}


