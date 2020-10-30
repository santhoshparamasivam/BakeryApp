package com.cbe.bakery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SummaryModel {
    @SerializedName("action")
    @Expose
    var action: String? = null

    @SerializedName("itemName")
    @Expose
    var itemName: String = ""

    @SerializedName("shopName")
    @Expose
    var shopName: String = ""

    @SerializedName("hdrId")
    @Expose
    var hdrId = 0

    @SerializedName("shopId")
    @Expose
    var shopId = 0

    @SerializedName("itemId")
    @Expose
    var itemId = 0

    @SerializedName("dtlId")
    @Expose
    var dtlId = 0
    @SerializedName("quantity")
    @Expose
    var quantity = 0L
    @SerializedName("modifiedOn")
    @Expose
    var modifiedOn = 0L
}