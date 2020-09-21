package com.cbe.bakery

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.cbe.bakery.model.LoginModel
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.ViewUtils
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
    private lateinit var viewUtils: ViewUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        viewUtils = ViewUtils()
          sessionManager = SessionManager(this)
        loginBtn.setOnClickListener {
            if (loginEmailId.text.isEmpty()){
                loginEmailId.error = "Please Enter Email Id"
            }else if(login_password.text.isEmpty()){
                login_password.error = "Please Enter Password"
            }else
                  loginMethod()

        }

        id= sessionManager.getStringKey(SessionKeys.USER_ID).toString()
       if (id != ""){
           Log.e("user id",id+"  user")
           intent = Intent(applicationContext, HomeActivity::class.java)
           intent.putExtra("UserId",id)
           startActivity(intent)
       }
    }

    private fun loginMethod() {
        val progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        val requestInterface = Retrofit.Builder()
            .baseUrl(ApiManager.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        val jsonObject=JsonObject()
        jsonObject.addProperty("username",loginEmailId.text.toString())
        jsonObject.addProperty("password",login_password.text.toString())

       requestInterface.userLogin(jsonObject).enqueue(object : Callback<LoginModel> {
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
//                       viewUtils.showToast(this@LoginActivity,"Logged In Successfully",Toast.LENGTH_SHORT)
                       Toast.makeText(
                           this@LoginActivity,
                           "Logged In Successfully",
                           Toast.LENGTH_LONG
                       ).show()
                       intent = Intent(applicationContext, HomeActivity::class.java)
                       startActivity(intent)
                       finish()
                   } else {
                       progressDialog.dismiss()
//                       viewUtils.showToast(this@LoginActivity,"Please Check Username And Password and try again later",Toast.LENGTH_SHORT)
                       Toast.makeText(
                           this@LoginActivity,
                           "Please Check Username And Password and try again later",
                           Toast.LENGTH_LONG
                       ).show()
                   }

           }
           override fun onFailure(call: Call<LoginModel>, t: Throwable) {
               progressDialog.dismiss()
                t.printStackTrace()
//               viewUtils.showToast(this@LoginActivity,"Connection failed. please try again later",Toast.LENGTH_SHORT)
               Toast.makeText(
                   this@LoginActivity,
                   "Connection failed. please try again later",
                   Toast.LENGTH_LONG
               ).show()
           }
        })
    }
}
