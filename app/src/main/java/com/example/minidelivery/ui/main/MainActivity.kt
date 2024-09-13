package com.example.minidelivery.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.minidelivery.R
import com.example.minidelivery.ui.done.DoneActivity
import com.example.minidelivery.ui.delivery.ManageDeliveryActivity
import com.example.minidelivery.ui.detail.DetailActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var orderCardView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupUI()
        observeViewModel()
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        orderCardView = findViewById(R.id.orderCardView)
    }

    private fun setupUI() {
        tabLayout.addOnTabSelectedListener(viewModel.tabSelectedListener)

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, DoneActivity::class.java))
                    true
                }
                R.id.nav_delivery -> {
                    startActivity(Intent(this, ManageDeliveryActivity::class.java))
                    true
                }
                else -> false
            }
        }

        orderCardView.setOnClickListener {
            viewModel.currentOrder.value?.let { order ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("order", order)
                }
                startActivityForResult(intent, ORDER_DETAILS_REQUEST_CODE)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentOrder.observe(this) { order ->
            // Update UI with order details
            // You'll need to find and update the appropriate views here
        }

        viewModel.orderCardVisibility.observe(this) { isVisible ->
            orderCardView.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        viewModel.navigateToOrderDetails.observe(this) { order ->
            order?.let {
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("order", it)
                }
                startActivityForResult(intent, ORDER_DETAILS_REQUEST_CODE)
                viewModel.onOrderDetailsNavigated()
            }
        }
    }

    companion object {
        const val ORDER_DETAILS_REQUEST_CODE = 1001
    }
}