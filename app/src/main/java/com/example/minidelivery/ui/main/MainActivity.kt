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
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.chip.ChipGroup

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var tabLayout: TabLayout // 탭 레이아웃 선언
    private lateinit var chipGroup: ChipGroup // 칩 그룹 선언
    private lateinit var bottomNavigation: BottomNavigationView // 하단 네비게이션 선언
    private lateinit var orderCardView: CardView // 주문 카드 뷰 선언
    private lateinit var acceptButton: Button // 접수/조리완료 버튼 선언
    private lateinit var timeTextView: TextView // 시간 텍스트뷰 선언
    private lateinit var orderSummaryTextView: TextView // 주문 요약 텍스트뷰 선언
    private lateinit var addressTextView: TextView // 주소 텍스트뷰 선언
    private lateinit var paymentStatusTextView: TextView // 결제 상태 텍스트뷰 선언
    private lateinit var priceTextView: TextView // 가격 텍스트뷰 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 레이아웃 설정

        initViews() // 뷰 초기화
        setupUI() // UI 설정
        observeViewModel() // ViewModel 관찰 설정
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        chipGroup = findViewById(R.id.chipGroup)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        orderCardView = findViewById(R.id.orderCardView)
        acceptButton = findViewById(R.id.acceptButton)
        timeTextView = findViewById(R.id.timeTextView)
        orderSummaryTextView = findViewById(R.id.orderSummaryTextView)
        addressTextView = findViewById(R.id.addressTextView)
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView)
        priceTextView = findViewById(R.id.priceTextView)
    }

    private fun setupUI() {
        tabLayout.addOnTabSelectedListener(viewModel.tabSelectedListener) // 탭 선택 리스너 설정

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipLatest -> viewModel.setSortOrder(SortOrder.LATEST) // 최신순 정렬
                R.id.chipOldest -> viewModel.setSortOrder(SortOrder.OLDEST) // 과거순 정렬
            }
        }

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true
                R.id.nav_delivery -> {
                    startActivity(Intent(this, DeliveryActivity::class.java)) // 배달 관리로 이동
                    true
                }
                R.id.nav_done -> {
                    startActivity(Intent(this, DoneActivity::class.java)) // 완료 내역으로 이동
                    true
                }
                else -> false
            }
        }

        acceptButton.setOnClickListener {
            viewModel.onAcceptButtonClick() // 접수/조리완료 버튼 클릭 처리
        }

        orderCardView.setOnClickListener {
            viewModel.currentOrder.value?.let { order ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("orderId", order.id) // 주문 ID 전달
                    putExtra("orderStatus", order.status.name) // 주문 상태 전달
                }
                startActivityForResult(intent, ORDER_DETAILS_REQUEST_CODE) // 상세 화면으로 이동
            }
        }
    }


    private fun observeViewModel() {
        viewModel.currentOrder.observe(this) { order ->
            order?.let {
                timeTextView.text = it.time
                orderSummaryTextView.text = it.summary
                addressTextView.text = it.address
                paymentStatusTextView.text = it.paymentStatus
                priceTextView.text = it.price
            }
        }

        viewModel.orderCardVisibility.observe(this) { isVisible ->
            orderCardView.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        viewModel.acceptButtonState.observe(this) { state ->
            acceptButton.text = state.text // 버튼 텍스트 업데이트
            acceptButton.setBackgroundColor(getColor(state.colorResId)) // 버튼 색상 업데이트
        }

        viewModel.navigateToDelivery.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, DeliveryActivity::class.java)) // 배달 관리 화면으로 자동 이동
                viewModel.onDeliveryNavigated()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ORDER_DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getStringExtra("newStatus")?.let { newStatus ->
                viewModel.updateOrderStatus(newStatus) // 상세 화면에서 변경된 주문 상태 업데이트
            }
        }
    }

    companion object {
        const val ORDER_DETAILS_REQUEST_CODE = 1001
    }
}