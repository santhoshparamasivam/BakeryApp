package com.cbe.bakery.model


import com.google.gson.annotations.SerializedName

class AvailableQuantity {

    @SerializedName("availableQuantity")
    var availableQuantity: Int? = null
    @SerializedName("mrp")
    var mrp: Float? = null
}