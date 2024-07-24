package com.example.minidelivery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup

class CompletedOrdersActivity : AppCompatActivity() {
    private lateinit var chipGroup: ChipGroup
    private lateinit var orderSummaryTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var completedButton: Button
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_orders)

        initViews()
        setupListeners()
        loadCompletedOrders()

        bottomNavigation.selectedItemId = R.id.nav_history

        // 뒤로 가기 동작 처리
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHome()
            }
        })
    }

    private fun initViews() {
        chipGroup = findViewById(R.id.chipGroup)
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView)
        addressTextView = findViewById(R.id.addressTextView)
        priceTextView = findViewById(R.id.priceTextView)
        completedButton = findViewById(R.id.completedButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun setupListeners() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navigateToHome()
                    true
                }
                R.id.nav_history -> {
                    // Already on this screen
                    true
                }
                R.id.nav_sales -> {
                    // TODO: Implement sales activity
                    true
                }
                R.id.nav_calendar -> {
                    // TODO: Implement calendar activity
                    true
                }
                else -> false
            }
        }
    }

    private fun loadCompletedOrders() {
        // This is where you would load the completed orders from a database or API
        // For now, we'll just set some example data
        orderSummaryTextView.setText(getString(R.string.order_summary))
        addressTextView.setText(getString(R.string.order_address))
        priceTextView.setText(getString(R.string.order_price, "21,200"))
    }
}