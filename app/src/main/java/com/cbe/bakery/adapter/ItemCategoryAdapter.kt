package com.cbe.bakery.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.model.ItemCategoryModel
import com.cbe.bakery.R
import kotlinx.android.synthetic.main.recyclerview_row.view.*
import java.util.*
import kotlin.collections.ArrayList


class ItemCategoryAdapter(
    var itemCategoryModelList: ArrayList<ItemCategoryModel>,
    private val homeActivity: Context

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var filterList = ArrayList<ItemCategoryModel>()

    var mContext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        filterList = itemCategoryModelList
        mContext= homeActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)
        val sch = CountryHolder(
            countryListView
        )
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.select_country_container.setBackgroundColor(Color.TRANSPARENT)

        holder.itemView.select_country_text.text = filterList[position].name

//        holder.itemView.select_country_text.setOnClickListener {
//
//            homeActivity.itemCategorySetUp(filterList[position])
//        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = itemCategoryModelList
                } else {
                    val resultList =  ArrayList<ItemCategoryModel>()
                    for (row in itemCategoryModelList) {
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
                filterList = results?.values as  ArrayList<ItemCategoryModel>
                notifyDataSetChanged()
            }

        }
    }

}


