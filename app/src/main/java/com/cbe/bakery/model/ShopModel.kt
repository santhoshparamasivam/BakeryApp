package com.cbe.bakery.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class ShopModel {
    @SerializedName("createdBy")
    @Expose
    var createdBy: Any? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = null

    @SerializedName("updatedOn")
    @Expose
    var updatedOn: Any? = null

    @SerializedName("updatedBy")
    @Expose
    var updatedBy: Any? = null

    @SerializedName("id")
    @Expose
    var id: Long? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null

    @SerializedName("email")
    @Expose
    var email: Any? = null

    @SerializedName("mobile")
    @Expose
    var mobile: Any? = null

}