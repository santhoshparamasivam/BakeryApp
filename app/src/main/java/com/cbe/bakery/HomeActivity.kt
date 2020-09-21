package com.cbe.bakery

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.cbe.bakery.fragments.HomeFragment
import com.cbe.bakery.fragments.ViewItemsFragment
import com.cbe.bakery.fragments.ViewMoveFragment
import com.cbe.bakery.fragments.ViewStockFragment
import com.cbe.bakery.sessionManager.SessionKeys
import com.cbe.bakery.sessionManager.SessionManager
import com.cbe.bakery.utils.ViewUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*


class HomeActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    lateinit var shopId: String
    private lateinit var userId: String
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    private lateinit var viewUtils: ViewUtils
    lateinit var searchView: SearchView
    lateinit var fab: FloatingActionButton

    private var doubleBackToExitPressedOnce = false
    lateinit var appBar: RelativeLayout

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.toolbar)
        appBar = findViewById<RelativeLayout>(R.id.appBar)
        appBar.visibility = View.GONE
        searchView=findViewById(R.id.searchView)
        searchView.visibility=View.GONE
        setSupportActionBar(toolbar)
        viewUtils = ViewUtils()
        sessionManager = SessionManager(this)
        drawerLayout = findViewById(R.id.drawer_layout)
        fab=findViewById(R.id.fab)
        fab.visibility=View.GONE
        navView = findViewById(R.id.nav_view)
        progressDialog = ProgressDialog(this@HomeActivity)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        move_stock_menu.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
//            intent = Intent(applicationContext, MoveStockActivity::class.java)
//            startActivity(intent)
            loadFragment(ViewMoveFragment())
        }

        add_stock_menu.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val bundle = Bundle()
            bundle.putString("type", "adStock")
            val fragment= ViewStockFragment()
            fragment.arguments = bundle;
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()
        }
        Dashboard.setOnClickListener {
            searchView.visibility=View.GONE
            fab.visibility=View.GONE
            appBar.visibility = View.GONE
            drawerLayout.closeDrawer(GravityCompat.START)
            loadFragment(HomeFragment())
        }
        remove_stock_menu.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val bundle = Bundle()
            bundle.putString("type", "removeStock")
            val fragment= ViewStockFragment()
            fragment.arguments = bundle;
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()

        }
        item_menu.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            loadFragment(ViewItemsFragment())
        }


        logout_menu.setOnClickListener {
            viewUtils.bakeryAlert(
                this,
                "Are you sure you want to Logout?",
                "Yes",
             DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                 sessionManager.clearSession()
                 Toast.makeText(
                     this@HomeActivity,
                     "Logged Out Successfully",
                     Toast.LENGTH_LONG
                 ).show()
//                 viewUtils.showToast(this@HomeActivity,"Logged Out Successfully",Toast.LENGTH_SHORT)
                    intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },"No",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                },true
            )
        }


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        stock_move_menu.setOnClickListener {
            if (nav_submenu.isVisible) {
                nav_submenu.visibility = View.GONE
            }else{
                nav_submenu.visibility = View.VISIBLE
            }
        }
        nav_bottomView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.addStockByItem -> {
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.addStockByLocation -> {
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }
        val headerView: View = navView.getHeaderView(0)
        headerView.userName.text = sessionManager.getStringKey(SessionKeys.FIRST_NAME)
        userId = sessionManager.getStringKey(SessionKeys.USER_ID).toString()
        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            super.onBackPressed()
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true

//        viewUtils.showToast(this@HomeActivity,"Please click BACK again to exit",Toast.LENGTH_SHORT)
        Toast.makeText(
            this@HomeActivity,
            "Please click BACK again to exit",
            Toast.LENGTH_LONG
        ).show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)

    }
}