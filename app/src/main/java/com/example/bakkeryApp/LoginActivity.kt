package com.example.bakkeryApp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bakkeryApp.model.LoginModel
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    lateinit var id:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
          sessionManager = SessionManager(this)
        loginBtn.setOnClickListener {
            if (login_emailid.text.isEmpty()){
                login_emailid.error = "Please Enter Email Id"
            }else if(login_password.text.isEmpty()){
                login_password.error = "Please Enter Password"
            }else
                  LoginMethod()

        }

        id= sessionManager.getStringKey(SessionKeys.USER_ID).toString()
       if (id != ""){
           Log.e("user id",id+"  user")
           intent = Intent(applicationContext, HomeActivity::class.java)
           intent.putExtra("UserId",id)
           startActivity(intent)
       }
    }

    private fun LoginMethod() {
        val progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        val requestInterface = Retrofit.Builder()
            .baseUrl(ApiManager.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        val jsonObject=JsonObject()
        jsonObject.addProperty("username",login_emailid.text.toString())
        jsonObject.addProperty("password",login_password.text.toString())

       requestInterface.UserLogin(jsonObject).enqueue(object : Callback<LoginModel> {
           override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
               progressDialog.dismiss()
                   if (response.code() == 200) {
                       Log.e("response", response.message() + " " + response.code())
                       sessionManager.setSessionValue(SessionKeys.USER_ID, response.body().id)
                       sessionManager.setSessionValue(
                           SessionKeys.FIRST_NAME,
                           response.body().username
                       )
                       sessionManager.setSessionValue(
                           SessionKeys.USER_TOKEN,
                           response.body().accessToken
                       )
                       sessionManager.setSessionValue(
                           SessionKeys.AUTH_TOKEN,
                           response.body().tokenType + " " + response.body().accessToken
                       )
                       Toast.makeText(
                           applicationContext,
                           "Logged In Successfully",
                           Toast.LENGTH_LONG
                       )
                           .show()
                       intent = Intent(applicationContext, HomeActivity::class.java)
                       startActivity(intent)
                       finish()
                   } else {
                       progressDialog.dismiss()
                       Log.e("response", response.message() + "")
                       Toast.makeText(
                           applicationContext,
                           "Please Check Username And Password and try again later",
                           Toast.LENGTH_LONG
                       ).show()
                   }

           }
           override fun onFailure(call: Call<LoginModel>, t: Throwable) {
               progressDialog.dismiss()
                t.printStackTrace()
               Toast.makeText(applicationContext, "loggin failed,Please try again later",Toast.LENGTH_LONG).show()
           }
        })
    }
}
