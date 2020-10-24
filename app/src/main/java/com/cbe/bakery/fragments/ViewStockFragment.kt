package com.cbe.bakery.fragments
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cbe.bakery.AddStockActivity
import com.cbe.bakery.HomeActivity
import com.cbe.bakery.R
import com.cbe.bakery.ViewStockDetails
import com.cbe.bakery.adapter.StockAdapter
import com.cbe.bakery.model.StockModel
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

class ViewStockFragment : Fragment(){
    private lateinit var  createItem: ImageView
    private lateinit var  viewItem: ImageView
    private lateinit var  pageHeaderTxt: TextView
    lateinit var  recyclerview: RecyclerView
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    var stockList: ArrayList<StockModel> = ArrayList()
    private lateinit var stockAdapter: StockAdapter
    private lateinit var  swipeRefresh: SwipeRefreshLayout
    private lateinit var  type: String
    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view :View= inflater.inflate(R.layout.fragment_view_stock, container, false)
         type = arguments!!.getString("type")!!
        
        createItem=view.findViewById(R.id.create_item)
        viewItem=view.findViewById(R.id.view_item)
        recyclerview=view.findViewById(R.id.recyclerview)
        swipeRefresh=view.findViewById(R.id.swipeRefresh)
        pageHeaderTxt=view.findViewById(R.id.pageHeaderTxt)
        if(type=="removeStock") {
            activity?.title = "Remove Stock"
            pageHeaderTxt.text= "Remove Stock"
            (activity as HomeActivity?)?.img_icon!!.setImageDrawable(resources.getDrawable(R.drawable.ic_minus))
        }else if(type=="addStock") {
            activity?.title = "View Stock"
            pageHeaderTxt.text= "View Stock"
            (activity as HomeActivity?)?.img_icon!!.setImageDrawable(resources.getDrawable(R.drawable.ic_add))

        }


        progressDialog= ProgressDialog(activity)
        sessionManager=
            SessionManager(activity)
        swipeRefresh.setOnRefreshListener {
            viewStockMethod()
            swipeRefresh.isRefreshing = false
        }
        (activity as HomeActivity?)?.fab!!.visibility = View.VISIBLE
        (activity as HomeActivity?)?.searchView!!.visibility = View.VISIBLE

        (activity as HomeActivity?)?.fab!!.setOnClickListener {
            val intent= Intent(activity, AddStockActivity::class.java)
            intent.putExtra("type",type)
            activity?.startActivity(intent)

        }
        (activity as HomeActivity?)?.appBar!!.visibility = View.VISIBLE
        (activity as HomeActivity?)?.searchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                val finalStockList = ArrayList<StockModel>()
                for (item in stockList) {
                    if (item.shop.toString().toLowerCase(Locale.ROOT).contains(
                            newText.toLowerCase(
                                Locale.ROOT
                            )
                        )
                        || item.item.toString().toLowerCase(Locale.ROOT).contains(
                            newText.toLowerCase(
                                Locale.ROOT
                            )
                        )
                        || item.transId.toString().toLowerCase(Locale.ROOT).contains(
                            newText.toLowerCase(
                                Locale.ROOT
                            )
                        )
                    ) {
                        finalStockList.add(item)
                    }
                }
                setAdapterMethod(finalStockList)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
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
                                ViewStockDetails::class.java
                            )
                            intent.putExtra("ItemId", stockList[position].id);
                            if (stockList[position].item != null)
                                intent.putExtra("stockBy", "ByItem");
                            if (stockList[position].shop != null)
                                intent.putExtra("stockBy", "ByLocation");

                            context!!.startActivity(intent)

                    }
                })
        )
        return view
    }

    private fun viewStockMethod() {
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

        if(type=="addStock") {
            requestInterface.getAddStock().enqueue(object : Callback<ArrayList<StockModel>> {
                override fun onResponse(
                    call: Call<ArrayList<StockModel>>,
                    response: Response<ArrayList<StockModel>>
                ) {
                    progressDialog.dismiss()
                    if (response.code() == 200) {
                        progressDialog.dismiss()
                        stockList = response.body()
                        setAdapterMethod(stockList)
                    } else {
                        progressDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<ArrayList<StockModel>>, t: Throwable) {
                    t.printStackTrace()
                    progressDialog.dismiss()
                    Toast.makeText(
                        activity,
                        "Connection failed,Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else if(type=="removeStock") {
            requestInterface.getRemoveStock().enqueue(object : Callback<ArrayList<StockModel>> {
                override fun onResponse(
                    call: Call<ArrayList<StockModel>>,
                    response: Response<ArrayList<StockModel>>
                ) {
                    progressDialog.dismiss()
                    if (response.code() == 200) {
                        progressDialog.dismiss()
                        stockList = response.body()
                        setAdapterMethod(stockList)
                    } else {
                        progressDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<ArrayList<StockModel>>, t: Throwable) {
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
    }
    private fun setAdapterMethod(stockList: ArrayList<StockModel>) {
        recyclerview.layoutManager = LinearLayoutManager(recyclerview.context)
        recyclerview.setHasFixedSize(true)
        stockAdapter =
            StockAdapter(stockList, activity)
        recyclerview.adapter =stockAdapter
    }


    override fun onResume() {
        super.onResume()
        viewStockMethod()
    }
}