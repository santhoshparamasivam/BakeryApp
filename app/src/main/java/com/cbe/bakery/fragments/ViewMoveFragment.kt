package com.cbe.bakery.fragments
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cbe.bakery.*
import com.cbe.bakery.adapter.MoveStockAdapter
import com.cbe.bakery.model.ItemsModel
import com.cbe.bakery.model.MoveStockModel
import com.cbe.bakery.retrofitService.ApiManager
import com.cbe.bakery.retrofitService.ApiService
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.RecyclerItemClickListener
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class ViewMoveFragment : Fragment() {
    lateinit var recyclerview: RecyclerView
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    var itemList: ArrayList<MoveStockModel> = ArrayList()
    private lateinit var itemsAdapter: MoveStockAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_view_move, container, false)
        recyclerview = view.findViewById(R.id.movelistRecycler)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        sessionManager =
            SessionManager(activity)
        progressDialog = ProgressDialog(activity)
        activity?.title = "Move Stock"
        (activity as HomeActivity?)?.searchView!!.visibility = View.VISIBLE
        (activity as HomeActivity?)?.fab!!.visibility = View.VISIBLE
        (activity as HomeActivity?)?.fab!!.setOnClickListener {
            val intent = Intent(activity, MoveStockActivity::class.java)
            activity?.startActivity(intent)

        }
        (activity as HomeActivity?)?.searchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                val finalItemList = ArrayList<MoveStockModel>()
                for (item in itemList) {
                    if (item.shop.toString().toLowerCase(Locale.ROOT).contains(
                            newText.toLowerCase(
                                Locale.ROOT
                            )
                        ) || item.item.toString().toLowerCase(
                            Locale.ROOT
                        ).contains(newText.toLowerCase(Locale.ROOT))
                    ) {
                        finalItemList.add(item)
                    }
                }
                setAdapterMethod(finalItemList)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                return false
            }

        })
        recyclerview.addOnItemTouchListener(
            RecyclerItemClickListener(
                context!!,
                object :
                    RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val intent = Intent(
                            context,
                            ViewMoveStockDetails::class.java
                        )
                        intent.putExtra("ItemId", itemList[position].id);
                        if (itemList[position].action != null && itemList[position].action=="MoveByLocation")
                        intent.putExtra("moveBy", "ByLocation");
                        if (itemList[position].action != null && itemList[position].action=="MoveByItem")
                            intent.putExtra("moveBy", "ByItem");
                        context!!.startActivity(intent)
                    }
                })
        )

        swipeRefresh.setOnRefreshListener {
            moveStockList()
            swipeRefresh.isRefreshing = false
        }
        return view
    }

    private fun moveStockList() {
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        var userToken = sessionManager.getStringKey(SessionKeys.USER_TOKEN).toString()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $userToken")
                .build()
            chain.proceed(newRequest)
        }.build()
        val requestInterface = Retrofit.Builder()
            .baseUrl(ApiManager.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
        requestInterface.getAllMoveStockList().enqueue(object : Callback<ArrayList<MoveStockModel>> {
            override fun onResponse(
                call: Call<ArrayList<MoveStockModel>>,
                response: Response<ArrayList<MoveStockModel>>
            ) {
                progressDialog.dismiss()
                Log.e("response",response.code().toString()+" ")
                if (response.code() == 200) {

                    itemList = response.body()
                    setAdapterMethod(itemList)
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ArrayList<MoveStockModel>>, t: Throwable) {
                t.printStackTrace()
                progressDialog.dismiss()

                Log.e("response", t.message+"  ")
                Toast.makeText(
                    activity,
                    "Connection failed,Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setAdapterMethod(itemList: ArrayList<MoveStockModel>) {
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        itemsAdapter =
            MoveStockAdapter(itemList, activity)
        recyclerview.adapter = itemsAdapter
    }

    override fun onResume() {
        super.onResume()
        moveStockList()
    }
}