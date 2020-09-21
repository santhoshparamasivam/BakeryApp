package com.cbe.bakery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PinVerificationModel {
    @SerializedName("pin")
    @Expose
    var pin: String? = null
}