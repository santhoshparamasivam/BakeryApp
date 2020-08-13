package com.example.bakkeryApp.retrofitService

import com.example.bakkeryApp.model.ItemCategoryModel
import com.example.bakkeryApp.model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

//  @FormUrlEncoded
//  @POST("/api/auth/signin")
//  fun UserLogin(@Field("username")email:String,@Field("password")password:String ): Call<LoginModel>
//  @FormUrlEncoded
  @POST("/api/auth/signin")
  fun UserLogin(@Body jsonObject: JsonObject ): Call<LoginModel>



  @GET("/shops")
  fun GetShops(): Call<ArrayList<ShopModel>>

//  @FormUrlEncoded
//  @POST("/orders")
//  fun SaveOrders(@Field("shopId")shopId:String,@Field("itemId")itemId:String,@Field("quantity")quantity:Int,@Field("deliveryDate")deliverydate:String ): Call<ResponseBody>


//  @FormUrlEncoded
//  @POST("/api/auth/getToken")
//  fun GetToken(@Field("token")userToken:String): Call<LoginModel>

  @POST("/api/auth/getToken")
  fun GetToken(@Body jsonObject: JsonObject): Call<LoginModel>

    @GET("/item/categories")
    fun getItemCategories(): Call<ArrayList<ItemCategoryModel>>

    @GET("/items")
    fun GetItems(): Call<ArrayList<ItemsModel>>

    @GET("/items/{id}")
    fun getItem(@Path(value = "id", encoded = true) id: Long): Call<ItemsModel>

  @Multipart
  @POST("/items")
  fun SaveOrders(@Part image: MultipartBody.Part,
                 @Part("type") type: RequestBody?,
                 @Part("name") item_name: RequestBody?,
                 @Part("itemCategory") itemCategory: RequestBody?,
                 @Part("costPrice") costPrice: RequestBody?,
                 @Part("sellingPrice")sellingPrice: RequestBody?,
                 @Part("taxPercentage")taxPercentage: RequestBody?,
                 @Part("unitOfMeasure")unitOfMeasure: RequestBody?,
                 @Part("taxInclude") taxIncluded: RequestBody?,
                 @Part("hsnCode") hsn_Code: RequestBody?,
                 @Part("sku") sku: RequestBody?): Call<ResponseBody>

    @GET("/items/hist/{id}")
    fun getItemPriceHistory(@Path(value = "id", encoded = true) id: Long): Call<ArrayList<ItemHistoryModel>>

    @GET("/stock")
    fun GetViewStock(): Call<ArrayList<StockModel>>


  @POST("/stock/byItem")
  fun StockByItem(@Body jsonObject: JsonObject): Call<ResponseBody>

  @POST("/stock/byLocation")
  fun StockByLocation(@Body jsonObject: JsonObject): Call<ResponseBody>


}