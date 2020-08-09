package com.example.bakkeryApp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetItemlistModel {

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
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("nameInTamil")
        @Expose
        var nameInTamil: Any? = null

        @SerializedName("itemCategory")
        @Expose
        var itemCategory: String? = null

        @SerializedName("costPrice")
        @Expose
        var costPrice: Int? = null

        @SerializedName("sellingPrice")
        @Expose
        var sellingPrice: Int? = null

        @SerializedName("taxPercentage")
        @Expose
        var taxPercentage: Int? = null

        @SerializedName("taxIncluded")
        @Expose
        var taxIncluded: Boolean? = null

        @SerializedName("unitOfMeasure")
        @Expose
        var unitOfMeasure: String? = null

        @SerializedName("imageFileName")
        @Expose
        var imageFileName: String? = null

        @SerializedName("itemHistory")
        @Expose
        var itemHistory: List<ItemHistory>? = null

        @SerializedName("imagePath")
        @Expose
        var imagePath: Any? = null

        inner class ItemHistory {
            @SerializedName("name")
            @Expose
            var name: Any? = null

            @SerializedName("nameInTamil")
            @Expose
            var nameInTamil: Any? = null

            @SerializedName("itemCategory")
            @Expose
            var itemCategory: String? = null

            @SerializedName("costPrice")
            @Expose
            var costPrice: Int? = null

            @SerializedName("sellingPrice")
            @Expose
            var sellingPrice: Int? = null

            @SerializedName("taxPercentage")
            @Expose
            var taxPercentage: Int? = null

            @SerializedName("taxIncluded")
            @Expose
            var taxIncluded: Boolean? = null

            @SerializedName("unitOfMeasure")
            @Expose
            var unitOfMeasure: String? = null

            @SerializedName("createdOn")
            @Expose
            var createdOn: String? = null

        }

}