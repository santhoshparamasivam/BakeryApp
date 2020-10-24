package com.cbe.bakery.retrofitService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {


    companion object {

//        const val BASE_URL = "http://18.222.152.24:8080/"
//        const val BASE_URL = "http://arasubakery.pagekite.me/"
        //24/10/2020 changed
        const val BASE_URL = "http://103.212.120.18:8082/"
    }

    init {
        // 2
        val retrofit = Retrofit.Builder()
            // 1
            .baseUrl(BASE_URL)
            //3
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //4

    }

    private fun loadData() {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)

    }
}