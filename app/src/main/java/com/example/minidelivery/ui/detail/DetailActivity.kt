package com.example.minidelivery.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.minidelivery.R
import com.example.minidelivery.data.OrderDetails
import com.example.minidelivery.data.OrderItem
import com.example.minidelivery.data.OrderStatus
import com.example.minidelivery.ui.done.DoneActivity

class DetailActivity : AppCompatActivity() {
    // UI 컴포넌트 선언
    private lateinit var orderSummaryTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var paymentStatusTextView: TextView
    private lateinit var deliveryStatusButton: Button
    private lateinit var storeRequestTextView: TextView
    private lateinit var deliveryRequestTextView: TextView
    private lateinit var orderItemsContainer: LinearLayout

    // ViewModel 선언
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        initViews()
        setupListeners()
        observeViewModel()

        // 주문 상세 정보 로드
        viewModel.loadOrderDetails(intent.getStringExtra("orderId"), intent.getStringExtra("orderStatus"))
    }

    // 뷰 초기화
    private fun initViews() {
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView)
        addressTextView = findViewById(R.id.addressTextView)
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView)
        deliveryStatusButton = findViewById(R.id.deliveryStatusButton)
        storeRequestTextView = findViewById(R.id.storeRequestTextView)
        deliveryRequestTextView = findViewById(R.id.deliveryRequestTextView)
        orderItemsContainer = findViewById(R.id.orderItemsContainer)
    }

    // 리스너 설정
    private fun setupListeners() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            viewModel.onBackButtonClicked(this)
        }

        deliveryStatusButton.setOnClickListener {
            viewModel.updateOrderStatus()
        }
    }

    // ViewModel 관찰
    private fun observeViewModel() {
        viewModel.orderDetails.observe(this) { orderDetails ->
            updateUI(orderDetails)
        }

        viewModel.orderStatus.observe(this) { status ->
            updateDeliveryStatusButton(status)
        }

        viewModel.navigateToCompletedOrders.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                navigateToCompletedOrders()
            }
        }
    }

    // UI 업데이트
    private fun updateUI(orderDetails: OrderDetails) {
        orderSummaryTextView.text = orderDetails.summary
        addressTextView.text = orderDetails.address
        paymentStatusTextView.text = orderDetails.paymentStatus
        storeRequestTextView.text = orderDetails.storeRequest
        deliveryRequestTextView.text = orderDetails.deliveryRequest
        setupOrderItems(orderDetails.items)
    }

    // 배달 상태 버튼 업데이트
    private fun updateDeliveryStatusButton(status: OrderStatus) {
        deliveryStatusButton.text = when (status) {
            OrderStatus.READY -> "접수"
            OrderStatus.COOKING -> "조리중"
            OrderStatus.COOKED -> "조리완료"
            OrderStatus.DELIVERING -> "배달중"
            OrderStatus.COMPLETED -> "배달완료"
        }
    }

    // 주문 항목 설정
    private fun setupOrderItems(items: List<OrderItem>) {
        orderItemsContainer.removeAllViews()
        items.forEach { item ->
            addOrderItem(item.menuName, item.quantity.toString(), item.price)
        }
    }

    // 주문 항목 추가
    private fun addOrderItem(menuName: String, quantity: String, price: String) {
        val itemView = layoutInflater.inflate(R.layout.order_item_row, orderItemsContainer, false)
        itemView.findViewById<TextView>(R.id.menuNameTextView).text = menuName
        itemView.findViewById<TextView>(R.id.quantityTextView).text = quantity
        itemView.findViewById<TextView>(R.id.priceTextView).text = price
        orderItemsContainer.addView(itemView)
    }

    // 완료된 주문 화면으로 이동
    private fun navigateToCompletedOrders() {
        val intent = Intent(this, DoneActivity::class.java).apply {
            putExtra("orderSummary", orderSummaryTextView.text.toString())
            putExtra("address", addressTextView.text.toString())
            putExtra("price", paymentStatusTextView.text.toString())
            putExtra("isNewOrder", true)
        }
        startActivity(intent)
        finish()
    }

}