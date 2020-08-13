package com.example.bakkeryApp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.model.ItemCategoryModel
import com.example.bakkeryApp.AddItemActivity
import com.example.bakkeryApp.R
import kotlinx.android.synthetic.main.recyclerview_row.view.*
import java.util.*
import kotlin.collections.ArrayList


class ItemCategoryAdapter(
    var itemCategoryModelList: ArrayList<ItemCategoryModel>,
    private val homeActivity: AddItemActivity

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var FilterList = ArrayList<ItemCategoryModel>()

    var mcontext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        FilterList = itemCategoryModelList
        mcontext= homeActivity
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

        holder.itemView.select_country_text.text = FilterList[position].name

        holder.itemView.select_country_text.setOnClickListener {

            homeActivity.itemCategorySetUp(FilterList[position])
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    FilterList = itemCategoryModelList
                } else {
                    val resultList =  ArrayList<ItemCategoryModel>()
                    for (row in itemCategoryModelList) {
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
                FilterList = results?.values as  ArrayList<ItemCategoryModel>
                notifyDataSetChanged()
            }

        }
    }

}


