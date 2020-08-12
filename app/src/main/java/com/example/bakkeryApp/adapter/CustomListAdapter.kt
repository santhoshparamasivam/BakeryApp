package com.example.bakkeryApp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.bakkeryApp.R
import com.example.bakkeryApp.model.ItemsModel
import java.util.*

class CustomListAdapter(
    //context
    private val context: Context,
    private val items: ArrayList<ItemsModel>
) : BaseAdapter(), Filterable {
    private var Filteritems
            : ArrayList<ItemsModel> = items

    override fun getCount(): Int {
        return Filteritems.size //returns total of items in the list
    }

    override fun getItem(position: Int): Any {
        return Filteritems[position] //returns list item at the specified position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        // inflate the layout for each list row
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.recyclerview_row, parent, false)
        }
        val textViewItemName =
            convertView?.findViewById<TextView>(R.id.select_country_text)
        textViewItemName!!.text = Filteritems[position].name
        return convertView!!
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    Filteritems = items
                } else {
                    val resultList = ArrayList<ItemsModel>()
                    for (row in items) {
                        if (row.name?.toLowerCase(Locale.ROOT)!!
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    Filteritems = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = Filteritems
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                Filteritems = results?.values as ArrayList<ItemsModel>
                notifyDataSetChanged()
            }
        }

    }
}