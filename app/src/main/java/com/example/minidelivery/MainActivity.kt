package com.example.minidelivery

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var acceptButton: Button
    private lateinit var tabLayout: TabLayout
    private lateinit var timeTextView: TextView
    private lateinit var orderSummaryTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var paymentStatusTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var orderCardView: View
    private val processingOrders = mutableListOf<Order>()
    private val deliveringOrders = mutableListOf<Order>()
    private var currentOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        loadInitialData()

        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun initViews() {
        acceptButton = findViewById(R.id.acceptButton)
        tabLayout = findViewById(R.id.tabLayout)
        timeTextView = findViewById(R.id.timeTextView)
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView)
        addressTextView = findViewById(R.id.addressTextView)
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView)
        priceTextView = findViewById(R.id.priceTextView)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        orderCardView = findViewById(R.id.orderCardView)
    }

    private fun navigateToCompletedOrders() {
        val intent = Intent(this, CompletedOrdersActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun setupListeners() {
        acceptButton.setOnClickListener { handleAcceptButtonClick() }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadProcessingOrders()
                    1 -> loadDeliveringOrders()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Handle home navigation
                    true
                }
                R.id.nav_history -> {
                    navigateToCompletedOrders()
                    true
                }
                R.id.nav_sales -> {
                    // Handle sales navigation
                    true
                }
                R.id.nav_calendar -> {
                    // Handle calendar navigation
                    true
                }
                else -> false
            }
        }
    }


    private fun loadInitialData() {
        processingOrders.add(Order(
            id = "1",
            time = "15:00",
            summary = "연어 샐러드 외 1개",
            address = "삼선동 SK뷰 아파트 1301동 1804호",
            paymentStatus = "결제완료",
            price = "21,200원",
            status = OrderStatus.READY
        ))
        loadProcessingOrders()
    }

    private fun loadProcessingOrders() {
        if (processingOrders.isNotEmpty()) {
            currentOrder = processingOrders.first()
            updateOrderDetails(currentOrder!!)
            acceptButton.text = "접수"
            acceptButton.setBackgroundColor(ContextCompat.getColor(this, R.color.processing_color))
            orderCardView.visibility = View.VISIBLE
        } else {
            orderCardView.visibility = View.GONE
        }
    }

    private fun loadDeliveringOrders() {
        if (deliveringOrders.isNotEmpty()) {
            currentOrder = deliveringOrders.first()
            updateOrderDetails(currentOrder!!)
            acceptButton.text = "완료"
            acceptButton.setBackgroundColor(ContextCompat.getColor(this, R.color.delivering_color))
            orderCardView.visibility = View.VISIBLE
        } else {
            orderCardView.visibility = View.GONE
        }
    }

    private fun handleAcceptButtonClick() {
        currentOrder?.let { order ->
            when (order.status) {
                OrderStatus.READY -> {
                    order.status = OrderStatus.COOKING
                    updateOrderStatus(OrderStatus.COOKING)
                }
                OrderStatus.COOKING -> {
                    order.status = OrderStatus.DELIVERING
                    processingOrders.remove(order)
                    deliveringOrders.add(order)
                    loadProcessingOrders()
                }
                OrderStatus.DELIVERING -> {
                    order.status = OrderStatus.COMPLETED
                    deliveringOrders.remove(order)
                    loadDeliveringOrders()
                }
                else -> {}
            }
        }
    }

    private fun updateOrderDetails(order: Order) {
        timeTextView.text = order.time
        orderSummaryTextView.text = order.summary
        addressTextView.text = order.address
        paymentStatusTextView.text = order.paymentStatus
        priceTextView.text = order.price
    }

    private fun updateOrderStatus(newStatus: OrderStatus) {
        when (newStatus) {
            OrderStatus.COOKING -> {
                acceptButton.text = "조리완료"
            }
            OrderStatus.DELIVERING -> {
                acceptButton.text = "배달중"
            }
            OrderStatus.COMPLETED -> {
                acceptButton.text = "완료"
                orderCardView.visibility = View.GONE
            }
            else -> {}
        }
    }
}