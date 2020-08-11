package com.example.bakkeryApp.model

import com.example.bakkeryApp.AddStockActivity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MultiStockAdd {
    constructor(s: String, s1: String)


    @SerializedName("location")
    @Expose
    var location: String? = null


    @SerializedName("quantity")
    @Expose
    var quantity: String? = null

}