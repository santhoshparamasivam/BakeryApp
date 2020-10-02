package com.cbe.bakery.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoveMultiStockAdd {
    constructor(locate: String, size: String,availability :String,price:String)
    constructor()


    @SerializedName("location")
    @Expose
    var location: String? = null


    @SerializedName("quantity")
    @Expose
    var quantity: String? = null

    @SerializedName("Availability")
    @Expose
    var availability: String? = null

    @SerializedName("Price")
    @Expose
    var price: String? = null

}