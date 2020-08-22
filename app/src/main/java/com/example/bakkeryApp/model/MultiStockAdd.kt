package com.example.bakkeryApp.model

import com.example.bakkeryApp.AddStockActivity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MultiStockAdd {
    constructor(locate: String, size: String)


    @SerializedName("location")
    @Expose
    var location: String? = null


    @SerializedName("quantity")
    @Expose
    var quantity: String? = null

}