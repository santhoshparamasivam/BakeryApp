package com.cbe.bakery.retrofitService

import com.cbe.bakery.model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth/signin")
    fun userLogin(@Body jsonObject: JsonObject): Call<LoginModel>

    @GET("/shops")
    fun getShopsList(): Call<ArrayList<ShopModel>>

    @POST("/api/auth/getToken")
    fun getToken(@Body jsonObject: JsonObject): Call<LoginModel>

    @GET("/item/categories")
    fun getItemCategories(): Call<ArrayList<ItemCategoryModel>>

    @GET("/items")
    fun getAllItems(): Call<ArrayList<ItemsModel>>

    @GET("/items/{id}")
    fun getItemDetails(@Path(value = "id", encoded = true) id: Long): Call<ItemsModel>

    @Multipart
    @POST("/items")
    fun saveItems(
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody?,
        @Part("name") item_name: RequestBody?,
        @Part("itemCategory") itemCategory: RequestBody?,
        @Part("costPrice") costPrice: RequestBody?,
        @Part("sellingPrice") sellingPrice: RequestBody?,
        @Part("taxPercentage") taxPercentage: RequestBody?,
        @Part("unitOfMeasure") unitOfMeasure: RequestBody?,
        @Part("trackInventory") taxIncluded: RequestBody?,
        @Part("saleTaxIncluded") saleTaxIncluded: RequestBody?,
        @Part("costTaxIncluded")costTaxIncluded: RequestBody?,
        @Part("hsnCode") hsn_Code: RequestBody?,
        @Part("sku") sku: RequestBody?
    ): Call<ResponseBody>

    @GET("/items/hist/{id}")
    fun getItemPriceHistory(
        @Path(
            value = "id",
            encoded = true
        ) id: Long
    ): Call<ArrayList<ItemHistoryModel>>

    @GET("/stock")
    fun getViewStock(): Call<ArrayList<StockModel>>

    @GET("/items/getAvailableQuantity/{shopId}/{itemId}")
    fun getAvailableQuantity(@Path(value = "shopId", encoded = true) shopId: Long, @Path(value = "itemId", encoded = true) itemId: Long): Call<AvailableQuantity>


    @POST("/stock/byItem")
    fun stockByItem(@Body jsonObject: JsonObject): Call<ResponseBody>

    @POST("/stock/byLocation")
    fun stockByLocation(@Body jsonObject: JsonObject): Call<ResponseBody>


//    @PUT("/items")
//    fun updateItems(
//        @Query("id") id: Long,
//        @Query("costPrice") costPrice: Float,
//        @Query("sellingPrice") sellingPrice: Float
//    ): Call<ResponseBody>

    @Multipart
    @PUT("/items")
    fun updateItems(
       @Part image: MultipartBody.Part,
       @Part("id") id: RequestBody?,
       @Part("type") type: RequestBody?,
       @Part("name") item_name: RequestBody?,
       @Part("itemCategory") itemCategory: RequestBody?,
       @Part("costPrice") costPrice: RequestBody?,
       @Part("sellingPrice") sellingPrice: RequestBody?,
       @Part("taxPercentage") taxPercentage: RequestBody?,
       @Part("unitOfMeasure") unitOfMeasure: RequestBody?,
       @Part("trackInventory") taxIncluded: RequestBody?,
       @Part("saleTaxIncluded") saleTaxIncluded: RequestBody?,
       @Part("costTaxIncluded")costTaxIncluded: RequestBody?,
       @Part("hsnCode") hsn_Code: RequestBody?,
       @Part("sku") sku: RequestBody?
    ): Call<ResponseBody>

    @GET("/stock/{id}")
    fun getStock(@Path(value = "id", encoded = true) id: Int): Call<ResponseBody>

    @GET("/stock/void/{id}")
    fun voidStock(@Path(value = "id", encoded = true) id: Int): Call<ResponseBody>

    @POST("/stock/delete/byLocation")
    fun voidStockByLocation(@Body jsonObject: JsonObject): Call<ResponseBody>

    @POST("/stock/delete/byItem")
    fun voidStockByItem(@Body jsonObject: JsonObject): Call<ResponseBody>

    @GET("/items/void/{id}")
    fun voidItemDetails(@Path(value = "id", encoded = true) id: Long): Call<ResponseBody>


    @GET("/users/getPIN/{userId}")
    fun getPin(@Path(value = "userId", encoded = true) id: String): Call<PinVerificationModel>

    @POST("/move/byItem")
    fun moveStockByItem(@Body jsonObject: JsonObject): Call<ResponseBody>

    @POST("/move/byLocation")
    fun moveStockByLocation(@Body jsonObject: JsonObject): Call<ResponseBody>


    @GET("/move")
    fun getAllMoveStockList():Call<ArrayList<MoveStockModel>>

    @GET("/move/{id}")
    fun getMove(@Path(value = "id", encoded = true) id: Int): Call<ResponseBody>

    @GET("/move/void/{id}")
    fun voidStockDetails(@Path(value = "id", encoded = true) id: Int): Call<ResponseBody>



}