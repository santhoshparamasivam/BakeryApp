package com.cbe.bakery.adapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.cbe.bakery.R
import com.cbe.bakery.model.MoveStockModel
import com.cbe.bakery.utils.ViewUtils
import kotlinx.android.synthetic.main.itemsview_row.view.name_text
import kotlinx.android.synthetic.main.stock_item_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class MoveStockAdapter(
    var ItemList: ArrayList<MoveStockModel>,
    homeActivity: FragmentActivity?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var filterList = ArrayList<MoveStockModel>()

    lateinit var mContext: Context
    var viewUtils: ViewUtils
    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        filterList = ItemList
        viewUtils= ViewUtils()
        if (homeActivity != null) {
            mContext=homeActivity
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.stock_item_row, parent, false)
        val sch =
            CountryHolder(countryListView)
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.imageView.visibility=View.GONE
//        if (filterList[position].item==null && filterList[position].shop==null){
//            holder.itemView.name_text.text = "Item Name :  No values"
//        }
        if (filterList[position].item!=null) {
            holder.itemView.txt_field_name.text = "Item Name"
            holder.itemView.nameTxt.text = filterList[position].item.toString()
//            holder.itemView.lay_container.setBackgroundColor(R.color.background_color);
             }
        if (filterList[position].shop!=null) {
            holder.itemView.txt_field_name.text = "Shop Name"
            holder.itemView.nameTxt.text = filterList[position].shop
//            holder.itemView.lay_container.setBackgroundColor(R.color.colorAccent);
//            holder.itemView.lay_container.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorAccent));

        }

            if(filterList[position].action=="MoveByLocation"){
                holder.itemView.lay_container.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.byItem));

            }else{
                holder.itemView.lay_container.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.byLocation));

            }


        holder.itemView.transTxt.text = filterList[position].transId.toString()

        if(null != filterList[position].createdOn) {
            holder.itemView.txtNameDate.text="Created On"
            holder.itemView.dateTxt.text =
                 viewUtils.convertLongToTime(filterList[position].createdOn!!)
        }
//        else {
//            holder.itemView.stockName.text = "Created On: "
//        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = ItemList
                } else {
                    val resultList =  ArrayList<MoveStockModel>()
                    for (row in ItemList) {
                        if (row.shop?.toLowerCase(Locale.ROOT)!!.contains(charSearch.toLowerCase(Locale.ROOT))) {
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
                filterList = results?.values as  ArrayList<MoveStockModel>
                notifyDataSetChanged()
            }

        }
    }
}