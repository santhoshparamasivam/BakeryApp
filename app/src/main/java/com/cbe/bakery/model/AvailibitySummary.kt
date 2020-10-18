package com.cbe.bakery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AvailibitySummary {
    @SerializedName("modifiedOn")
    @Expose
    var modifiedOn: Long = 0L

    @SerializedName("shopName")
    @Expose
    var shopName: String? = null

    @SerializedName("itemName")
    @Expose
    var itemName: String? = null

    @SerializedName("shopId")
    @Expose
    var shopId: Long? = 0L

    @SerializedName("quantity")
    @Expose
    var quantity: Long? = 0L

    @SerializedName("itemId")
    @Expose
    var itemId: Long? = 0L
}