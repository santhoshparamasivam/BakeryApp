package com.cbe.bakery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ItemHistoryModel {
    @SerializedName("id")
    @Expose
    var id: Long? = null

    @SerializedName("itemId")
    @Expose
    var itemId: Long? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Any? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = null

    @SerializedName("modifiedOn")
    @Expose
    var modifiedOn: String? = null

    @SerializedName("modifiedBy")
    @Expose
    var modifiedBy: Any? = null

    @SerializedName("itemCategory")
    @Expose
    var itemCategory: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("sku")
    @Expose
    var sku: String? = null

    @SerializedName("unitOfMeasure")
    @Expose
    var unitOfMeasure: String? = null

    @SerializedName("costPrice")
    @Expose
    var costPrice: Float? = null

    @SerializedName("sellingPrice")
    @Expose
    var sellingPrice: Float? = null

    @SerializedName("hsnCode")
    @Expose
    var hsnCode: String? = null

    @SerializedName("taxPercentage")
    @Expose
    var taxPercentage: Float? = null

    @SerializedName("taxIncluded")
    @Expose
    var taxIncluded: Boolean? = null

    @SerializedName("imageFileName")
    @Expose
    var imageFileName: String? = null

}