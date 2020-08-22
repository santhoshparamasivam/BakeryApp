package com.example.bakkeryApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakkeryApp.model.LoginModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    lateinit var userToken:String
    lateinit var userId:String
    private val TIME_OUT = 3000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        sessionManager = SessionManager(this)

        userToken= sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        userId= sessionManager.getStringKey(SessionKeys.USER_ID).toString()
        if (userId !="" && userToken !=""){
            getUserToken()
        }else if(userToken == ""){
            intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
        private fun getUserToken() {
        val requestInterface = Retrofit.Builder()
            .baseUrl(ApiManager.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)

            val jsonObject= JsonObject()
            jsonObject.addProperty("token",userToken)

        requestInterface.getToken(jsonObject).enqueue(object : Callback<LoginModel> {
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                Log.e("response",response.code().toString()+"    response")
                if (response.code() == 200) {
                        Log.e("response",response.body().toString()+"    response")
                    sessionManager.setSessionValue(SessionKeys.USER_TOKEN,response.body().accessToken)
                    sessionManager.setSessionValue(SessionKeys.AUTH_TOKEN,response.body().tokenType+" "+response.body().accessToken)
                    intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{

                    Log.e("response",response.message()+"")
                    Log.e("response",response.errorBody().string()+"")
                    Toast.makeText(applicationContext, "Please try again later", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(applicationContext, "loggin failed,Please try again later", Toast.LENGTH_LONG).show()
            }
        })
        }


}
