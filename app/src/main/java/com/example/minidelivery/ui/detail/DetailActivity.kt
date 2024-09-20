package com.example.minidelivery.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minidelivery.R
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderStatus
import com.example.minidelivery.order.OrderAdapter
import com.example.minidelivery.order.OrderItemsAdapter
import com.example.minidelivery.ui.delivery.DeliveryActivity

class DetailActivity : AppCompatActivity() {
    private lateinit var viewModel: DetailViewModel
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderItemsAdapter: OrderItemsAdapter

    private lateinit var orderSummaryTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var paymentStatusTextView: TextView
    private lateinit var deliveryStatusButton: Button
    private lateinit var storeRequestTextView: TextView
    private lateinit var deliveryRequestTextView: TextView
    private lateinit var orderItemsRecyclerView: RecyclerView
    private lateinit var orderListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        initViews()
        setupRecyclerViews()
        setupListeners()
        observeViewModel()

        viewModel.loadOrderDetails(intent.getStringExtra("orderId"))
        viewModel.loadOrderList()
    }

    private fun initViews() {
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView)
        addressTextView = findViewById(R.id.addressTextView)
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView)
        deliveryStatusButton = findViewById(R.id.deliveryStatusButton)
        storeRequestTextView = findViewById(R.id.storeRequestTextView)
        deliveryRequestTextView = findViewById(R.id.deliveryRequestTextView)
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        orderListRecyclerView = findViewById(R.id.orderListRecyclerView)
    }

    private fun setupRecyclerViews() {
        orderItemsAdapter = OrderItemsAdapter()
        orderItemsRecyclerView.adapter = orderItemsAdapter
        orderItemsRecyclerView.layoutManager = LinearLayoutManager(this)

        orderAdapter = OrderAdapter(
            onItemClick = { order -> viewModel.selectOrder(order) },
            onStatusButtonClick = { order -> viewModel.updateOrderStatus(order) }
        )
        orderListRecyclerView.adapter = orderAdapter
        orderListRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener { onBackPressed() }
        deliveryStatusButton.setOnClickListener { viewModel.updateOrderStatus() }
    }

    private fun observeViewModel() {
        viewModel.selectedOrder.observe(this, { order ->
            updateUI(order)
        })

        viewModel.orderList.observe(this, { orders ->
            orderAdapter.submitList(orders)
        })

        viewModel.orderItems.observe(this, { items ->
            orderItemsAdapter.submitList(items)
        })

        viewModel.navigateToDelivery.observe(this, { shouldNavigate ->
            if (shouldNavigate) {
                navigateToDelivery()
            }
        })
    }

    private fun updateUI(order: Order) {
        orderSummaryTextView.text = order.summary
        addressTextView.text = order.address
        paymentStatusTextView.text = order.paymentStatus
        storeRequestTextView.text = order.storeRequest
        deliveryRequestTextView.text = order.deliveryRequest
        updateDeliveryStatusButton(order.status)
    }

    private fun updateDeliveryStatusButton(status: OrderStatus) {
        deliveryStatusButton.text = when (status) {
            OrderStatus.READY -> "접수"
            OrderStatus.COOKING -> "조리중"
            OrderStatus.COOKED -> "조리완료"
            OrderStatus.DELIVERING -> "배달중"
            OrderStatus.COMPLETED -> "배달완료"
        }
    }

    private fun navigateToDelivery() {
        startActivity(Intent(this, DeliveryActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        viewModel.selectedOrder.value?.let { order ->
            setResult(Activity.RESULT_OK, Intent().putExtra("newStatus", order.status.name))
        }
        super.onBackPressed()
    }
}