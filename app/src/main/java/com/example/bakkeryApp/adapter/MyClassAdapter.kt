package com.example.bakkeryApp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.bakkeryApp.R
import com.example.bakkeryApp.model.ItemsModel
import java.util.*

class MyClassAdapter(
    dataSet: List<ItemsModel>,
    mContext: Context
) : ArrayAdapter<ItemsModel?>(mContext, R.layout.recyclerview_row, dataSet) {

    private class ViewHolder {
        var txtName: TextView? = null
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        // Get the data item for this position
        var convertView = convertView
        val dataModel = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view

        val viewHolder: ViewHolder // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.recyclerview_row, parent, false)
            viewHolder.txtName = convertView!!.findViewById<View>(R.id.select_country_text) as TextView
            convertView.tag = viewHolder
            viewHolder.txtName!!.text=dataModel.toString();
        } else {
            viewHolder =
                convertView.tag as ViewHolder
        }
        return convertView!!
    }

}