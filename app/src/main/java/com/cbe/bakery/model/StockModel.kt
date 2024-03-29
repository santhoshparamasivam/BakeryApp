package com.cbe.bakery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StockModel {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("transId")
    @Expose
    var transId: String? = null

    @SerializedName("shopId")
    @Expose
    var shopId: Int? = null

    @SerializedName("itemId")
    @Expose
    var itemId: Long? = null

    @SerializedName("shop")
    @Expose
    var shop: String? = null

    @SerializedName("item")
    @Expose
    var item: Any? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Any? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn:  String? = null

    @SerializedName("modifiedOn")
    @Expose
    var modifiedOn:  Long?  = null

    @SerializedName("modifiedBy")
    @Expose
    var modifiedBy: Any? = null

}