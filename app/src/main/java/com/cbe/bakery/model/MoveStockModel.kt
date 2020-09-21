package com.cbe.bakery.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class MoveStockModel {
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
    var itemId: Any? = null

    @SerializedName("shop")
    @Expose
    var shop: String? = null

    @SerializedName("item")
    @Expose
    var item: Any? = null

    @SerializedName("action")
    @Expose
    var action: String? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Any? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn:  String? = null

    @SerializedName("modifiedOn")
    @Expose
    var modifiedOn:  String?  = null

    @SerializedName("modifiedBy")
    @Expose
    var modifiedBy: Any? = null
}