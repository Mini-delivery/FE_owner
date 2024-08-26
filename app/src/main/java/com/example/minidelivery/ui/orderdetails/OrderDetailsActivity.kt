package com.example.minidelivery.ui.orderdetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.minidelivery.OrderStatus
import com.example.minidelivery.R
import com.example.minidelivery.ui.completedorders.CompletedOrdersActivity

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var orderSummaryTextView: TextView // 주문 요약 텍스트뷰
    private lateinit var addressTextView: TextView // 주소 텍스트뷰
    private lateinit var paymentStatusTextView: TextView // 결제 상태 텍스트뷰
    private lateinit var deliveryStatusButton: Button // 배달 상태 버튼
    private lateinit var storeRequestTextView: TextView // 가게 요청사항 텍스트뷰
    private lateinit var deliveryRequestTextView: TextView // 배달 요청사항 텍스트뷰
    private lateinit var orderItemsContainer: LinearLayout // 주문 항목 컨테이너

    private var orderId: String? = null // 주문 ID
    private var orderStatus: OrderStatus = OrderStatus.READY // 주문 상태

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 부모 클래스 onCreate 호출
        setContentView(R.layout.activity_order_details) // 레이아웃 설정

        initViews() // 뷰 초기화
        setupListeners() // 리스너 설정
        loadOrderDetails() // 주문 상세 정보 로드
    }

    private fun initViews() {
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView) // 주문 요약 텍스트뷰 찾기
        addressTextView = findViewById(R.id.addressTextView) // 주소 텍스트뷰 찾기
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView) // 결제 상태 텍스트뷰 찾기
        deliveryStatusButton = findViewById(R.id.deliveryStatusButton) // 배달 상태 버튼 찾기
        storeRequestTextView = findViewById(R.id.storeRequestTextView) // 가게 요청사항 텍스트뷰 찾기
        deliveryRequestTextView = findViewById(R.id.deliveryRequestTextView) // 배달 요청사항 텍스트뷰 찾기
        orderItemsContainer = findViewById(R.id.orderItemsContainer) // 주문 항목 컨테이너 찾기
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra("newStatus", orderStatus.name)) // 결과 설정
            finish() // 액티비티 종료
        }

        deliveryStatusButton.setOnClickListener {
            updateOrderStatus() // 주문 상태 업데이트
        }
    }

    private fun loadOrderDetails() {
        orderId = intent.getStringExtra("orderId") // 주문 ID 가져오기
        orderStatus = OrderStatus.valueOf(intent.getStringExtra("orderStatus") ?: OrderStatus.READY.name) // 주문 상태 가져오기

        orderSummaryTextView.text = "연어 샐러드 외 1개" // 주문 요약 설정
        addressTextView.text = "삼선동 SK뷰 아파트 1301동 1804호" // 주소 설정
        paymentStatusTextView.text = "결제완료 21,200원" // 결제 상태 설정
        storeRequestTextView.text = "리뷰이벤트 참여합니다~" // 가게 요청사항 설정
        deliveryRequestTextView.text = "문 앞에 놓아주세요!" // 배달 요청사항 설정

        updateDeliveryStatusButton() // 배달 상태 버튼 업데이트
        setupOrderItems() // 주문 항목 설정
    }

    private fun updateDeliveryStatusButton() {
        deliveryStatusButton.text = when (orderStatus) {
            OrderStatus.READY -> "접수" // 준비 상태일 때
            OrderStatus.COOKING -> "조리완료" // 조리 중 상태일 때
            OrderStatus.DELIVERING -> "배달완료" // 배달 중 상태일 때
            OrderStatus.COMPLETED -> "완료" // 완료 상태일 때
        }
    }

    private fun updateOrderStatus() {
        orderStatus = when (orderStatus) {
            OrderStatus.READY -> OrderStatus.COOKING
            OrderStatus.COOKING -> OrderStatus.DELIVERING
            OrderStatus.DELIVERING -> OrderStatus.COMPLETED
            OrderStatus.COMPLETED -> OrderStatus.COMPLETED
        }
        updateDeliveryStatusButton()

        setResult(Activity.RESULT_OK, Intent().putExtra("newStatus", orderStatus.name))

        if (orderStatus == OrderStatus.COMPLETED) {
            val intent = Intent(this, CompletedOrdersActivity::class.java)
            intent.putExtra("orderSummary", orderSummaryTextView.text.toString()) // 주문 요약 전달
            intent.putExtra("address", addressTextView.text.toString()) // 주소 전달
            intent.putExtra("price", paymentStatusTextView.text.toString()) // 가격 전달
            intent.putExtra("isNewOrder", true) // 새 주문임을 나타내는 플래그 추가
            startActivity(intent)
            finish()
        }
    }
    private fun setupOrderItems() {
        addOrderItem("연어 샐러드", "1", "12,000원") // 첫 번째 주문 항목 추가
        addOrderItem("우삼겹 샐러드", "1", "10,200원") // 두 번째 주문 항목 추가
    }

    private fun addOrderItem(menuName: String, quantity: String, price: String) {
        val itemView = layoutInflater.inflate(R.layout.order_item_row, orderItemsContainer, false) // 주문 항목 뷰 생성
        itemView.findViewById<TextView>(R.id.menuNameTextView).text = menuName // 메뉴 이름 설정
        itemView.findViewById<TextView>(R.id.quantityTextView).text = quantity // 수량 설정
        itemView.findViewById<TextView>(R.id.priceTextView).text = price // 가격 설정
        orderItemsContainer.addView(itemView) // 컨테이너에 뷰 추가
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent().putExtra("newStatus", orderStatus.name)) // 결과 설정
        super.onBackPressed() // 기본 뒤로가기 동작 수행
    }
}