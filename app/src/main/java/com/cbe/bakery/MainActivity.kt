package com.cbe.bakery

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cbe.bakery.model.LoginModel
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.ViewUtils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    lateinit var userToken:String
    private lateinit var userId:String
    private lateinit var viewUtils: ViewUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        viewUtils = ViewUtils()
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
                if (response.code() == 200) {
                    sessionManager.setSessionValue(SessionKeys.USER_TOKEN,response.body().accessToken)
                    sessionManager.setSessionValue(SessionKeys.AUTH_TOKEN,response.body().tokenType+" "+response.body().accessToken)
                    intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
//                    viewUtils.showToast(this@MainActivity,"please try again later",Toast.LENGTH_SHORT)
                    Toast.makeText(
                        this@MainActivity,
                        "Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                t.printStackTrace()
//                viewUtils.showToast(this@MainActivity,"Request Failed, Please try again later",Toast.LENGTH_SHORT)
                Toast.makeText(
                    this@MainActivity,
                    "Request Failed, Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
