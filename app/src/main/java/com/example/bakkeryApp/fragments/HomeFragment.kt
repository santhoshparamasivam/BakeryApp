package com.example.bakkeryApp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bakkeryApp.R

class HomeFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View= inflater.inflate(R.layout.fragment_home,container,false)
        activity?.title  ="DashBoard"


   return view }
}