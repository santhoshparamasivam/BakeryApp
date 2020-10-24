package com.cbe.bakery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UnitsModel {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Any? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = null

    @SerializedName("modifiedOn")
    @Expose
    var modifiedOn: Any? = null

    @SerializedName("modifiedBy")
    @Expose
    var modifiedBy: Any? = null

    @SerializedName("unit")
    @Expose
    var unit: String? = null
}