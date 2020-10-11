package com.cbe.bakery

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.cbe.bakery.fragments.AddStockItemFragment
import com.cbe.bakery.fragments.AddStockLocationFragment
import com.cbe.bakery.fragments.AvailabilityFragment
import com.cbe.bakery.fragments.StockMovementSummary
import com.cbe.bakery.sessionManager.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class StockSummary : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    lateinit var navBottomView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stock_summary)
        toolbar = findViewById(R.id.toolbar)
        navBottomView=findViewById(R.id.nav_bottomView)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Stock Summary"
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        navBottomView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.movement -> {
                    loadFragment(StockMovementSummary())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.availability -> {
                    loadFragment(AvailabilityFragment())
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }
        loadFragment(StockMovementSummary())
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.isAddToBackStackAllowed
        transaction.commit()
    }
}