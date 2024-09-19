package com.example.minidelivery.ui.detail

import android.app.Activity
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
import com.example.minidelivery.ui.delivery.DeliveryActivity
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


        initViews() // 뷰 초기화
        setupListeners() // 리스너 설정
        observeViewModel() // ViewModel 관찰 설정

        // 주문 상세 정보 로드
        viewModel.loadOrderDetails(
            intent.getStringExtra("orderId"),
            intent.getStringExtra("orderStatus")
        )
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
            viewModel.onBackButtonClicked(this) // 뒤로가기 버튼 클릭 처리
        }

        deliveryStatusButton.setOnClickListener {
            viewModel.updateOrderStatus() // 주문 상태 업데이트 버튼 클릭 처리
        }
    }

    // ViewModel 관찰
    private fun observeViewModel() {
        viewModel.orderDetails.observe(this) { orderDetails ->
            updateUI(orderDetails) // 주문 상세 정보로 UI 업데이트
        }

        viewModel.orderStatus.observe(this) { status ->
            updateDeliveryStatusButton(status) // 주문 상태에 따라 버튼 업데이트
        }

        viewModel.navigateToDelivery.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                navigateToDelivery() // 배달 화면으로 이동
            }
        }
    }

    // UI 업데이트
    private fun updateUI(orderDetails: OrderDetails) {
        orderSummaryTextView.text = orderDetails.summary // 주문 요약 설정
        addressTextView.text = orderDetails.address // 주소 설정
        paymentStatusTextView.text = orderDetails.paymentStatus // 결제 상태 설정
        storeRequestTextView.text = orderDetails.storeRequest // 가게 요청사항 설정
        deliveryRequestTextView.text = orderDetails.deliveryRequest // 배달 요청사항 설정
        setupOrderItems(orderDetails.items) // 주문 항목 설정
    }

    // 배달 상태 버튼 업데이트
    private fun updateDeliveryStatusButton(status: OrderStatus) {
        deliveryStatusButton.text = when (status) {
            OrderStatus.READY -> "접수"
            OrderStatus.COOKING -> "조리완료"
            OrderStatus.COOKED -> "조리완료"
            OrderStatus.DELIVERING -> "배달중"
            OrderStatus.COMPLETED -> "배달완료"
            else -> "접수"
        }
    }

    // 주문 항목 설정
    private fun setupOrderItems(items: List<OrderItem>) {
        orderItemsContainer.removeAllViews() // 기존 뷰 제거
        items.forEach { item ->
            addOrderItem(item.menuName, item.quantity.toString(), item.price) // 각 주문 항목 추가
        }
    }


    // 주문 항목 추가
    private fun addOrderItem(menuName: String, quantity: String, price: String) {
        val itemView = layoutInflater.inflate(R.layout.order_item_row, orderItemsContainer, false)
        itemView.findViewById<TextView>(R.id.menuNameTextView).text = menuName // 메뉴 이름 설정
        itemView.findViewById<TextView>(R.id.quantityTextView).text = quantity // 수량 설정
        itemView.findViewById<TextView>(R.id.priceTextView).text = price // 가격 설정
        orderItemsContainer.addView(itemView) // 컨테이너에 항목 뷰 추가
    }

    // 배달 화면으로 이동
    private fun navigateToDelivery() {
        val intent = Intent(this, DeliveryActivity::class.java)
        startActivity(intent) // 배달 관리 화면으로 이동
        finish() // 현재 액티비티 종료
    }

    // 액티비티 결과 설정 및 종료
    fun finishWithResult(newStatus: OrderStatus) {
        val resultIntent = Intent().putExtra("newStatus", newStatus.name)
        setResult(Activity.RESULT_OK, resultIntent) // 결과 설정
        finish() // 액티비티 종료
    }

}