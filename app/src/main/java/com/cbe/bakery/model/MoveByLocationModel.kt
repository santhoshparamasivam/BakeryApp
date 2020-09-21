package com.cbe.bakery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoveByLocationModel {
    @SerializedName("fromShopId")
    @Expose
    var fromShopId: Int? = null

    @SerializedName("fromShopName")
    @Expose
    var fromShopName: String? = null

    @SerializedName("toShopId")
    @Expose
    var toShopId: Int? = null

    @SerializedName("toShopName")
    @Expose
    var toShopName: String? = null

    @SerializedName("stock")
    @Expose
    var stock: List<Stock>? = null

    class Stock {
        @SerializedName("itemId")
        @Expose
        var itemId: Int? = null

        @SerializedName("itemName")
        @Expose
        var itemName: String? = null

        @SerializedName("quantity")
        @Expose
        var quantity: Int? = null
    }
}