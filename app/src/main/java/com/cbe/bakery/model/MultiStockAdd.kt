package com.cbe.bakery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MultiStockAdd {
    constructor(locate: String, size: String)
    constructor()


    @SerializedName("location")
    @Expose
    var location: String? = null


    @SerializedName("quantity")
    @Expose
    var quantity: String? = null

}