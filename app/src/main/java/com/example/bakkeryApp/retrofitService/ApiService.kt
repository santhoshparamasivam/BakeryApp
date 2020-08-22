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
  fun userLogin(@Body jsonObject: JsonObject ): Call<LoginModel>



  @GET("/shops")
  fun getShopsList(): Call<ArrayList<ShopModel>>

//  @FormUrlEncoded
//  @POST("/orders")
//  fun SaveOrders(@Field("shopId")shopId:String,@Field("itemId")itemId:String,@Field("quantity")quantity:Int,@Field("deliveryDate")deliverydate:String ): Call<ResponseBody>


//  @FormUrlEncoded
//  @POST("/api/auth/getToken")
//  fun GetToken(@Field("token")userToken:String): Call<LoginModel>

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
  fun saveItems(@Part image: MultipartBody.Part,
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
    fun getViewStock(): Call<ArrayList<StockModel>>


  @POST("/stock/byItem")
  fun stockByItem(@Body jsonObject: JsonObject): Call<ResponseBody>

  @POST("/stock/byLocation")
  fun stockByLocation(@Body jsonObject: JsonObject): Call<ResponseBody>


    @PUT("/items")
    fun updateItems(@Query("id") id:Long, @Query("costPrice")costprice:Float, @Query("sellingPrice")sellingPrice:Float): Call<ResponseBody>
//   fun updateItems(@Query("id") id:Long,@Query("sku") sku:String, @Query("hsnCode") hsn_code:String, @Query("costPrice")costprice:Float, @Query("sellingPrice")sellingPrice:Float): Call<ResponseBody>

    @GET("/stock/{id}")
    fun getStock(@Path(value = "id", encoded = true) id:Int): Call<ResponseBody>
}