package com.example.minidelivery.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.example.minidelivery.ui.completedorders.CompletedOrdersActivity
import com.example.minidelivery.ui.managedelivery.ManageDeliveryActivity
import com.example.minidelivery.Order
import com.example.minidelivery.ui.orderdetails.OrderDetailsActivity
import com.example.minidelivery.OrderStatus
import com.example.minidelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class RequestData(
    val orderTime: String,
    val foodName: String,
    val address: String,
    val amount: String
)

data class ResponseData(
    val id: Int,
    val orderTime: String,
    val foodName: String,
    val address: String,
    val amount: String
)

interface ApiService {
    @POST("/api/orders")
    fun postData(@Body requestData: RequestData): Call<ResponseData>
}

class MainActivity : AppCompatActivity() {
    private lateinit var acceptButton: Button // 접수 버튼
    private lateinit var tabLayout: TabLayout // 탭 레이아웃
    private lateinit var timeTextView: TextView // 시간 표시 텍스트뷰
    private lateinit var orderSummaryTextView: TextView // 주문 요약 텍스트뷰
    private lateinit var addressTextView: TextView // 주소 텍스트뷰
    private lateinit var paymentStatusTextView: TextView // 결제 상태 텍스트뷰
    private lateinit var priceTextView: TextView // 가격 텍스트뷰
    private lateinit var bottomNavigation: BottomNavigationView // 하단 네비게이션 뷰
    private lateinit var orderCardView: View // 주문 카드 뷰
    private lateinit var apiService: ApiService // API 서비스 (Server)
    private val processingOrders = mutableListOf<Order>() // 처리 중인 주문 목록
    private val deliveringOrders = mutableListOf<Order>() // 배달 중인 주문 목록
    private var currentOrder: Order? = null // 현재 표시 중인 주문
    private val ORDER_DETAILS_REQUEST_CODE = 1001 // 주문 상세 요청 코드
    private var receivedOrderData: ResponseData? = null // 데이터를 전체적으로 관리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 부모 클래스 onCreate 호출
        setContentView(R.layout.activity_main) // 레이아웃 설정

        initViews() // 뷰 초기화
        setupListeners() // 리스너 설정
        loadInitialData() // 초기 데이터 로드

        bottomNavigation.selectedItemId = R.id.nav_home // 홈 메뉴 아이템 선택


        // Server (건우)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.137.222:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        val requestData = RequestData(
            orderTime = "15:00",
            foodName = "로티세리 치킨 샐러드",
            address = "경기도 부천시 상오정로 7번길 43",
            amount = "13000"
        )
        sendPostRequest(requestData)
    }

    private fun sendPostRequest(requestData: RequestData) {
        apiService.postData(requestData).enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    // 성공적으로 응답을 받았을 때 처리
                    receivedOrderData = response.body()
                    Log.d("ApiServiceCall", "Response: $receivedOrderData")
                } else {
                    // 응답이 실패했을 때 처리
                    Log.d("ApiServiceCall", "Request failed with response code: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                // 요청이 실패했을 때 처리
                t.printStackTrace()
                Log.d("MainActivity", "Request failed: ${t.message}")
            }
        })
    }

    private fun initViews() {
        acceptButton = findViewById(R.id.acceptButton) // 접수 버튼 찾기
        tabLayout = findViewById(R.id.tabLayout) // 탭 레이아웃 찾기
        timeTextView = findViewById(R.id.timeTextView) // 시간 텍스트뷰 찾기
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView) // 주문 요약 텍스트뷰 찾기
        addressTextView = findViewById(R.id.addressTextView) // 주소 텍스트뷰 찾기
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView) // 결제 상태 텍스트뷰 찾기
        priceTextView = findViewById(R.id.priceTextView) // 가격 텍스트뷰 찾기
        bottomNavigation = findViewById(R.id.bottomNavigation) // 하단 네비게이션 찾기
        orderCardView = findViewById(R.id.orderCardView) // 주문 카드 뷰 찾기
    }

    private fun setupListeners() {
        acceptButton.setOnClickListener { handleAcceptButtonClick() } // 접수 버튼 클릭 리스너 설정

        orderCardView.setOnClickListener {
            val intent = Intent(this, OrderDetailsActivity::class.java) // 주문 상세 액티비티로 이동할 인텐트 생성
            currentOrder?.let {
                intent.putExtra("orderId", it.id) // 주문 ID 전달
                intent.putExtra("orderStatus", it.status.name) // 주문 상태 전달
            }
            startActivityForResult(intent, ORDER_DETAILS_REQUEST_CODE) // 결과를 기대하며 액티비티 시작
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadProcessingOrders() // 처리 중인 주문 로드
                    1 -> loadDeliveringOrders() // 배달 중인 주문 로드
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {} // 사용하지 않음
            override fun onTabReselected(tab: TabLayout.Tab?) {} // 사용하지 않음
        })

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true // 홈 메뉴 선택 시
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // 부모 메서드 호출
        if (requestCode == ORDER_DETAILS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newStatus = data?.getStringExtra("newStatus")?.let { OrderStatus.valueOf(it) } // 새 상태 가져오기
            currentOrder?.let { order ->
                if (newStatus != null && newStatus != order.status) {
                    order.status = newStatus // 주문 상태 업데이트
                    when (newStatus) {
                        OrderStatus.COOKING -> updateOrderStatus(OrderStatus.COOKING) // 조리 중 상태로 업데이트
                        OrderStatus.DELIVERING -> {
                            processingOrders.remove(order) // 처리 중 목록에서 제거
                            deliveringOrders.add(order) // 배달 중 목록에 추가
                            loadProcessingOrders() // 처리 중 주문 다시 로드
                        }
                        OrderStatus.COMPLETED -> {
                            deliveringOrders.remove(order) // 배달 중 목록에서 제거
                            loadDeliveringOrders() // 배달 중 주문 다시 로드
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    // 배달완료 페이지로 이동
    private fun navigateToCompletedOrders() {
        val intent = Intent(this, CompletedOrdersActivity::class.java)
        intent.putExtra("isNewOrder", false) // 새 주문이 아님을 나타냄
        ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent)
    }

    // 배달관리 페이지로 이동
    private fun navigateToManageDelivery() {
        val intent = Intent(this, ManageDeliveryActivity::class.java) // 인텐트 생성
        ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out) // 애니메이션 설정
        startActivity(intent) // 액티비티 시작
    }
    private fun loadInitialData() {
        // 초기 주문 데이터 추가
        processingOrders.add(
            Order(
            id = "1",
            time = "15:00",
            summary = "연어 샐러드 외 1개",
            address = "삼선동 SK뷰 아파트 1301동 1804호",
            paymentStatus = "결제완료",
            price = "21,200원",
            status = OrderStatus.READY
        )
        )
        loadProcessingOrders() // 처리 중인 주문 로드
    }

    // 처리중인 주문 로드
    private fun loadProcessingOrders() {
        if (processingOrders.isNotEmpty()) {
            currentOrder = processingOrders.first() // 첫 번째 처리 중인 주문 선택
            updateOrderDetails(currentOrder!!) // 주문 상세 정보 업데이트
            acceptButton.text = "접수" // 버튼 텍스트 변경
            acceptButton.setBackgroundColor(ContextCompat.getColor(this, R.color.processing_color)) // 버튼 색상 변경
            orderCardView.visibility = View.VISIBLE // 주문 카드 보이기
        } else {
            orderCardView.visibility = View.GONE // 처리 중인 주문이 없으면 카드뷰 숨김
        }
    }

    // 배달중인 주문 로드
    private fun loadDeliveringOrders() {
        if (deliveringOrders.isNotEmpty()) {
            currentOrder = deliveringOrders.first() // 첫 번째 배달 중인 주문 선택
            updateOrderDetails(currentOrder!!) // 주문 상세 정보 업데이트
            acceptButton.text = "완료" // 버튼 텍스트 변경
            acceptButton.setBackgroundColor(ContextCompat.getColor(this, R.color.delivering_color)) // 버튼 색상 변경
            orderCardView.visibility = View.VISIBLE // 주문 카드 보이기
        } else {
            orderCardView.visibility = View.GONE // 배달 중인 주문이 없으면 카드뷰 숨김
        }
    }

    private fun handleAcceptButtonClick() {
        currentOrder?.let { order ->
            when (order.status) {
                OrderStatus.READY -> {
                    order.status = OrderStatus.COOKING // 상태를 조리 중으로 변경
                    updateOrderStatus(OrderStatus.COOKING) // 주문 상태 업데이트
                }
                OrderStatus.COOKING -> {
                    order.status = OrderStatus.DELIVERING // 상태를 배달 중으로 변경
                    processingOrders.remove(order) // 처리 중 목록에서 제거
                    deliveringOrders.add(order) // 배달 중 목록에 추가
                    loadProcessingOrders() // 처리 중 주문 다시 로드
                }
                OrderStatus.DELIVERING -> {
                    order.status = OrderStatus.COMPLETED // 상태를 완료로 변경
                    deliveringOrders.remove(order) // 배달 중 목록에서 제거
                    loadDeliveringOrders() // 배달 중 주문 다시 로드
                }
                else -> {}
            }
        }
    }

    // 주문 상세 정보 업데이트
    private fun updateOrderDetails(order: Order) {
        timeTextView.text = order.time // 시간 설정
        orderSummaryTextView.text = order.summary // 주문 요약 설정
        addressTextView.text = order.address // 주소 설정
        paymentStatusTextView.text = order.paymentStatus // 결제 상태 설정
        priceTextView.text = order.price // 가격 설정
    }

    // 주문 현황 정보 업데이트
    private fun updateOrderStatus(newStatus: OrderStatus) {
        when (newStatus) {
            OrderStatus.COOKING -> {
                acceptButton.text = "조리완료" // 버튼 텍스트 변경
            }
            OrderStatus.DELIVERING -> {
                acceptButton.text = "배달중" // 버튼 텍스트 변경
            }
            OrderStatus.COMPLETED -> {
                acceptButton.text = "완료" // 버튼 텍스트 변경
                orderCardView.visibility = View.GONE // 주문 카드 숨기기
            }
            else -> {}
        }
    }
}