package com.example.bakkerykotlin.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrdersModel {
    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    class Datum {
        @SerializedName("itemId")
        @Expose
        var itemId: String? = null

        @SerializedName("modifiedOn")
        @Expose
        var modifiedOn: String? = null

        @SerializedName("quantity")
        @Expose
        var quantity: Int? = null

        @SerializedName("delivered")
        @Expose
        var delivered: Boolean? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("shopId")
        @Expose
        var shopId: String? = null

        @SerializedName("deliveryDate")
        @Expose
        var deliveryDate: String? = null

        @SerializedName("createdOn")
        @Expose
        var createdOn: String? = null

    }
}