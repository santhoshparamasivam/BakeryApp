package com.cbe.bakery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cbe.bakery.R

class HomeFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View= inflater.inflate(R.layout.fragment_home,container,false)
        activity?.title  ="Dashboard"


   return view }
}