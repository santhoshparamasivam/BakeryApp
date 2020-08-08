package com.example.bakkeryApp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginModel {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("roles")
    @Expose
    var roles: List<String>? = null

    @SerializedName("tokenType")
    @Expose
    var tokenType: String? = null

    @SerializedName("accessToken")
    @Expose
    var accessToken: String? = null

}