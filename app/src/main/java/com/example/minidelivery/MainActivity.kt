package com.example.minidelivery

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    // 뷰 요소들 선언
    private lateinit var acceptButton: Button
    private lateinit var tabLayout: TabLayout
    private lateinit var timeTextView: TextView
    private lateinit var orderSummaryTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var paymentStatusTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var orderCardView: View
    private val processingOrders = mutableListOf<Order>() // 처리 중인 주문 목록
    private val deliveringOrders = mutableListOf<Order>() // 배달 중인 주문 목록
    private var currentOrder: Order? = null // 현재 표시 중인 주문

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 레이아웃 설정

        initViews() // 뷰 초기화
        setupListeners() // 리스너 설정
        loadInitialData() // 초기 데이터 로드

        bottomNavigation.selectedItemId = R.id.nav_home // 홈 메뉴 아이템 선택

    }

    private fun initViews() {
        // 뷰 요소들 초기화
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

    private fun setupListeners() {
        acceptButton.setOnClickListener { handleAcceptButtonClick() } // 주문 접수 버튼 클릭 리스너

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadProcessingOrders() // 처리 중인 주문 로드
                    1 -> loadDeliveringOrders() // 배달 중인 주문 로드
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_history -> {
                    navigateToCompletedOrders() // 완료된 주문으로 이동
                    true
                }
                R.id.nav_delivery -> {
                    navigateToManageDelivery() // 배달 관리로 이동
                    true
                }
                R.id.nav_calendar -> {
                    // 일정 관리로 이동 (미구현)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToCompletedOrders() {
        val intent = Intent(this, CompletedOrdersActivity::class.java)
        ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent)
    }

    private fun navigateToManageDelivery() {
        val intent = Intent(this, ManageDeliveryActivity::class.java)
        ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent)
    }

    private fun loadInitialData() {
        // 초기 주문 데이터 추가
        processingOrders.add(Order(
            id = "1",
            time = "15:00",
            summary = "연어 샐러드 외 1개",
            address = "삼선동 SK뷰 아파트 1301동 1804호",
            paymentStatus = "결제완료",
            price = "21,200원",
            status = OrderStatus.READY
        ))
        loadProcessingOrders() // 처리 중인 주문 로드
    }

    private fun loadProcessingOrders() {
        if (processingOrders.isNotEmpty()) {
            currentOrder = processingOrders.first() // 첫 번째 처리 중인 주문 선택
            updateOrderDetails(currentOrder!!) // 주문 상세 정보 업데이트
            acceptButton.text = "접수"
            acceptButton.setBackgroundColor(ContextCompat.getColor(this, R.color.processing_color))
            orderCardView.visibility = View.VISIBLE
        } else {
            orderCardView.visibility = View.GONE // 처리 중인 주문이 없으면 카드뷰 숨김
        }
    }

    private fun loadDeliveringOrders() {
        if (deliveringOrders.isNotEmpty()) {
            currentOrder = deliveringOrders.first() // 첫 번째 배달 중인 주문 선택
            updateOrderDetails(currentOrder!!) // 주문 상세 정보 업데이트
            acceptButton.text = "완료"
            acceptButton.setBackgroundColor(ContextCompat.getColor(this, R.color.delivering_color))
            orderCardView.visibility = View.VISIBLE
        } else {
            orderCardView.visibility = View.GONE // 배달 중인 주문이 없으면 카드뷰 숨김
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
        // 주문 상세 정보 업데이트
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