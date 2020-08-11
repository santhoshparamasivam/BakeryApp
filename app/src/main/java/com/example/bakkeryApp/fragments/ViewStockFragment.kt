package com.example.bakkeryApp.fragments
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakkeryApp.AddItemActivity
import com.example.bakkeryApp.AddStockActivity
import com.example.bakkeryApp.R
import com.example.bakkeryApp.adapter.Items_Adapter
import com.example.bakkeryApp.adapter.Orders_Adapter
import com.example.bakkeryApp.model.ItemsModel
import com.example.bakkeryApp.retrofitService.ApiManager
import com.example.bakkeryApp.retrofitService.ApiService
import com.example.bakkeryApp.sessionManager.SessionKeys
import com.example.bakkeryApp.sessionManager.SessionManager
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewStockFragment : Fragment(){
    lateinit var  create_item: ImageView
    lateinit var  view_item: ImageView
    lateinit var  recyclerview: RecyclerView
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    var itemList: ArrayList<ItemsModel> = ArrayList()
    lateinit var Itemsadapter: Items_Adapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view :View= inflater.inflate(R.layout.fragment_view_stock,container,false)
        create_item=view.findViewById(R.id.create_item)
        view_item=view.findViewById(R.id.view_item)
        recyclerview=view.findViewById(R.id.recyclerview)
        activity?.title  ="View Stock"
        progressDialog= ProgressDialog(activity)
        sessionManager= SessionManager(activity)
        create_item.setOnClickListener {
            val intent= Intent(activity, AddItemActivity::class.java)
            activity?.startActivity(intent)
        }
        view_item.setOnClickListener {
            val intent= Intent(activity, AddStockActivity::class.java)
            activity?.startActivity(intent)
        }

        ViewStockMethod()

    return view}

    private fun ViewStockMethod() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        var user_token = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $user_token")
                .build()
            chain.proceed(newRequest)
        }.build()
        val requestInterface = Retrofit.Builder()
            .baseUrl(ApiManager.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        requestInterface.GetItems().enqueue(object : Callback<ArrayList<ItemsModel>> {
            override fun onResponse(
                call: Call<ArrayList<ItemsModel>>,
                response: Response<ArrayList<ItemsModel>>) {
                progressDialog.dismiss()
                Log.e("response", response.code().toString() + "  rss")
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    itemList = response.body()
                    setadaptermethod()
                    Log.e("Itemlist", itemList.size.toString() + " error")
                } else {
                    progressDialog.dismiss()
//                    Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
//                        .show()
                }
            }
            override fun onFailure(call: Call<ArrayList<ItemsModel>>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()
                Toast.makeText(
                    activity,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun setadaptermethod() {
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        Itemsadapter = Items_Adapter(itemList, activity)
        recyclerview.adapter =Itemsadapter
    }
}