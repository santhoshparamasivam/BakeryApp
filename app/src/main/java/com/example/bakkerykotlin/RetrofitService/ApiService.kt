package com.example.bakkerykotlin.RetrofitService

import com.example.bakkerykotlin.Model.ItemsModel
import com.example.bakkerykotlin.Model.LoginModel
import com.example.bakkerykotlin.Model.OrdersModel
import com.example.bakkerykotlin.Model.ShopModel
import com.google.gson.JsonObject
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

  @GET("/items")
  fun GetItems(): Call<ArrayList<ItemsModel>>

  @GET("/shops")
  fun GetShops(): Call<ArrayList<ShopModel>>

  @FormUrlEncoded
  @POST("/orders")
  fun SaveOrders(@Field("shopId")shopId:String,@Field("itemId")itemId:String,@Field("quantity")quantity:Int,@Field("deliveryDate")deliverydate:String ): Call<ResponseBody>


//  @FormUrlEncoded
//  @POST("/api/auth/getToken")
//  fun GetToken(@Field("token")userToken:String): Call<LoginModel>

  @POST("/api/auth/getToken")
  fun GetToken(@Body jsonObject: JsonObject): Call<LoginModel>

  @GET("/orders/formatted")
  fun GetOrders(): Call<OrdersModel>

}