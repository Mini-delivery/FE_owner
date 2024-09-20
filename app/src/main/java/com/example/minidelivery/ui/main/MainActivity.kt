package com.example.minidelivery.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.minidelivery.R
import com.example.minidelivery.ui.done.DoneActivity
import com.example.minidelivery.ui.delivery.DeliveryActivity
import com.example.minidelivery.ui.detail.DetailActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minidelivery.data.Order
import com.example.minidelivery.data.OrderStatus
import com.example.minidelivery.order.OrderAdapter
import com.google.android.material.chip.ChipGroup

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels() // ViewModel 초기화

    private lateinit var tabLayout: TabLayout // 탭 레이아웃 선언
    private lateinit var chipGroup: ChipGroup // 칩 그룹 선언
    private lateinit var bottomNavigation: BottomNavigationView // 하단 네비게이션 선언
    private lateinit var orderRecyclerView: RecyclerView // RecyclerView 선언
    private lateinit var orderAdapter: OrderAdapter // Adapter 선언

    // 액티비티 생성 시 호출되는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 레이아웃 설정

        initViews() // 뷰 초기화
        setupUI() // UI 설정
        observeViewModel() // ViewModel 관찰 설정
        viewModel.loadOrders() // 초기 데이터 로드
    }

    // 뷰 초기화 함수
    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        chipGroup = findViewById(R.id.chipGroup)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        orderRecyclerView = findViewById(R.id.orderRecyclerView)
    }

    // UI 설정 함수
    private fun setupUI() {
        setupRecyclerView() // RecyclerView 설정
        setupTabLayout() // 탭 레이아웃 설정
        setupChipGroup() // 칩 그룹 설정
        setupBottomNavigation() // 하단 네비게이션 설정
    }

    // RecyclerView 설정 함수
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter( // OrderAdapter 초기화
            this::onOrderItemClick,
            this::onStatusButtonClick
        )
        orderRecyclerView.adapter = orderAdapter // 어댑터 설정
        orderRecyclerView.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저 설정
        orderRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL)) // 구분선 추가
    }

    // 탭 레이아웃 설정 함수
    private fun setupTabLayout() {
        // 탭 선택 리스너 설정
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.onTabSelected(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // 칩 그룹 설정 함수
    private fun setupChipGroup() {
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipLatest -> viewModel.setSortOrder(SortOrder.LATEST) // 최신순 정렬
                R.id.chipOldest -> viewModel.setSortOrder(SortOrder.OLDEST) // 과거순 정렬
            }
        }
    }

    // 하단 네비게이션 설정 함수
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true // 홈 화면
                R.id.nav_delivery -> {
                    startActivity(Intent(this, DeliveryActivity::class.java)) // 배달 관리 화면으로 이동
                    true
                }
                R.id.nav_done -> {
                    startActivity(Intent(this, DoneActivity::class.java)) // 완료 내역 화면으로 이동
                    true
                }
                else -> false
            }
        }
    }

    // 주문 아이템 클릭 리스너
    private fun onOrderItemClick(order: Order) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("orderId", order.id) // 주문 ID 전달
            putExtra("orderStatus", order.status.name) // 주문 상태 전달
        }
        startActivityForResult(intent, ORDER_DETAILS_REQUEST_CODE) // 상세 화면으로 이동
    }

    // 상태 버튼 클릭 리스너
    private fun onStatusButtonClick(order: Order) {
        viewModel.updateOrderStatus(order) // 주문 상태 업데이트
    }

    // ViewModel 관찰 함수
    private fun observeViewModel() {
        viewModel.orders.observe(this) { orders ->
            orderAdapter.updateOrders(orders) // 주문 목록 업데이트
        }

        viewModel.navigateToDelivery.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, DeliveryActivity::class.java)) // 배달 관리 화면으로 자동 이동
                viewModel.onDeliveryNavigated()
            }
        }
    }

    // DetailActivity에서 돌아왔을 때 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ORDER_DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
            viewModel.loadOrders() // 주문 목록 새로고침
        }
    }

    companion object {
        const val ORDER_DETAILS_REQUEST_CODE = 1001  // DetailActivity 요청 코드
    }
}