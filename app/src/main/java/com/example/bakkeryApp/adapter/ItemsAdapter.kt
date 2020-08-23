package com.example.bakkeryApp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakkeryApp.R
import com.example.bakkeryApp.ViewSingleItem
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.retrofitService.ApiManager.Companion.BASE_URL
import kotlinx.android.synthetic.main.itemsview_row.view.*
import java.net.URLEncoder
import kotlin.collections.ArrayList


class ItemsAdapter(
    var itemList: ArrayList<ItemsModel>,
    private val homeActivity: FragmentActivity?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var finalList = ArrayList<ItemsModel>()

    var mContext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        finalList = itemList
        mContext= this.homeActivity!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.itemsview_row, parent, false)
        val sch = CountryHolder(countryListView)
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return finalList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val encodedURL: String = URLEncoder.encode(finalList[position].imageFileName, "UTF-8").replace("+", "%20")
        var uri= BASE_URL+"downloadfile/item/"+encodedURL

        Glide.with(mContext).load( uri).into(holder.itemView.imageView)

        holder.itemView.name_text.text = "Item :  "+finalList[position].name
        holder.itemView.units_text.text = "Units :  "+finalList[position].unitOfMeasure
        holder.itemView.sale_text.text = "Sale Price :  "+finalList[position].sellingPrice+" Rs"

        holder.itemView.layout_container.setOnClickListener{
            val intent = Intent(mContext, ViewSingleItem::class.java)
            intent.putExtra("id", finalList[position].id)
            mContext.startActivity(intent)
        }
    }


//    fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val charSearch = constraint.toString()
//                if (charSearch.isEmpty()) {
//                    finalList =  itemList
//                } else {
//                    val resultList = ArrayList<ItemsModel>()
//                    for (row in itemList) {
//                        if (row.name.toString().toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)) ||row.itemCategory.toString().toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
//                            resultList.add(row)
//                        }
//                    }
//                    finalList = resultList
//                }
//                val filterResults = FilterResults()
//                filterResults.values = finalList
//                return filterResults
//            }
//
//            @Suppress("UNCHECKED_CAST")
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                finalList = results?.values as  ArrayList<ItemsModel>
//                notifyDataSetChanged()
//            }
//
//        }
//    }

}